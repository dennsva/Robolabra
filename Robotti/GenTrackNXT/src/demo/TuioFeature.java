package demo;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.objectdetection.Feature;

/**
 * Class describing the properties of a feature detected by a TUIO tracker. Can
 * be used for other similar purposes.
 * 
 * @author Jouko Strömmer
 * 
 */
public class TuioFeature implements Feature {

	private int id;
	private float x,y,angle;
	
	public TuioFeature () {
		id = 0;
		x = y = angle = 0;
	}
	
	public TuioFeature (int id, float x, float y, float angle) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	// not used, just there because leJOS wants them
	public RangeReading getRangeReading() {	return null; }
	public RangeReadings getRangeReadings() { return null; }
	public long getTimeStamp() { return 0; }

}
