package demo;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;

/**
 * Test program for TuioCar. 
 * @author Jouko Strömmer
 *
 */
public class TuioCarDemo {

	public static void main(String[] args) {
		TuioCar car = new TuioCar(5.6f, 17f, new NXTMotor(MotorPort.A), new NXTMotor(MotorPort.B), 50, SensorPort.S3);
		car.setSpeed(50);
		car.connect();
	}
}
