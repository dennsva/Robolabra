package demo;
import lejos.pc.comm.*;
import java.io.*;

/**
 * Simple program to transmit TUIO information over Bluetooth to an NXT. Adapted
 * from TUIO_JAVA examples. This version will use a standard input reader so you
 * can modify any external program to print out and pipe the object data to it.
 * The values will be sent to the NXT which can use its TuioListener. See the
 * GenTrack example program.
 * 
 * When connection has been established, the NXT program should send an integer
 * value 1 to indicate it wants a readout of the currently visible objects.
 * 
 * After receiving this message, PC will first write an integer to NXT that says
 * how many objects there are (which allows the NXT to prepare a suitable array
 * for storing them) and immediately transmits each object in sequence.
 * 
 * For each object, the data is written in the following order: symbol ID,
 * centroid X coordinate, centroid Y coordinate, angle.
 * 
 * The NXT side TuioListener should read these values in the same order.
 * 
 * @author Jouko Strömmer
 * 
 */
public class GenTrackPC {	

	final static int REQUEST_OBJECTS = 1;

	/**
	 * Send object data to NXT.
	 * @param dos DataOutputStream to use
	 * @param objectList list of TUIO objects maintained by TuioTrack
	 * @throws IOException
	 */
	public static void sendObjects (DataOutputStream dos, TrackerObject[] objects) throws IOException {
		if(objects == null) return;
		if(objects.length > 0) dos.writeInt(objects.length);
		else dos.writeInt(0);
		dos.flush();
		if(objects.length == 0) return;
		System.out.println("Sending "+objects.length+" objects");
		for(int i = 0; i < objects.length; i++) {
			TrackerObject obj = objects[i];
			dos.writeInt(obj.getId());
			dos.writeFloat(obj.getX());
			dos.writeFloat(obj.getY());
			dos.writeFloat(obj.getAngle());
			dos.flush();
			System.out.println("   Sent object "+obj.getId()+" at ("+obj.getX()+","+obj.getY()+") angle "+obj.getAngle());
		}
	}

	public static void main(String[] args) {
		// create StdinTracker that reads standard input
		StdinTracker tracker = new StdinTracker(System.in);
		(new Thread(tracker)).start();

		while(true) {
			NXTConnector conn = new NXTConnector();

			// Connect to any NXT over Bluetooth
			boolean connected = conn.connectTo("btspp://");

			/*		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}*/

			DataOutputStream dos = conn.getDataOut();
			DataInputStream dis = conn.getDataIn();
			int msg;

			// Wait for NXT to request data, then send them. Repeat.
			while(connected) {
				try {
					msg = dis.readInt();
					if(msg == REQUEST_OBJECTS) sendObjects(dos, tracker.getObjects());
				} catch (IOException ioe) {
					System.out.println("IO Exception in NXT communication:");
					System.out.println(ioe.getMessage());
					break;
				}
			}
			/*
			// close connection
			try {
				dis.close();
				dos.close();
				conn.close();
			} catch (IOException ioe) {
				System.out.println("IOException closing connection:");
				System.out.println(ioe.getMessage());
			}*/
		}
	}
}
