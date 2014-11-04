package com.ma.schiffeversenken.model;

public interface Schiff {
	/*
	 * Interface für alle Schiffe
	 */

	int size = 0;
	String name = null;

	
	public abstract int getSize();
	public abstract String getName();


}
