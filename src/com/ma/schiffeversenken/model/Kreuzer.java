package com.ma.schiffeversenken.model;

public class Kreuzer implements Schiff {
	private String name = "Kreuzer";

	String hallo = "test";
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

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

}
