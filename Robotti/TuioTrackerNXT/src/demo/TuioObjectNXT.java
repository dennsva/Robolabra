package demo;

/**
 * Class to hold relevant fiducial data, i.e. symbol ID, coordinates and angle.
 * @author Jouko Strömmer
 *
 */
public class TuioObjectNXT {
	private int id;
	private float x,y,angle;
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

	public TuioObjectNXT () {
		this.id = -1;
		this.x = -1;
		this.y = -1;
		this.angle = -1;
	}

	public TuioObjectNXT (int id, float x, float y, float angle) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
}
