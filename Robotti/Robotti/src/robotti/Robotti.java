package robotti;

import lejos.nxt.*;

/**
 * Example leJOS Project with an ant build file
 *
 */

public class Robotti {
	
	//alas = A forward
	//vasemmalle = B forward
	//taakse = C forward
	
	//lähtöpaikka NXT:n vieressa!
	
	public static void main(String[] args) {	
		Piirturi piirturi = new Piirturi();
		
		piirturi.asetaKorkeus();
		//piirturi.vapaaPiirtaminen();
		piirturi.piirraViiva(100, 100, 500, 800);
		piirturi.piirraViiva(100, 200, 400, 300);
		piirturi.nollaaRobotti();
	}

}
