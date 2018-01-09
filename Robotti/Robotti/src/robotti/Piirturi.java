package robotti;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;

public class Piirturi {

	//En löytänyt tapaa poistaa listeneria moottorista A, joten deaktivoin sen booleanin moottoriA avulla.
	private boolean moottoriA;
	
	//Kynä on paperissa, kun moottorin A tila on rajaA. Tämä säädetään metodissa asetaKorkeus.
	private int rajaA;
	
	//moottori B liikuttaa kynää x-akselin suuntaisesti välillä [0, rajaB]
	private final int rajaB;
	//moottori C liikuttaa kynää y-akselin suuntaisesti välillä [0, rajaC]
	private final int rajaC;
	private final int vakionopeus;
	
	public Piirturi(int rajaB, int rajaC, int vakionopeus) {
		this.rajaB = rajaB;
		this.rajaC = rajaC;
		this.vakionopeus = vakionopeus;
	}
	
	public Piirturi() {
		this.rajaB = 850;
		this.rajaC = 500;
		this.vakionopeus = 90;
	}
	
	/**Antaa mahdollisuuden ohjata piirturia suoraan nappien avulla.*/
	public void vapaaPiirtaminen() {
		vapaaPiirtaminen(true);
	}
	
	public void vapaaPiirtaminen(final boolean rajoitettu) {
		//metodin parametrit ovat testausta varten!
		
		asetaKorkeus();
		asetaNopeudet(vakionopeus);
		
		System.out.println("Nyt voit piirtaa");
		System.out.println("vapaasti! Esc");
		System.out.println("lopettaa.");
		
		//kynän siirtäminen vasemmalle
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
		
		//kynän siirtäminen oikealle
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
	
	/**Antaa kynan x-koordinaatin valilla [0, 850].*/
	public int getX() {
		return Motor.B.getPosition();
	}
	
	/**Antaa kynan y-koordinaatin valilla [0, 500].*/
	public int getY() {
		return Motor.C.getPosition();
	}
	
	private double etaisyysPisteeseen(int x, int y) {
		return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
	}

	/**Nostaa kynan ja siirtaa piirturin oletusasentoon.*/
	public void nollaaRobotti() {
		nostaKyna();
		asetaNopeudet(vakionopeus);
		liikuta(0, 0);
	}
	
	/**Laskee kynan paperiin.*/
	public void laskeKyna() {
		Motor.A.rotateTo(rajaA);
	}
	
	/**Nostaa kynan paperista.*/
	public void nostaKyna() {
		Motor.A.rotateTo(0);
	}
	
	/**Liikuttaa piirturia suoraan parametrien maaraamaan pisteeseen. X valilla [0, 850] ja Y valilla [0, 500].*/
	public void liikuta(int loppuX, int loppuY) {
		if (loppuX < 0) loppuX = 0;
		if (loppuX > rajaB) loppuX = rajaB;
		if (loppuY < 0) loppuY = 0;
		if (loppuY > rajaC) loppuY = rajaC;
		
		//liikutaan lyhintä reittiä
		int leveys = Math.abs(loppuX - getX());
		int korkeus = Math.abs(loppuY - getY());
		float suurin = (float) Math.max(korkeus, leveys);
		
		//moottoreiden pitää liikkua eri pituiset osuudet yhtä nopeasti! maksiminopeus vakionopeus.
		System.out.println((vakionopeus * leveys) / suurin);
		System.out.println((vakionopeus * korkeus) / suurin);
		asetaNopeudet((vakionopeus * leveys) / suurin, (vakionopeus * korkeus) / suurin);
		
		//liikutaan
		Motor.B.rotateTo(loppuX, true);
		Motor.C.rotateTo(loppuY);
		Motor.B.rotateTo(loppuX);
	}
	
	/**Asettaa kynan liikkumisnopeuden parametrin molempiin suuntiin.*/
	public void asetaNopeudet(float nopeus) {
		asetaNopeudet(nopeus, nopeus);
	}
	
	/**Asettaa kynan liikkumisnopeuden parametrin molempiin suuntiin.*/
	public void asetaNopeudet(int nopeus) {
		asetaNopeudet(nopeus, nopeus);
	}
	
	/**Asettaa kynan liikkumisnopeuden parametrien mukaiseksi.*/
	public void asetaNopeudet(float nopeusX, float nopeusY) {
		Motor.B.setSpeed(nopeusX);
		Motor.C.setSpeed(nopeusY);
	}
	
	/**Asettaa kynan liikkumisnopeuden parametrien mukaiseksi.*/
	public void asetaNopeudet(int nopeusX, int nopeusY) {
		Motor.B.setSpeed(nopeusX);
		Motor.C.setSpeed(nopeusY);
	}
	
	/**Antaa kayttajalle mahdollisuuden hienosaataa kynan korkeus.*/
	public void asetaKorkeus() {
		
		moottoriA = true;
		
		//riville mahtuu 16 merkkiä
		System.out.println("Aseta kynan");
		System.out.println("korkeus ja paina");
		System.out.println("enter");
		System.out.println("<- alas  ylos ->");
		
		//kynan siirtäminen alaspäin
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
		
		//kynan siirtäminen ylöspäin
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
	
	/**Piirtaa suoran viivan parametrien maaraamien pisteiden valille.*/
	public void piirraViiva(int lahtoX, int lahtoY, int loppuX, int loppuY) {
		
		nostaKyna();
		
		//aloitetaan lähimmasta päästa
		if (etaisyysPisteeseen(loppuX, loppuY) < etaisyysPisteeseen(lahtoX, lahtoY)) {
			piirraViiva(loppuX, loppuY, lahtoX, lahtoY);
			return;
		}
		
		liikuta(lahtoX, lahtoY);
		laskeKyna();
		liikuta(loppuX, loppuY);
		nostaKyna();
	}
	
}