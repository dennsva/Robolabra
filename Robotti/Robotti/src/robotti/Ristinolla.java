package robotti;

import java.util.ArrayList;

import lejos.nxt.Button;

public class Ristinolla {
	
	//ruudukon indeksit:
	// 0 1 2
	// 3 4 5
	// 6 7 8
	
	// 0 tarkoittaa tyhjää
	// 1 on pelaaja 1
	// 2 on pelaaja 2
	
	private ArrayList<Integer> ruudukko;
	private Ristinollapiirturi piirturi;
	private Pelaaja pelaaja1;
	private Pelaaja pelaaja2;
	
	public Ristinolla(Pelaaja pelaaja1, Pelaaja pelaaja2) {
		this.ruudukko = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			ruudukko.add(0);
		}
		this.piirturi = new Ristinollapiirturi();
		this.pelaaja1 = pelaaja1;
		this.pelaaja2 = pelaaja2;
	}
	
	public ArrayList<Integer> getRuudukko() {
		return ruudukko;
	}
	
	public void aloita() {
		piirturi.luoKentta(0, 0, 300, 300);
		piirturi.piirraKentta();
		while (peliKaynnissa()) {
			pelaa();
		}
		if (voittaja() > 0) {
			viivaaVoittaja();
			System.out.println("Voittaja on " + merkki(voittaja()));
		} else {
			System.out.println("Tasapeli!");
		}
		Button.ENTER.waitForPressAndRelease();
		piirturi.nollaaRobotti();
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
		for (int i = 0; i < ruudukko.size(); i++) {
			if (ruudukko.get(i) == pelaaja) pelattuja++;
		}
		return pelattuja;
	}
	
	public int vuorossa() {
		if (pelattuja(1) > pelattuja(2)) return 2;
		return 1;
	}
	
	public Pelaaja vuorossaPelaaja() {
		if (vuorossa() == 2) return pelaaja2;
		return pelaaja1;
	}
	
	public boolean pelaa() {
		int indeksi = vuorossaPelaaja().pelaa(ruudukko);
		if (indeksi < 0 || indeksi >= 9) return false;
        if (voittaja() > 0) return false;
        if (ruudukko.get(indeksi) > 0) return false;
        
        piirturi.piirraMerkki(indeksi, vuorossa());
        ruudukko.set(indeksi, vuorossa());
        return true;
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
		return voittaja(false);
	}
	
	public int viivaaVoittaja() {
		return voittaja(true);
	}
	
	public int voittaja(boolean viivaa) {
		//rivit
		if (voittorivi(0, 1, 2) > 0) {
			if (viivaa) piirturi.viivaaRivi(0);
			return voittorivi(0, 1, 2);
		}
		if (voittorivi(3, 4, 5) > 0) {
			if (viivaa) piirturi.viivaaRivi(1);
			return voittorivi(3, 4, 5);
		}
		if (voittorivi(6, 7, 8) > 0) {
			if (viivaa) piirturi.viivaaRivi(2);
			return voittorivi(6, 7, 8);
		}
		
		//sarakkeet
		if (voittorivi(0, 3, 6) > 0) {
			if (viivaa) piirturi.viivaaRivi(3);
			return voittorivi(0, 3, 6);
		}
		if (voittorivi(1, 4, 7) > 0) {
			if (viivaa) piirturi.viivaaRivi(4);
			return voittorivi(1, 4, 7);
		}
		if (voittorivi(2, 5, 8) > 0) {
			if (viivaa) piirturi.viivaaRivi(5);
			return voittorivi(2, 5, 8);
		}
		
		//diagonaalit
		if (voittorivi(0, 4, 8) > 0) {
			if (viivaa) piirturi.viivaaRivi(6);
			return voittorivi(0, 4, 8);
		}
		if (voittorivi(2, 4, 6) > 0) {
			if (viivaa) piirturi.viivaaRivi(7);
			return voittorivi(2, 4, 6);
		}
		
		//ei voittajaa
		return 0;
	}
	
	public boolean peliKaynnissa() {
		if (voittaja() == 0 && pelattuja(1) + pelattuja(2) < 9) return true;
		return false;
	}
	
}
