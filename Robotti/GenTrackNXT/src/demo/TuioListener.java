package demo;

import lejos.nxt.comm.*;
import lejos.robotics.objectdetection.Feature;
import lejos.util.Delay;

import java.io.*;

/**
 * A TuioListener adapted for NXT. Handles the Bluetooth connection, receiving
 * data and maintaining a list of visible objects. It repeatedly asks the PC for
 * fiducial information and waits pollDelay milliseconds after each request.
 * 
 * @author Jouko Strömmer
 * 
 */
public class TuioListener implements Runnable {

	private DataInputStream dis;
	private DataOutputStream dos;
	private final int REQUEST_OBJECTS = 1;
	private int pollDelay = 50;
	private TuioObjectNXT[] objects = new TuioObjectNXT[0];
	private volatile boolean connected;

	public int getPollDelay() {
		return pollDelay;
	}

	public void setPollDelay(int pollDelay) {
		this.pollDelay = pollDelay;
	}

	public int getObjectCount () {
		return objects.length;
	}

	/**
	 * The main accessor. 
	 * 
	 * Example use in a program:
	 * 
	 * TuioListener tuio = new TuioListener();
	 * TuioObjectNXT target = tuio.getObjects()[0];
	 * System.out.println("At "+target.getX()+","+target.getY()+" angle "+target.getAngle());
	 * 
	 */
	public TuioObjectNXT[] getObjects () {
		synchronized (this) {
			return objects;
		}
	}
	
	public Feature featureScan() {
		if(objects.length > 0) {
			return null;
		}
		return null;
		
	}

	public TuioListener () {
		connected = false;
	}

	public boolean connected () {
		return connected;
	}

	public void disconnect () {
		connected = false;
	}

	public TuioObjectNXT[] readObjects () throws IOException {
		int num;
		TuioObjectNXT[] obj;

		// write request
		dos.writeInt(REQUEST_OBJECTS);
		dos.flush();

		// read number of TUIO objects
		num = dis.readInt();

		// return empty array if there are no objects
		if(num == 0) {
			//objectCount = 0;
			return new TuioObjectNXT[0];
		} else
			obj = new TuioObjectNXT[num];

		// fill array with objects
		for(int i = 0; i < num; i++)
			obj[i] = new TuioObjectNXT(dis.readInt(), dis.readFloat(), dis.readFloat(), dis.readFloat());

		return obj;
	}

	private void updateObjects () {
		try {
			synchronized (this) {
				objects = readObjects();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		try {

			while (true) {
				System.out.println("Waiting...");
				BTConnection btc = Bluetooth.waitForConnection();
				System.out.println("Connected.");
				this.connected = true;

				dis = btc.openDataInputStream();
				dos = btc.openDataOutputStream();

				// poll objects in a loop
				while(connected) {
					updateObjects();
					Delay.msDelay(pollDelay);
				}
					
				dis.close();
				dos.close();
				Thread.sleep(200); // wait for data to drain
				btc.close();
			}
		} catch (Exception e) {
			System.out.println("TuioListener error.");
		}	
	}
}


