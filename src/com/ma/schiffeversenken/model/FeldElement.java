package com.ma.schiffeversenken.model;

public class FeldElement {
	//Direkte Nachbar dieses Feldes
	FeldElement lNachbar, rNachbar, oNachbar, uNachbar;
	//Schiff, das auf diesem FeldElement steht
	Schiff platziertesSchiff;
	int id;
	boolean belegt;
	int kante1=0;
	int kante2=0;
	
	public void setKante(int kante, int wert){
		if(kante == 1) this.kante1 = wert;
		else if(kante == 2) this.kante2 = wert;
	}
	
	public int getKante(int kante){
		int ret = 0;
		if(kante == 1) ret = this.kante1;
		else if(kante == 2) ret = this.kante2;
		
		return ret;
	}
	
	public FeldElement(int id){
		this.id=id;
		this.belegt = false;
	}

	public void setNeighbors(FeldElement lNachbar, FeldElement rNachbar, FeldElement oNachbar, FeldElement uNachbar){
		//Weißt diesem FeldElement seine direkten Nachbarn zu
		this.lNachbar = lNachbar;
		this.rNachbar = rNachbar;
		this.oNachbar = oNachbar;
		this.uNachbar = uNachbar;
	}
	
	public int getID(){
		return id;
	}
	
	public void platziereSchiff(Schiff schiff){
		this.platziertesSchiff = schiff;
	}
	
	public Schiff getPlatziertesSchiff(){
		return this.platziertesSchiff;
	}
	
	public boolean getBelegt(){
		return belegt;
	}
	
	public void setBelegt(boolean belegt){
		this.belegt = belegt;
	}
}
