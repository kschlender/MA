package com.ma.schiffeversenken.model;

public abstract class Schiff {
	/*
	 * Interface f�r alle Schiffe
	 */
	
	int size; //L�nger des Schiffs
	String name; //Name des Schiffs
	FeldElement[] standort; //Felder auf denen das Schiff platziert ist
	
	public Schiff(String name, int size){
		this.name=name;
		this.size=size;
		this.standort = new FeldElement[size];
	}
	
	public int getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}

	public void setStandort(FeldElement element, int i){
		this.standort[i] = element;
	}
}
