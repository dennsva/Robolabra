package robotti;

import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.TouchSensor;

public class Ihmispelaaja implements Pelaaja {

	private int pelaaja;
	private int indeksi;
	private boolean napitAktivoitu;
	
	public Ihmispelaaja(int pelaaja) {
		this.pelaaja = pelaaja;
		this.indeksi = 0;
		this.napitAktivoitu = false;
		
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				if (napitAktivoitu) {
					indeksi++;
					if (indeksi == 9) indeksi = 0;
	
					/*
					LCD.clear();
					System.out.println("pelaaja " + pelaaja);
					System.out.println("Valitse pelattava");
					System.out.println("ruutu ja paina");
					System.out.println("enter");
					*/
					
					System.out.println(indeksi);
				}
			}
			
			public void buttonReleased(Button b) {
				
			}
		});
	}
	
	public int pelaa(ArrayList<Integer> ruudukko) {
		indeksi = 0;
		
		//LCD.clear();
		System.out.println("pelaaja " + pelaaja);
		System.out.println("Valitse pelattava");
		//System.out.println("ruutu ja paina");
		//System.out.println("enter");
		System.out.println(ruudukko);
		System.out.println(indeksi);
		
		napitAktivoitu = true;
		Button.ENTER.waitForPressAndRelease();
		napitAktivoitu = false;
		return indeksi;
	}
	
	public void paivitaIndeksi(int indeksi) {
		this.indeksi = indeksi;
	}
	
}
