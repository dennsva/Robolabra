package demo;
import lejos.pc.comm.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import TUIO.TuioObject;

/**
 * Simple program to transmit TUIO information over Bluetooth to an NXT. Adapted
 * from TUIO_JAVA examples.
 * 
 * The program listens for TUIO messages in port 3333 (UDP) using a TuioTrack
 * object (which in turn uses TuioClient for the actual listening) and maintains
 * a hashtable of detected fiducials (symbol ID as the key). At startup it
 * attempts to connect to an NXT that uses a (custom) TuioListener.
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
public class TuioTransmitter {	

	final static int REQUEST_OBJECTS = 1;

	/**
	 * Send object data to NXT.
	 * @param dos DataOutputStream to use
	 * @param objectList list of TUIO objects maintained by TuioTrack
	 * @throws IOException
	 */
	public static void sendObjects (DataOutputStream dos, Hashtable<Long,TuioObject> objectList) throws IOException {
		if(objectList != null) dos.writeInt(objectList.size());
		else dos.writeInt(0);
		dos.flush();
		if(objectList.size() == 0) return;
		System.out.println("Sending "+objectList.size()+" objects");
		Enumeration<TuioObject> iterate = objectList.elements();
		while(iterate.hasMoreElements()) {
			TuioObject obj = iterate.nextElement();
			// write the symbol ID (number of each fiducial in the PDF), not session ID
			dos.writeInt(obj.getSymbolID());
			dos.writeFloat(obj.getX());
			dos.writeFloat(obj.getY());
			dos.writeFloat(obj.getAngle());
			dos.flush();
			System.out.println("Sent object "+obj.getSymbolID()+" at ("+obj.getX()+","+obj.getY()+" angle "+obj.getAngle());
		}
	}

	/**
	 * Send cursor data to NXT in an identical fashion. Not tested.
	 */
	public static void sendCursors (DataOutputStream dos, Hashtable<Long,TuioObject> cursorList) throws IOException {
		// just copy & paste of sendObjects, may not work as intended and left unfinished	
		if(cursorList != null) dos.writeInt(cursorList.size());
		else dos.writeInt(0);
		dos.flush();
		if(cursorList.size() == 0) return;
		System.out.println("Sending "+cursorList.size()+" objects");
		Enumeration<TuioObject> iterate = cursorList.elements();
		while(iterate.hasMoreElements()) {
			TuioObject obj = iterate.nextElement();
			// write the symbol ID, not session ID
			dos.writeInt(obj.getSymbolID());
			dos.writeFloat(obj.getX());
			dos.writeFloat(obj.getY());
			dos.writeFloat(obj.getAngle());
			dos.flush();
			System.out.println("Sent cursor "+obj.getSymbolID()+" at ("+obj.getX()+","+obj.getY()+" angle "+obj.getAngle());
		}
	}

	public static void main(String[] args) {
		// create TUIO tracker (fiducial finder)
		TuioTracker tracker = new TuioTracker();
		
		tracker.run();
		

		NXTConnector conn = new NXTConnector();

		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");

		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}

		DataOutputStream dos = conn.getDataOut();
		DataInputStream dis = conn.getDataIn();
		int msg;

		// Wait for NXT to request data, then send them. Repeat.
		while(true) {
			try {
				msg = dis.readInt();
				System.out.print(".");
				if(msg == 1) sendObjects(dos, tracker.getObjectList());
			} catch (IOException ioe) {
				System.out.println("IO Exception in NXT communication:");
				System.out.println(ioe.getMessage());
				break;
			}
		}

		// close connection
		try {
			dis.close();
			dos.close();
			conn.close();
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}
}
