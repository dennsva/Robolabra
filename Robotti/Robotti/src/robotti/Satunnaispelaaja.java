package robotti;

import java.util.ArrayList;
import java.util.Random;

public class Satunnaispelaaja implements Pelaaja {

	private int pelaaja;
	public Ristinolla peli;
	
	public Satunnaispelaaja(int pelaaja) {
		this.pelaaja = pelaaja;
	}
	
	/**Tekee satunnaisen siirron.*/
	public int pelaa() {
		Random random = new Random();
		while (true) {
			int luku = random.nextInt(9);
			if (peli.getRuudukko().get(luku) == 0) {
				return luku;
			}
		}
	}
	
	/**Antaa pelaaja-oliolle pelin tiedot.*/
	public void setPeli(Ristinolla peli) {
		this.peli = peli;
	}
			
}
