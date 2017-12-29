package demo;

import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetectorAdapter;

/**
 * A feature detector for TUIO data.
 * 
 * @author Jouko Strömmer
 *
 */
public class TuioFeatureDetector extends FeatureDetectorAdapter {
	TuioListener tuio;
	public TuioFeatureDetector(int delay) {
		super(delay);
		tuio = new TuioListener();
		tuio.setPollDelay(delay);
		(new Thread(tuio)).start();
	}

	public Feature scan() {
		// reading should happen here
		TuioObjectNXT[] objects = tuio.getObjects();
		// note that for now we only send the FIRST object and discard the rest!
		if(objects.length > 0) return new TuioFeature(objects[0].getId(), objects[0].getX(), objects[0].getY(), objects[0].getAngle());
		else return null;
	}
	
}
