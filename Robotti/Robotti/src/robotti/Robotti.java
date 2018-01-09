package robotti;

import lejos.nxt.*;

/**
 * Example leJOS Project with an ant build file
 *
 */

/**
Ohjelmassa on ensin toiminnallisuus kynän korkeuden hienosäätöön, jotta piirturi toimii tarkoituksenmukaisesti. (metodi asetaKorkeus)
Tämän jälkeen piirturia on mahdollista käyttää manuaalisesti nappien sekä kosketussensorien avulla.
En löytänyt tapaa poistaa listeneria moottorista A, joten deaktivoin sen booleanin moottoriA avulla.
 */
public class Robotti {

	static boolean moottoriA;
	
	//paperi on korkeudella rajaA, tämä asetetaan metodissa asetaKorkeus
	static int rajaA;
	
	//B range [0, 850]
	//C range [0, 500]
	static final int rajaB = 850;
	static final int rajaC = 500;
	
	//moottoreiden B ja C vakionopeudeksi 90
	static final int vakionopeus = 90;
	
	//alas = A forward
	//vasemmalle = B forward
	//taakse = C forward
	
	//lähtöpaikka NXT:n vieressä!
	
	public static void main(String[] args) {	
		asetaKorkeus();
		
		//vapaaPiirtaminen(false);
		piirraViiva(100, 100, 700, 400);
		
		nollaaRobotti();
	}
	
	public static void vapaaPiirtaminen() {
		vapaaPiirtaminen(true);
	}
	
	public static void vapaaPiirtaminen(final boolean rajoitettu) {
		//metodin parametrit ovat testausta varten!
		
		asetaKorkeus();
		asetaNopeudet(vakionopeus);
		
		System.out.println("Nyt voit piirtaa");
		System.out.println("vapaasti! Esc");
		System.out.println("lopettaa.");
		
		//piirturin siirtäminen vasemmalle
		Button.LEFT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (rajoitettu) {
					Motor.B.rotateTo(rajaB, true);
				} else {
					Motor.B.forward();
				}
			}
			
			public void buttonReleased(Button b) {
				Motor.B.stop();
			}
		});
		
		//piirturin siirtäminen oikealle
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (rajoitettu) {
					Motor.B.rotateTo(0, true);
				} else {
					Motor.B.backward();
				}
			}
			
			public void buttonReleased(Button b) {
				Motor.B.stop();
			}
		});
		
		//kynän siirtäminen eteen
		SensorPort.S1.addSensorPortListener(new SensorPortListener() {
			public void stateChanged(SensorPort p, int i, int f) {
				TouchSensor sensor = new TouchSensor(p);
				if (sensor.isPressed()) {
					if (rajoitettu) {
						Motor.C.rotateTo(rajaC, true);
					} else {
						Motor.C.forward();
					}
				} else {
					Motor.C.stop();
				}
			}
		});
		
		//kynän siirtäminen taakse
		SensorPort.S2.addSensorPortListener(new SensorPortListener() {
			public void stateChanged(SensorPort p, int i, int f) {
				TouchSensor sensor = new TouchSensor(p);
				if (sensor.isPressed()) {
					if (rajoitettu) {
						Motor.C.rotateTo(0, true);
					} else {
						Motor.C.backward();
					}
				} else {
					Motor.C.stop();
				}
			}
		});
		
		Button.ESCAPE.waitForPressAndRelease();
	}
	
	public static int getX() {
		return Motor.B.getPosition();
	}
	
	public static int getY() {
		return Motor.C.getPosition();
	}
	
	private static double etaisyysPisteeseen(int x, int y) {
		return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
	}

	public static void nollaaRobotti() {
		nostaKyna();
		asetaNopeudet(vakionopeus);
		liikuta(0, 0);
	}
	
	public static void laskeKyna() {
		Motor.A.rotateTo(rajaA);
	}
	
	public static void nostaKyna() {
		Motor.A.rotateTo(0);
	}
	
	public static void liikuta(int loppuX, int loppuY) {
		Motor.B.rotateTo(loppuX, true);
		Motor.C.rotateTo(loppuY);
		Motor.B.rotateTo(loppuX);
	}
	
	public static void asetaNopeudet(int nopeus) {
		asetaNopeudet(nopeus, nopeus);
	}
	
	public static void asetaNopeudet(int nopeusX, int nopeusY) {
		Motor.B.setSpeed(nopeusX);
		Motor.C.setSpeed(nopeusY);
	}
	
	public static void asetaKorkeus() {
		
		moottoriA = true;
		
		//riville mahtuu 16 merkkiä
		System.out.println("Aseta kynan");
		System.out.println("korkeus ja paina");
		System.out.println("enter");
		System.out.println("<- alas  ylos ->");
		
		//kynän siirtäminen alaspäin
		Button.LEFT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (moottoriA) {
					Motor.A.forward();
				}
			}
			
			public void buttonReleased(Button b) {
				Motor.A.stop();
			}
		});
		
		//kynän siirtäminen ylöspäin
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (moottoriA) {
					Motor.A.backward();
				}
			}
			
			public void buttonReleased(Button b) {
				Motor.A.stop();
			}
		});
		
		Button.ENTER.waitForPressAndRelease();
		moottoriA = false;
		rajaA = Motor.A.getPosition();
		LCD.clear();
	}
	
	public static void piirraViiva(int lahtoX, int lahtoY, int loppuX, int loppuY) {
		
		//aloitetaan lähimmästä päästä
		if (etaisyysPisteeseen(loppuX, loppuY) < etaisyysPisteeseen(lahtoX, lahtoY)) {
			piirraViiva(loppuX, loppuY, lahtoX, lahtoY);
			return;
		}
		
		asetaNopeudet(vakionopeus);
		liikuta(lahtoX, lahtoY);
		
		//moottoreiden pitää liikkua eri pituiset osuudet yhtä nopeasti!
		int leveys = loppuX - lahtoX;
		int korkeus = loppuY - lahtoY;
		int suurin = Math.max(korkeus, leveys);
		asetaNopeudet((90 * leveys) / suurin, (90 * korkeus) / suurin);

		laskeKyna();
		liikuta(loppuX, loppuY);
		nostaKyna();
	}
	
}
