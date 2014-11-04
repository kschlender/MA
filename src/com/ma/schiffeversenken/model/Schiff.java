package com.ma.schiffeversenken.model;

public abstract class Schiff {
	/*
	 * Interface f�r alle Schiffe
	 */
	
	int size; //L�nger des Schiffs
	String name; //Name des Schiffs
	FeldElement[] standort; //Felder auf denen das Schiff platziert ist
	
	public Schiff(String name){
		this.name=name;
		standort = new FeldElement[size];
	}
	
	public int getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}

	
}
