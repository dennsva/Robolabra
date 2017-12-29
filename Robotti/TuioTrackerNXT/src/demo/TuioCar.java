package demo;

import lejos.nxt.*;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

/**
 * An example program where a car is controlled by rotating a cube. Each of the
 * faces have been marked with fiducials and the PC is running TuioTransmitter
 * (TuioTrackerPC project) and reacTIVision software with a camera. reacTIVision
 * will detect the markers and send TUIO messages to TuioTrack on PC, which
 * maintains a hashtable of the visible objects and listens to NXT's requests
 * for getting the current data. When NXT sends a request, relevant information
 * (symbol ID, position and angle) is sent over Bluetooth to NXT.
 * 
 * It only cares about the first fiducial seen, so showing multiple at the same
 * time may cause unexpected behavior.
 * 
 * The order of the markers is pretty arbitrary and depends on where and in
 * which angle you attach the markers on it. The values below are what I happen
 * to have and I actually put them upside down and didn't bother to change, so
 * do adjust them to your liking. The point is to hold the cube with both hands,
 * initially showing 'STOP' to the camera. When you rotate the cube forward (the
 * top away from you), the car will start driving forward. Speed setting is the
 * cube's vertical position in the camera picture (lower the cube to go slow).
 * While in either 'FORWARD' or 'BACKWARD' mode, steering is possible by tilting
 * the cube left and right. When the robot is stationary (in 'STOP'), you can
 * rotate the cube sideways to turn in place.
 * 
 * You can also flip the axes and angle in reacTIVision's window to change the
 * overall coordinate orientation. You can also use some other TUIO tracker
 * program to track the fiducials.
 * 
 * The robot uses the subsumption features of leJOS: while idle (no fiducials
 * seen) it will just make a continuous noise, which is the action of least
 * priority. Above that is turning in place, driving, stopping and at the top,
 * quitting the program.
 * 
 * @author Jouko Strömmer
 */
public class TuioCar {

	//private DifferentialPilot pilot;
	private NXTMotor left, right;
	private TuioListener eyes;
	private Arbitrator arbitrator;
	private LightSensor light;
	private int speed;

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	// fiducials on the cube
	private final int STOP = 3;
	private final int FORWARD = 0;
	private final int BACKWARD = 2;
	private final int TURNLEFT = 5;
	private final int TURNRIGHT = 4;

	/**
	 * Quit the program 
	 */
	class QuitProgram implements Behavior {
		public void action() { System.exit(0); }
		public void suppress() {}
		public boolean takeControl() {
			return Button.ENTER.isPressed();
		}
	}
	
	/**
	 * Stop if light sensor sees dark. (not used)
	 */
	class StopOnLight implements Behavior {
		public void action() { System.exit(0); }
		public void suppress() {}
		public boolean takeControl() {
			return light.readValue() < 30;
		}
	}

	private void stop() {
		left.stop();
		right.stop();
	}
	
	/**
	 * Stop motors. 
	 */
	class Stop implements Behavior {
		public void action() {
			stop();
		}
		public void suppress() {}
		public boolean takeControl() {
			TuioObjectNXT[] targets = eyes.getObjects();
			if(targets.length == 1) return targets[0].getId() == STOP;
			else return false;
		}
	}

	/**
	 * Turning in place.
	 */
	class Turn implements Behavior {
		TuioObjectNXT[] targets = new TuioObjectNXT[0];
		public void action() {
			TuioObjectNXT target;
			if(targets.length == 1)
				target = targets[0];
			else return;
			
			light.setFloodlight(true);
			left.setPower(speed-(speed/3));
			right.setPower(speed-(speed/3));
			if(target.getId() == TURNLEFT) {
				left.backward();
				right.forward();
			} else {
				left.forward();
				right.backward();
			}
			light.setFloodlight(false);
		}
		
		public void suppress() {
			light.setFloodlight(false);
			stop();
		}
		
		public boolean takeControl() {
			targets = eyes.getObjects();
			if(targets.length == 1) return targets[0].getId() == TURNLEFT || targets[0].getId() == TURNRIGHT;
			else return false;
		}

	}

	/**
	 * Drive forward or backward. Car speed is controlled with the vertical position of the cube. 
	 */
	class Drive implements Behavior {
		TuioObjectNXT[] targets = new TuioObjectNXT[0];
		public void action() {
			TuioObjectNXT target;
			if(targets.length == 1)
				target = targets[0];
			else return;

			int speedSetting = speed-(int)(target.getY()*speed);

			if(target.getId() == FORWARD) {
				left.forward();
				right.forward();
			} else if (target.getId() == BACKWARD) {
				left.backward();
				right.backward();
			}

			// set motor speed manually to do simple steering
			// set speed for each motor proportionally to the angle
			// differential -1 .. 1
			// text upside down should be at differential zero
			float differential = (float)(target.getAngle()/(2*Math.PI))*2-1;

			int leftspeed = speedSetting*2 - (int)(differential*speedSetting);
			int rightspeed = speedSetting*2 + (int)(differential*speedSetting);

			left.setPower(leftspeed);
			right.setPower(rightspeed);
		}
		
		public void suppress() { stop(); }
		public boolean takeControl() {
			targets = eyes.getObjects();
			if(targets.length == 1) return targets[0].getId() == FORWARD || targets[0].getId() == BACKWARD;
			else return false;
		}
	}

	/**
	 * Make some noise. 
	 */
	class WasteTime implements Behavior {
		public void action() {
			Sound.playTone(220, 10);
			while(Sound.getTime() > 0);
			Sound.playTone(247, 10);
			while(Sound.getTime() > 0);
			Sound.playTone(147, 10);
			while(Sound.getTime() > 0);
		}
		public void suppress() {  }
		public boolean takeControl() {
			return true;
		}
	}

	/**
	 * To turn ignition key, connect eyes and start brain.
	 */
	public void connect () {
		(new Thread(eyes)).start();
		arbitrator.start();
	}

	/**
	 * Constructor.
	 * @param wheelDiameter wheel diameter (NXT 1.0 wheels are 56mm)
	 * @param trackWidth distance between wheels' centers
	 * @param left left motor
	 * @param right right motor
	 * @param polldelay passed to TuioListener, the time (in ms) between each request to PC 
	 * @param lightport the port where light sensor is connected
	 */
	public TuioCar (float wheelDiameter, float trackWidth, NXTMotor left, NXTMotor right, int polldelay, SensorPort lightport) {
		// start TUIO listener in separate thread
		this.left = left;
		this.right = right;
		eyes = new TuioListener();
		eyes.setPollDelay(polldelay);
		arbitrator = new Arbitrator(new Behavior[]{new WasteTime(), new Turn(), new Drive(), new Stop(), new QuitProgram()});
		light = new LightSensor(lightport);
		light.setFloodlight(false);
		speed = 200;
	}
}


