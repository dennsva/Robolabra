package robotti;

import java.util.ArrayList;

public class Ristinolla {
	
	//ruudukon indeksit:
	// 0 1 2
	// 3 4 5
	// 6 7 8
	
	// 0 tarkoittaa tyhjää
	// 1 on pelaaja 1
	// 2 on pelaaja 2
	
	private ArrayList<Integer> ruudukko;
	
	public Ristinolla() {
		this.ruudukko = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			ruudukko.add(0);
		}
	}
	
	public String merkki(int pelaaja) {
		if (pelaaja == 1) return "X";
		if (pelaaja == 2) return "O";
		return "-";
	}
	
	public String ruudukkoString() {
		String string = "";
		for (int rivi = 0; rivi < 3; rivi++) {
			for (int sarake = 0; sarake < 3; sarake++) {
				string += merkki(ruudukko.get(3 * rivi + sarake));
			}
			string += "\n";
		}
		return string;
	}
	
	public int pelattuja(int pelaaja) {
		int pelattuja = 0;
		for (int i = 0; i < ruudukko.size();) {
			if (ruudukko.get(i) == pelaaja) pelattuja++;
		}
		return pelattuja;
	}
	
	public int vuorossa() {
		if (pelattuja(1) > pelattuja(2)) return 2;
		return 1;
	}
	
	public void pelaa(int indeksi) {
		ruudukko.set(indeksi, vuorossa());
	}
	
	public int voittorivi(int indeksi1, int indeksi2, int indeksi3) {
		if (ruudukko.get(indeksi1) == 1
				&& ruudukko.get(indeksi2) == 1
				&& ruudukko.get(indeksi3) == 1) {
			return 1;
		}
		
		if (ruudukko.get(indeksi1) == 2
				&& ruudukko.get(indeksi2) == 2
				&& ruudukko.get(indeksi3) == 2) {
			return 2;
		}
		
		return 0;
	}
	
	public int voittaja() {
		//rivit
		for (int rivi = 0; rivi < 3; rivi++) {
			if (voittorivi(3 * rivi, 3 * rivi + 1, 3 * rivi + 2) > 0) {
				return voittorivi(3 * rivi, 3 * rivi + 1, 3 * rivi + 2);
			}
		}
		
		//sarakkeet
		for (int sarake = 0; sarake < 3; sarake++) {
			if (voittorivi(sarake, 3 + sarake, 6 + sarake) > 0) {
				return voittorivi(sarake, 3 + sarake, 6 + sarake);
			}
		}
		
		//diagonaalit
		if (voittorivi(0, 4, 8) > 0) {
			return voittorivi(0, 4, 8);
		}
		if (voittorivi(2, 4, 6) > 0) {
			return voittorivi(2, 4, 6);
		}
		
		//ei voittajaa
		return 0;
	}
	
}
