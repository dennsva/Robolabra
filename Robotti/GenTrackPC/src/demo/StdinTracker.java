package demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * A class to pass object info from standard input to a transmitter program
 * (GenTrackPC) for sending to NXT.
 * 
 * @author Jouko Strömmer
 * 
 */
public class StdinTracker implements Runnable {
	InputStream input;
	TrackerObject[] objects;

	private void flush () {
		try {
			while(input.available() > 0) input.skip(input.available());
		} catch (IOException e) { System.out.println("Error reading input"); }
	}

	public StdinTracker (InputStream input) {
		this.input = input;
		objects = new TrackerObject[0];
		System.out.println("STDIN tracker created");
	}

	public TrackerObject[] getObjects() {
		synchronized (this) {
			return objects;
		}
	}

	public void run() {
		flush();
		Scanner scanner = new Scanner(input);
		String text = "";
		// read until EOF
		while(scanner.hasNext()) {
			text = scanner.nextLine();
			StringTokenizer t = new StringTokenizer(text);
			int numtokens = t.countTokens();
			// every object's data should be on the same line, 4 tokens each
			if(numtokens % 4 == 0 && numtokens >= 4) {
				TrackerObject[] newobjects = new TrackerObject[numtokens/4];
				for(int i = 0; i < newobjects.length; i++) {
					// read one integer and three floats per line separated by whitespace
					// --- for camshift read area instead of id and round it
					int id = Math.round(Float.parseFloat(t.nextToken()));
					//float area = Float.parseFloat(t.nextToken());
					float x = Float.parseFloat(t.nextToken());
					float y = Float.parseFloat(t.nextToken());
					float angle = Float.parseFloat(t.nextToken());
					// create a new tracker object with parsed information and place it in an array
					TrackerObject obj = new TrackerObject(id, x, y, angle);
					newobjects[i] = obj;
					System.out.println("objects["+i+"]={"+obj.id+", "+obj.x+", "+obj.y+", "+obj.angle+"}");
				}
				// replace the previous array with new data. If the first value of the object is -1,
				// interpret it so that all objects disappeared and make an empty array
				synchronized (this) {
					if(newobjects[0].getId() == -1) newobjects = new TrackerObject[0];
					objects = newobjects;
				}
			}
		}
	}
}
