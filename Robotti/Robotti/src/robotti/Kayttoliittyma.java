package robotti;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;

public class Kayttoliittyma {

	private boolean napitAktivoitu;
	private int indeksi;
	private Piirturi piirturi;
	
	public Kayttoliittyma() {
		this.indeksi = 0;
		this.napitAktivoitu = false;
		this.piirturi = new Piirturi();
		
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (napitAktivoitu) {
					indeksi++;
					if (indeksi == 6) indeksi = 0;
					tulostaOhje(indeksi);
				}
			}
			
			public void buttonReleased(Button b) {
				
			}
		});
		
		Button.LEFT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (napitAktivoitu) {
					indeksi--;
					if (indeksi == -1) indeksi = 5;
					tulostaOhje(indeksi);
				}
			}
			
			public void buttonReleased(Button b) {
				
			}
		});
	}
	
	public void kaynnista() {
		indeksi = 0;
		tulostaOhje(indeksi);
		napitAktivoitu = true;
		Button.ENTER.waitForPressAndRelease();
		napitAktivoitu = false;
		
		if (indeksi == 0) {
			piirra();
		} else if (indeksi == 1) {
			ristinollaIhmisilla();
		} else if (indeksi == 2) {
			ristinollaIhminenJaSatunnainen();
		} else if (indeksi == 3) {
			ristinollaSatunnaisilla();
		} else if (indeksi == 4) {
			ristinollaTesti();
		} else if (indeksi == 5) {
			nollaa();
		}
	}
	
	public void tulostaOhje(int indeksi) {
		LCD.clear();
		System.out.println("Valitse toiminto");
		System.out.println("ja paina enter");
		System.out.println(" ");
		
		if (indeksi == 0) {
			System.out.println("Vapaa");
			System.out.println("piirtaminen");
			System.out.println(" ");
		} else if (indeksi == 1) {
			System.out.println("Ristinolla");
			System.out.println("Pelaaja vastaan");
			System.out.println("pelaaja");
		} else if (indeksi == 2) {
			System.out.println("Ristinolla");
			System.out.println("Pelaaja vastaan");
			System.out.println("kone");
		} else if (indeksi == 3) {
			System.out.println("Ristinolla");
			System.out.println("Kone vastaan");
			System.out.println("kone");
		} else if (indeksi == 4) {
			System.out.println("Ristinolla");
			System.out.println("Testi");
			System.out.println("ei piirreta");
		} else if (indeksi == 5) {
			System.out.println("Manuaalinen");
			System.out.println("nollaus");
			System.out.println(" ");
		}
	}
	
	/**Vapaa piirto-ohjelma!*/
	public void piirra() {
		piirturi.asetaKorkeus();
		piirturi.vapaaPiirtaminen();
		piirturi.nollaaRobotti();
	}
	
	/**Robotin nollaus manuaalisesti.*/
	public void nollaa() {
		piirturi.manuaalinenNollaus();
	}
	
	/**Kaynnistaa ristinollapelin, jossa piirturi on pois kaytosta.*/
	public void ristinollaTesti() {
		ristinollaIhmisilla(false);
	}
	
	/**Ristinollapeli, jossa kaksi kayttajaa pelaavat vastakkain.*/
	public void ristinollaIhmisilla() {
		ristinollaIhmisilla(true);
	}
	
	private void ristinollaIhmisilla(boolean piirretaan) {
		Pelaaja pelaaja1 = new Ihmispelaaja(1);
		Pelaaja pelaaja2 = new Ihmispelaaja(2);
		
		Ristinolla peli = new Ristinolla(pelaaja1, pelaaja2, piirretaan);
		peli.aloita();
	}
	
	/**Ristinollapeli, jossa kone arpoo molempien pelaajien siirrot.*/
	public void ristinollaSatunnaisilla() {
		Pelaaja pelaaja1 = new Satunnaispelaaja(1);
		Pelaaja pelaaja2 = new Satunnaispelaaja(2);
		
		Ristinolla peli = new Ristinolla(pelaaja1, pelaaja2);
		peli.aloita();
	}
	
	/**Ristinollapeli, jossa kayttaja pelaa satunnaisesti pelaavaa konetta vastaan.*/
	public void ristinollaIhminenJaSatunnainen() {
		Pelaaja pelaaja1 = new Ihmispelaaja(1);
		Pelaaja pelaaja2 = new Satunnaispelaaja(2);
		
		Ristinolla peli = new Ristinolla(pelaaja1, pelaaja2);
		peli.aloita();
	}
	
}
