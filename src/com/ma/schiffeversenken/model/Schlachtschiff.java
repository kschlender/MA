package com.ma.schiffeversenken.model;

public class Schlachtschiff implements Schiff {
	
	private String name="Schlachtschiff";
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}
}
