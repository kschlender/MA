package com.ma.schiffeversenken.controller;

public class Block {
	int felder[];
	boolean belegt;
	
	public Block(int feld1, int feld2, int feld3, int feld4){
		felder = new int[]{feld1, feld2, feld3, feld4};
		belegt = false;
	}
	
	public int[] getFelder(){
		return felder;
	}
	
	public boolean getBelegt(){
		return belegt;
	}
}
