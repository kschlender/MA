package com.ma.schiffeversenken.android.model;

/**
 * Ein Block von 4 FeldElementen
 * @author Maik Steinborn
 */
public class Block {
	int fieldUnits[];
	boolean occupied;
	
	/**
	 * Erstellt ein Block Objekt
	 * @param field1 FeldElement 1
	 * @param field2 FeldElement 2
	 * @param field3 FeldElement 3
	 * @param field4 FeldElement 4
	 */
	public Block(int field1, int field2, int field3, int field4){
		fieldUnits = new int[]{field1, field2, field3, field4};
		occupied = false;
	}
	
	/**
	 * Gibt alle FeldElemente des Blocks zurueck
	 * @return Alle FeldElemente des Blocks
	 */
	public int[] getFieldUnits(){
		return fieldUnits;
	}
	
	/**
	 * Gibt zurueck ob der Block frei oder belegt ist
	 * @return true oder false ob frei oder belegt
	 */
	public boolean getOccupied(){
		return occupied;
	}
}
