package robotti;

import lejos.nxt.*;

/**
 * Example leJOS Project with an ant build file
 *
 */
public class Robotti {

	public static void main(String[] args) {
		//alas = A forward
		//vasemmalle = B forward
		
		//piirturin siirtäminen vasemmalle
		Button.LEFT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				Motor.B.forward();
			}
			
			public void buttonReleased(Button b) {
				Motor.B.stop();
			}
		});
		
		//piirturin siirtäminen oikealle
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				Motor.B.backward();
			}
			
			public void buttonReleased(Button b) {
				Motor.B.stop();
			}
		});
		
		//kynän siirtäminen alaspäin
		SensorPort.S1.addSensorPortListener(new SensorPortListener() {
			public void stateChanged(SensorPort p, int i, int f) {
				TouchSensor sensor = new TouchSensor(p);
				if (sensor.isPressed()) {
					Motor.A.forward();
				} else {
					Motor.A.stop();
				}
			}
		});
		
		//kynän siirtäminen ylöspäin
		SensorPort.S2.addSensorPortListener(new SensorPortListener() {
			public void stateChanged(SensorPort p, int i, int f) {
				TouchSensor sensor = new TouchSensor(p);
				if (sensor.isPressed()) {
					Motor.A.backward();
				} else {
					Motor.A.stop();
				}
			}
		});
		
		Button.ESCAPE.waitForPressAndRelease();
	}
}
