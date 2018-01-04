package testaus;

import robotti.Ristinolla;
import java.util.Scanner;

public class RistinollanTestaus {

	public static void main(String[] args) {	
		
		Ristinolla peli = new Ristinolla();
		Scanner lukija = new Scanner(System.in);
	
		while (peli.voittaja() == 0) {
			System.out.println(peli.ruudukkoString());
			System.out.println("Vuorossa: " + peli.merkki(peli.vuorossa()));
			System.out.println("Pelaa indeksiin:");
			
			int indeksi = Integer.parseInt(lukija.nextLine());
			peli.pelaa(indeksi);
		}
		
		System.out.println("Voittaja: " + peli.merkki(peli.voittaja()));
		
	}
	
}