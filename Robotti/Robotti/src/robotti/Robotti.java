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
	
	public static void main(String[] args) {	

		Kayttoliittyma kayttoliittyma = new Kayttoliittyma();
		kayttoliittyma.kaynnista();
		
	}

}
