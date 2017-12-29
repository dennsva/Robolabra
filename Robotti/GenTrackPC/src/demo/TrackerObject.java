package demo;

/**
 * A class for storing tracked object data.
 * 
 * @author Jouko Strömmer
 *
 */
public class TrackerObject {
	int id;
	float x, y, angle;
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public float getX() { return x; }
	public void setX(float x) { this.x = x; }
	public float getY() { return y; }
	public void setY(float y) { this.y = y; }
	public float getAngle() { return angle; }
	public void setAngle(float angle) { this.angle = angle; }
	
	public TrackerObject(int id, float x, float y, float angle) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

}