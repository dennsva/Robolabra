package robotti;

import lejos.nxt.Button;

public class Ristinollapiirturi extends Piirturi {

	private int[] x;
	private int[] y;
	
	/*
	 * y6 ----------
	 *    |  |  |  |
	 * y4 ----------
	 *    |  |  |  |
	 * y2 ----------
	 *    |  |  |  |
	 * y0 ----------
	 *    x0 x2 x4 x6
	 *    parittomat väleissä
	 */
	
	
	public Ristinollapiirturi() {
		super();
		this.x = new int[7];
		this.y = new int[7];
	}
	
	/**Tallentaa olioon tiedot kentasta.*/
	public void luoKentta(int x1, int y1, int x2, int y2) {
		if (!ruudukossa(x1, y1)) return;
		if (!ruudukossa(x2, y2)) return;
		
		//käännetty, sillä x-akseli kulkee oikealta vasemmalle
		x[0] = x2;
		x[1] = (5 * x2 + 1 * x1) / 6;
		x[2] = (4 * x2 + 2 * x1) / 6;
		x[3] = (3 * x2 + 3 * x1) / 6;
		x[4] = (2 * x2 + 4 * x1) / 6;
		x[5] = (1 * x2 + 5 * x1) / 6;
		x[6] = x1;
		
		y[0] = y1;
		y[1] = (5 * y1 + 1 * y2) / 6;
		y[2] = (4 * y1 + 2 * y2) / 6;
		y[3] = (3 * y1 + 3 * y2) / 6;
		y[4] = (2 * y1 + 4 * y2) / 6;
		y[5] = (1 * y1 + 5 * y2) / 6;
		y[6] = y2;
	}
	
	/**Piirtaa kentan. Kayta ensin metodia luoKentta()!*/
	public void piirraKentta() {
		asetaKorkeus();
		piirraViiva(x[0], y[4], x[6], y[4]);
		piirraViiva(x[0], y[2], x[6], y[2]);
		piirraViiva(x[2], y[0], x[2], y[6]);
		piirraViiva(x[4], y[0], x[4], y[6]);
		nostaKyna();
	}
	
	/**Piirtaa annetun merkin annettuun ruudukon indeksiin.*/
	public void piirraMerkki(int indeksi, int merkki) {
		if (indeksi == 0) piirraMerkki(merkki, x[0], y[4], x[2], y[6]);
		if (indeksi == 1) piirraMerkki(merkki, x[2], y[4], x[4], y[6]);
		if (indeksi == 2) piirraMerkki(merkki, x[4], y[4], x[6], y[6]);
		if (indeksi == 3) piirraMerkki(merkki, x[0], y[2], x[2], y[4]);
		if (indeksi == 4) piirraMerkki(merkki, x[2], y[2], x[4], y[4]);
		if (indeksi == 5) piirraMerkki(merkki, x[4], y[2], x[6], y[4]);
		if (indeksi == 6) piirraMerkki(merkki, x[0], y[0], x[2], y[2]);
		if (indeksi == 7) piirraMerkki(merkki, x[2], y[0], x[4], y[2]);
		if (indeksi == 8) piirraMerkki(merkki, x[4], y[0], x[6], y[2]);
	}
	
	/**Piirtaa annetut merkin ruutuun, jonka muodostaa annetut koordinaatit.*/
	public void piirraMerkki(int merkki, int x1, int y1, int x2, int y2) {
		if (merkki == 1) piirraRisti(x1, y1, x2, y2);
		if (merkki == 2) piirraNolla(x1, y1, x2, y2);
	}
	
	/**Piirtaa ristin ruutuun, jonka muodostaa annetut koordinaatit.*/
	public void piirraRisti(int x1, int y1, int x2, int y2) {
		piirraViiva(x1, y1, x2, y2);
		piirraViiva(x1, y2, x2, y1);
		nostaKyna();
	}
	
	/**Piirtaa nollan ruutuun, jonka muodostaa annetut koordinaatit.*/
	public void piirraNolla(int x1, int y1, int x2, int y2) {
		int xkeski = (x1 + x2) / 2;
		int ykeski = (y1 + y2) / 2;
		
		piirraViiva(x1, ykeski, xkeski, y2);
		piirraViiva(xkeski, y2, x2, ykeski);
		piirraViiva(x2, ykeski, xkeski, y1);
		piirraViiva(xkeski, y1, x1, ykeski);
		nostaKyna();
	}
	
	/**Piirtaa viivan annetun rivin yli. Indeksit seuraavasti:
	 * 0: rivi 1 ylhaalta
	 * 1: rivi 2
	 * 2: rivi 3
	 * 3: sarake 0 vasemmalta
	 * 4: sarake 1
	 * 5: sarake 2
	 * 6: diagonaali \
	 * 7: diagonaali /*/
	public void viivaaRivi(int rivi) {
		//rivit 0, 1, 2 (ylhäältä alas)
		if (rivi == 0) piirraViiva(x[0], y[5], x[6], y[5]);
		if (rivi == 1) piirraViiva(x[0], y[3], x[6], y[3]);
		if (rivi == 2) piirraViiva(x[0], y[1], x[6], y[1]);
		
		//sarakkeet 3, 4, 5 (vasemmalta oikealle)
		if (rivi == 3) piirraViiva(x[1], y[0], x[1], y[6]);
		if (rivi == 4) piirraViiva(x[3], y[0], x[3], y[6]);
		if (rivi == 5) piirraViiva(x[5], y[0], x[5], y[6]);
		
		//diagonaalit 6 (\), 7 (/)
		if (rivi == 6) piirraViiva(x[0], y[6], x[6], y[0]);
		if (rivi == 7) piirraViiva(x[0], y[0], x[6], y[6]);
		
		nostaKyna();
	}
	
}
