package demo;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;

public class TuioFeatureTest implements FeatureListener{
	public static void main(String[] args) throws Exception {
		TuioFeatureTest listener = new TuioFeatureTest();
		TuioFeatureDetector fd = new TuioFeatureDetector(50);
		fd.addListener(listener);
		while(!Button.ESCAPE.isPressed()) {
			
			// Perform a single scan:
			if(Button.ENTER.isPressed()) {
				TuioFeature res = (TuioFeature)fd.scan();
				if(res == null) System.out.println("Nothing detected");
				else {
					System.out.println(""+res.getId()+": "+res.getX()+" "+res.getY()+" "+res.getAngle());
				}
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

	public void featureDetected(Feature feature, FeatureDetector detector) {
		TuioFeature tf = (TuioFeature)feature;
		System.out.println(""+tf.getId()+" "+Math.round(tf.getX()*100)+" "+Math.round(tf.getY()*100)+" "+Math.round(tf.getAngle()*100));
	}
}
