package com.ma.schiffeversenken.model;



public class MaikTestKlasse {
	//Eine Klasse zum testen
	
	public MaikTestKlasse(){
		Schiff[] fleet =
		    {
		      new Kreuzer("E"),
		      new Uboot("f")
		    };
		
		Zerst�rer z = new Zerst�rer("Zerst�rer");
		z.getName();
	}
	
}
