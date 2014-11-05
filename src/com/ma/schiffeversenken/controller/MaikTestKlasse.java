package com.ma.schiffeversenken.controller;

import com.ma.schiffeversenken.model.*;



public class MaikTestKlasse {
	public MaikTestKlasse(){
		//Eine Klasse zum testen
		
		Zerst�rer z = new Zerst�rer("Zerst�rer");
		Uboot u = new Uboot("Uboot");
		
		Schiff[] schiffe = new Schiff[]{z, u};
		Spielfeld feld = new Spielfeld(0);
		
		KI ki = new KI();
		ki.platziereSchiffe(feld, schiffe);
	}
}
