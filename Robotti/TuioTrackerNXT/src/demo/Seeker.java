package demo;

import java.io.File;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.*;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.NavPathController;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.NodePathFinder;
import lejos.robotics.pathfinding.PathFinder;
import lejos.util.Delay;

/**
 * Testing of shortest path finding and leJOS navigation features using a TuioPoseProvider that
 * communicates with a PC, receiving X/Y-coordinates and the angle of fiduciary markers seen by a 
 * camera. The robot has a fiduciary marker placed on top of itself so it can know its location
 * and heading.
 * 
 * The robot uses two touch sensors for convenience because the fiducial on top of my robot was just
 * printed on a paper attached to a piece of cardboard that two pins hold in place and the buttons
 * are out of reach. When the program starts, it makes a repeating sound (you can stop this by pressing
 * recalcButton).
 * 
 * On the NXT, when creating a TuioPoseProvider object it starts listening for the PC to contact it.
 * Once connection has been established by running TuioTransmitter on the PC, TuioTransmitter listens
 * for TUIO messages in UDP port 3333 on the PC. ReacTIVision and other TUIO output enabled programs can 
 * provide these messages (fiduciary marker information, but with slight modification also other types).
 * 
 * After NXT and PC are connected, put your camera above the robot so it has an overhead view of the
 * area and make sure it's near enough so that it can reliably identify the individual markers when
 * the robot is moved around the visible area. You should ideally place the camera so it can look directly
 * down for best results. Using a camera with a big resolution should allow you to place it higher
 * (mine seems to just work with 640x480 with reacTIVision) and to see a bigger area. You could also
 * use a fisheye lens to see more, reacTIVision can correct the distortion.
 * 
 * After the camera is fixed above the robot, run reacTIVision and invert the Y axis and angle (press 'i'
 * and use arrow keys).
 * 
 * Now the robot should be making a repeating noise. Hold recalcButton for a short while so that it stops
 * repeating the sound and says "command?". Adjust map geometry with fiducials so at least one complete
 * wall is seen (fiducials 2&3 and 4&5) and that the robot and its target are also visible to the camera
 * and their fiducials are detected (0 for robot, 1 for target). Then press recalcButton again (making sure
 * your body doesn't get in the way of the camera) and the robot will calculate the map and shout "yeah!"
 * if successful. Now you can press searchButton and if the robot can get to the target, it should say 
 * "commencing..." and start driving towards the target using shortest path and shout "yeah!" again when
 * it reaches it.
 * 
 * If the robot refuses to move, it says either "huh?" or "your move, creep". "Huh?" usually means that at
 * their current locations, for some reason, either it can't see a complete wall or itself or the target.
 * If it says "your move, creep", it means that it failed to calculate a path, most likely due to not having
 * room to move with the chosen clearance. Try making the map less crowded or adjust the robot or wall
 * positions slightly. (And check that your reacTIVision is running and the PC is transmitting coordinates.)
 * 
 * You can move the target and the robot around at will and press the searchButton to make it find the target
 * again, but if you touch the map geometry such as walls, you need to press the recalcButton to update the 
 * map.
 * 
 * @author Jouko Strömmer
 * 
 */
public class Seeker {

	// Sound files
	private static File huh = new File("huh.wav");
	private static File command = new File("cmd.wav");
	private static File commencing = new File("com.wav");
	private static File yeah = new File("ye.wav");
	private static File bling = new File("st1.wav");
	private static File yourmove = new File("cop.wav");

	// Area boundaries: area will be defined as (0,0)-(xscale,yscale)
	// This is a square right now for no good reason.
	private final static int xscale = 100;
	private final static int yscale = 100;

	// Fiducial ID of the robot
	private final static int robot_id = 0;

	// Grid spacing and clearance for generating FourWayGridMesh
	private final static int gridspace = 10;
	private final static int clearance = 10;

	// Convenience
	private static void soundeffect (File snd) {
		Sound.playSample(snd);
		while(Sound.getTime() > 0);
	}

	// Convenience: play a sample and wait a bit after it.
	private static void soundeffect (File snd, int msdelay) {
		soundeffect(snd);
		Delay.msDelay(msdelay);
	}


	// Main program: user can modify the map with the fiducials and make the robot find its
	// target in the surroundings using shortest path.
	public static void main(String[] args) {
		// Define pilot
		DifferentialPilot pilot = new DifferentialPilot(5.6f, 17f, Motor.A,	Motor.B);

		// Create buttons for interaction
		TouchSensor searchButton = new TouchSensor(SensorPort.S1);
		TouchSensor recalcButton = new TouchSensor(SensorPort.S2);

		// Just for UI reasons: when the last command failed, we don't play the "command"-sample
		// again right after a fail sample ("huh?" or "your move, creep")
		boolean failed = false;

		// Pose object for the target we want to seek
		Pose target;

		// Set a decent speed
		pilot.setTravelSpeed(25);
		pilot.setRotateSpeed(90);

		// Start TUIO listener, which will contact the PC for providing poses of the visible markers
		// (on PC, run "TuioTransmitter" to contact the robot and transmit the values continuously).
		// TuioPoseProvider will use this listener.
		TuioListener listener = new TuioListener();

		// Make the listener poll the PC for values every 200 ms.
		listener.setPollDelay(200);

		// Create a PoseProvider for navigation (here it wants a target_id, but it's not used
		// anymore in favor of tuiopose.getSymbolPose(int)). (TODO)
		TuioPoseProvider tuiopose = new TuioPoseProvider(listener, robot_id, xscale, yscale);


		// Create a LineMap to store our geometry (walls and obstacles) in and create the bounds for it.
		LineMap myMap;
		lejos.geom.Rectangle bounds = new Rectangle(0, 0, xscale, yscale);

		// Use A* search:
		AstarSearchAlgorithm alg = new AstarSearchAlgorithm();

		// Create a rudimentary map (should be removed really, was for testing and will be replaced
		// by pressing the recalcButton)
		Line[] lines = new Line[1];
		lines[0] = new Line(40f, 49f, 50f, 50f);
		myMap = new LineMap(lines, bounds);

		// Signal standby
		while(!recalcButton.isPressed()) {
			soundeffect(bling, 100);
		}

		// Use a regular grid of node points that depict the traversable nodes in the map.
		// (this should really have some visualization too...)
		FourWayGridMesh grid = new FourWayGridMesh(myMap, gridspace, clearance);

		// Give the A* search alg and grid to the PathFinder:
		PathFinder pf = new NodePathFinder(alg, grid);

		// Create a NavPathController to utilize all the above: pilot to control the robot,
		// tuiopose for getting the robot's pose, and the pathfinder pf for navigation on the map.
		NavPathController nav = new NavPathController(pilot, tuiopose, pf);

		// Run program forever.
		while (true) {
			// Skip playing ready voice if last command failed.
			if(!failed) soundeffect(command, 300);
			// Wait for input.
			while (!searchButton.isPressed() && !recalcButton.isPressed());

			// Update the map with the current state by querying the visible fiducials. Walls
			// are formed by fiducials 2&3 and 4&5. An imaginary line will be drawn between the
			// each pair and is used in LineMap.
			if(recalcButton.isPressed()) {
				Pose wallAbegin, wallAend, wallBbegin, wallBend, rockA, rockB;
				wallAbegin = tuiopose.getSymbolPose(2);
				wallAend = tuiopose.getSymbolPose(3);
				wallBbegin = tuiopose.getSymbolPose(4);
				wallBend = tuiopose.getSymbolPose(5);
				rockA = tuiopose.getSymbolPose(6);
				rockB = tuiopose.getSymbolPose(7);


				// To be valid, at least one complete wall needs to be in the map.
				boolean validwalls = (wallAbegin != null && wallAend != null) || (wallBbegin != null && wallBend != null);

				// Construct map, walls first... (TODO: make it handle an arbitrary number of walls)
				// First it checks if there are complete walls, then looks for rocks and plays a signal for
				// each rock if they're not found (they're optional). For the robot to accept the map,
				// it needs at least one complete wall (checked above).
				if(validwalls) {
					int index = 0;
					Line wallAline = null, wallBline = null, rockAline = null, rockBline = null;
					// make wall A
					if(wallAbegin != null && wallAend != null) {
						wallAline = new Line(wallAbegin.getX(), wallAbegin.getY(), wallAend.getX(), wallAend.getY());
						index++;
					}
					// make wall B
					if(wallBbegin != null && wallBend != null) {
						wallBline = new Line(wallBbegin.getX(), wallBbegin.getY(), wallBend.getX(), wallBend.getY());
						index++;
					}

					// The rocks are pretty stupid.
					// make rock A
					if(rockA != null) {
						rockAline = new Line(rockA.getX()-5, rockA.getY(), rockA.getX()+5, rockA.getY());
						index++;
					} else soundeffect(bling);
					// make rock A
					if(rockB != null) {
						rockBline = new Line(rockB.getX()-5, rockB.getY(), rockB.getX()+5, rockB.getY());
						index++;
					} else soundeffect(bling);

					// Create a bunch of lines for geometry.
					lines = new Line[index];
					int i = 0;
					if(wallAline != null) {
						lines[i] = wallAline;
						i++;
					}
					if(wallBline != null) {
						lines[i] = wallBline;
						i++;
					}
					if(rockAline != null) {
						lines[i] = rockAline;
						i++;
					}
					if(rockBline != null) {
						lines[i] = rockBline;
						i++;
					}

					// Make a LineMap out of them.
					myMap = new LineMap(lines, bounds);
					// Set the new map for the grid.
					grid.setMap(myMap);
					// Regenerate the grid based on new map.
					grid.regenerate();

					soundeffect(yeah);
					System.out.println(""+lines.length+" "+myMap.toString());

				} else soundeffect(huh, 300);

				// If user wants the robot to search, verify first that we see both the robot and the
				// target. Then use the navigator to go the target Waypoint. When nav.goto() fails to
				// get a valid route it throws a NullPointerException which is catched and an awkward
				// sound is played.
			} else if (searchButton.isPressed()) {
				target = tuiopose.getSymbolPose(1);
				if (target == null || tuiopose.getSymbolPose(0) == null) {
					failed = true;
					soundeffect(huh, 300);
				}
				else {
					soundeffect(commencing, 300);
					try {
						nav.goTo(new WayPoint(target));
						soundeffect(yeah, 500);
						failed = false;

					} catch (NullPointerException e) {
						soundeffect(yourmove, 300);
						failed = true;
					}
				}				
			}
		}
	}
}