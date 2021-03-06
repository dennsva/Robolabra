package demo;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;

/**
 * A proof-of-concept general purpose object tracker getting its input from PC
 * via a TuioFeatureDetector, which will notify when something has been detected
 * by reading in values via Bluetooth with an NXT TuioListener class which is
 * created in TuioFeatureDetector.
 * 
 * On the PC side, a program using an StdinTracker runs along with a transmitter
 * program that will send the values to the NXT. The protocol is simple:
 * 
 * I) many objects can be reported on a single line with the following format:
 * <obj1 id> <obj1 x> <obj1 y> <obj1 angle> <obj2 id> <obj2 x> <obj2 y> <obj2 angle> .... 
 * II) when a the program loses the object from tracking, it should
 * print out -1 -1 -1 -1 so that StdinTracker returns an empty object list
 * 
 * In this example using a modified camshiftdemo program, the "id" number is
 * used to store the distance to the object, calculated by first fitting a
 * spline to the measured data (NXT ball measured distance from camera vs. pixel
 * area in the picture) and making a function for the spline to calculate
 * distance for arbitrary areas. Modify camshiftdemo2.cpp to do something
 * different. The spline function is Spline2D, generated by an online spline
 * fitter, but you could also use octave or gnuplot to get a similar result.
 * 
 * @author Jouko Strömmer
 * 
 */
public class GenTrack implements FeatureListener{

	static DifferentialPilot pilot;
	// motor for tilting the camera up and down
	static NXTRegulatedMotor cameramotor = Motor.C;
	// small nokia phone settings
//	final static int cameraResolutionX = 320;
//	final static int cameraResolutionY = 240;
//	final static int cameraFov = 48;
	// the target should be held no more than accuracy degrees
	// away from the heading of the robot
	final static int accuracy = 5;
	// quickcam pro 9000
		final static int cameraResolutionX = 640;
		final static int cameraResolutionY = 480;
		final static int cameraFov = 60;
	static boolean followBall = false;
	
	
	public static void main(String[] args) throws Exception {
		// use a pilot for simplicity
		pilot = new DifferentialPilot(5.6f, 17f, Motor.A, Motor.B);
		pilot.setRotateSpeed(20);
		pilot.setTravelSpeed(5);
		cameramotor.setSpeed(100);
		
		GenTrack listener = new GenTrack();
		TuioFeatureDetector fd = new TuioFeatureDetector(100);
		
		System.out.println("Rotate camera all the way to the back and press ENTER to start.");
		Button.ENTER.waitForPressAndRelease();
		cameramotor.resetTachoCount();
		cameramotor.rotateTo(60);
		
		fd.addListener(listener);
		while(!Button.ESCAPE.isPressed()) {
			
			// Perform a single scan:
			if(Button.ENTER.isPressed()) {
				TuioFeature res = (TuioFeature)fd.scan();
				if(res == null) System.out.println("Nothing detected");
				else System.out.println(""+res.getId()+": "+res.getX()+" "+res.getY()+" "+res.getAngle());
				Thread.sleep(500);
			}
			
			// toggle following of blue ball
			if(Button.LEFT.isPressed()) {
				followBall = !followBall;
				Thread.sleep(500);
			}
			
			// Enable/disable detection using buttons:
			if(Button.RIGHT.isPressed()) {
				if(fd.isEnabled()) {
					Sound.beepSequence();
					System.out.println("Autodetect OFF");
				} else {
					Sound.beepSequenceUp();
					System.out.println("Autodetect ON");
				}
				fd.enableDetection(!fd.isEnabled());
				Thread.sleep(500);
			}
			Thread.yield();		
		}

	}

	public static int sign(final int x) {
	      return (x == 0) ? 0 : (x > 0) ? 1 : -1;
	  }

	/**
	 * Here we define the actual responses to detected features. Note that the
	 * way the robot moves here is very suboptimal.
	 */
	public void featureDetected(Feature feature, FeatureDetector detector) {
		TuioFeature tf = (TuioFeature)feature;
		
		// calculate the offset from center, in angles (X) and pixels (Y)
		float anglediff = ((cameraFov*10000/(cameraResolutionX))*(tf.getX()-(cameraResolutionX/2)))/10000;
		int ydiff = Math.round(tf.getY()-(cameraResolutionY/2));
	
		// if there's enough deviation in Y axis, tilt the camera accordingly
		if(Math.abs(ydiff) > 15) {
			int tacho = cameramotor.getTachoCount();
			if (tacho >= 0 && ydiff < 0) cameramotor.rotate(-2, true);
			else if(tacho <= 90 && ydiff >= 0) cameramotor.rotate(2, true);
			else Sound.buzz();
		}
		
		// turn the robot to face the target if needed
		if(anglediff <= -accuracy) {
			pilot.setRotateSpeed(Math.abs(anglediff)*2);
			pilot.rotate(-anglediff);
		}
		else if(anglediff > accuracy) {
			pilot.setRotateSpeed(Math.abs(anglediff)*2);
			pilot.rotate(-anglediff);
		}
		
		// try to keep the distance at about 40cm
		if(followBall) {
			if(tf.getId() < 36) {
				pilot.travel(-2);
			} else if (tf.getId() > 45) {
				pilot.travel(2);
			}
		}
	}
}
