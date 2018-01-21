package robotti;

import java.util.ArrayList;
import java.util.Random;

public class Satunnaispelaaja implements Pelaaja {

	private int pelaaja;
	
	public Satunnaispelaaja(int pelaaja) {
		this.pelaaja = pelaaja;
	}
	
	/**Tekee satunnaisen siirron.*/
	public int pelaa(ArrayList<Integer> ruudukko) {
		Random random = new Random();
		while (true) {
			int luku = random.nextInt(9);
			if (ruudukko.get(luku) == 0) {
				return luku;
			}
		}
	}
			
}
