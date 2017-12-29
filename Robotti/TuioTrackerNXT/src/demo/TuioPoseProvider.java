package demo;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class TuioPoseProvider implements PoseProvider {
	private TuioListener listener;
	private TuioObjectNXT[] objects;
	private int posesymbol;
	private Pose pose;
	private boolean update;
	private float xscale, yscale;

	public TuioPoseProvider(TuioListener lis, int poseSymbol, float xscale, float yscale) {
		listener = lis;
		this.pose = new Pose(0, 0, 0);
		this.posesymbol = poseSymbol;
		this.xscale = xscale;
		this.yscale = yscale;
		if(!listener.connected()) (new Thread(listener)).start();
		this.update = true;
	}

	@Override
	public Pose getPose() {
		// return new pose or the last one if fiducial is not found
		if(update) updatePose();
		return pose;
	}

	private void updatePose () {
		Pose current = getSymbolPose(posesymbol);
		if(current != null) pose = current;
	}

	// Returns null if no such symbol is seen,
	// stops iterating after match found
	public Pose getSymbolPose (int symbolId) {
		objects = listener.getObjects();
		Pose found = null;
		for(int i = 0; i < objects.length; i++) {
			if(objects[i].getId() == symbolId) {
				found = new Pose();
				found.setLocation(objects[i].getX()*(float)xscale, objects[i].getY()*(float)yscale); 
				// reacTIVision's 0 angle is straight up, leJOS's is to the right, both grow counterclockwise
				found.setHeading((float)(Math.toDegrees(objects[i].getAngle()) + 90) % 360);
				break;
			}
		}
		return found;
	}

	public void setUpdate (boolean enabled) {
		update = enabled;
	}

	@Override
	public void setPose(Pose aPose) {
		pose = aPose;
	}

}
