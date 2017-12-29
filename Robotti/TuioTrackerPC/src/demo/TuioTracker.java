package demo;

import java.util.Hashtable;
import TUIO.*;

/**
 * Pretty much copied from TUIO_JAVA examples. TuioTrack implements the listening
 * of TUIO messages and maintaining information about visible fiducials.
 * 
 * @author Jouko Strömmer
 * 
 */
public class TuioTracker implements TuioListener, Runnable {
		
		volatile int id;
		volatile float x, y, angle, distance;
		int port;
	
		private Hashtable<Long,TuioObject> objectList = new Hashtable<Long,TuioObject>();
		private Hashtable<Long,TuioCursor> cursorList = new Hashtable<Long,TuioCursor>();
		
		public Hashtable<Long, TuioObject> getObjectList() {
			return objectList;
		}

		public Hashtable<Long, TuioCursor> getCursorList() {
			return cursorList;
		}

		public static final int finger_size = 15;
		public static final int object_size = 60;
		public static final int table_size = 760;

		public static int width, height;
		
		public void addTuioObject(TuioObject tobj) {
			objectList.put(tobj.getSessionID(),tobj);
		}

		public void updateTuioObject(TuioObject tobj) {
			TuioObject obj = objectList.get(tobj.getSessionID());
			obj.update(tobj);
		}

		public void removeTuioObject(TuioObject tobj) {
			objectList.remove(tobj.getSessionID());
		}

		public void addTuioCursor(TuioCursor tcur) {
			if (!cursorList.containsKey(tcur.getSessionID())) {
				cursorList.put(tcur.getSessionID(), tcur);
			}
		}

		public void updateTuioCursor(TuioCursor tcur) {
			TuioCursor cur = cursorList.get(tcur.getSessionID());
			cur.update(tcur);
		}

		public void removeTuioCursor(TuioCursor tcur) {
			cursorList.remove(tcur.getSessionID());
		}

        public void refresh(TuioTime frameTime) {
                //System.out.println("refresh "+frameTime.getTotalMilliseconds());
        }
        
        public int getNumObjects () {
        	return objectList.size();
        }
        
        public int getNumCursors () {
        	return cursorList.size();
        }
        
        public TuioTracker () {
        	port = 3333;
        	id = 0;
        	x = y = angle = 0;
        }
                
        public void run() {
            TuioClient client = new TuioClient(port);
            System.out.println("listening to TUIO messages at port "+port);
            client.addTuioListener(this);
            client.connect();
        } 
}
