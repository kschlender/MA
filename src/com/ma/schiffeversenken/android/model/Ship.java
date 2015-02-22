package com.ma.schiffeversenken.android.model;


/**
 * Fuer alle Schiffe
 * 
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class Ship {
	/** Vorderes Schiff Segment */
	public static final int SHIP_SEGMENT_FRONT = 0;
	/** Mittleres Schiff Segment */
	public static final int SHIP_SEGMENT_MIDDLE = 1;
	/** Hinteres Schiff Segment */
	public static final int SHIP_SEGMENT_BACK = 2;
	/**Anzahl der moeglichen Ausrichtungen eines Schiffs*/
	public static final int NUMBER_OF_ORIENTATIOS = 4;
	/**Ausrichtung des Schiffs nach rechts*/
	public static final int SHIP_ORIENTATION_RIGHT = 0;
	/**Ausrichtung des Schiffs nach oben*/
	public static final int SHIP_ORIENTATION_ABOVE = 1;
	/**Ausrichtung des Schiffs nach links*/
	public static final int SHIP_ORIENTATION_LEFT = 2;
	/**Ausrichtung des Schiffs nach unten*/
	public static final int SHIP_ORIENTATION_BELOW = 3;
	/**Groesse des Kreuzer*/
	public static final int CRUISER_SIZE = 1;
	/**Groesse des Uboot*/
	public static final int SUBMARINE_SIZE = 2;
	/**Groesse des Zerstoerer*/
	public static final int DESTROYER_SIZE = 3;
	/**Groesse des Schlachtschiff*/
	public static final int BATTLESHIP_SIZE = 4;
	/** Laenge des Schiffs */
	int size;
	/** Boolean ob das Schiff zerstoert ist */
	boolean destroyed=false;
	/** Felder auf denen das Schiff platziert ist */
	FieldUnit[] location;
	/** Ausrichtung des Schiffs: 0=rechts, 1=oben, 2=links, 3=unten */
	private int orientation;


	/**
	 * Erstellt ein Ship Objekt
	 * 
	 * @param size
	 *            Groesse des Schiffs
	 */
	public Ship(int size) {
		this.size = size;
		this.destroyed = false;
		this.location = new FieldUnit[size];

	}
	
	/**
	 * Erstellt ein Ship Objekt mit Location
	 * 
	 * @param size
	 *            Groesse des Schiffs
	 * @param location
	 *            Gebiet der Schiffsteile
	 */
	public Ship(int size, FieldUnit[] location) {
		this.size = size;
		this.destroyed = false;
		this.location = location;

	}
	
	public void setOrientation(int orientation){
		this.orientation = orientation;
	}
	
	/**
	 * Gibt die Orientierung des Schiffs zurueck Ausrichtung des Schiffs:
	 * 0=rechts, 1=oben, 2=links, 3=unten
	 * 
	 * @return Die Orientierung des Schiffs
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Setzt einen Boolean ob das Schiff zerstoert ist
	 * 
	 * @param destroyed
	 *            true oder false ob das Schiff zerstoert ist
	 */
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	/**
	 * Gibt zurueck ob das Schiff zerstoert ist
	 * 
	 * @return true oder false ob das Schiff zerstoert ist
	 */
	public boolean isDestroyed() {
		return this.destroyed;
	}

	/**
	 * Gibt die Groesse des Schiffs zurueck
	 * 
	 * @return Die Groesse des Schiffs
	 */
	public int getSize() {
		return size;
	}

//	/**
//	 * Gibt den Namen des Schiffs zurueck
//	 * 
//	 * @return Der Name des Schiffs
//	 */
//	public String getName() {
//		return name;
//	}

	/**
	 * Setzt den Standort des Schiffs
	 * 
	 * @param location
	 *            Das FeldElement[] Gebiet
	 * @param orientation
	 *            Die Orientierung des Schiffs (nach oben/unten/rechts/links
	 *            gerichtet)
	 */
	public void setStandortManual(FieldUnit[] location, int orientation) {
		this.location = location;
		this.orientation = orientation;
	}
	
	/**
	 * Setzt den Standort des Schiffs
	 * 
	 * @param element
	 *            Das FeldElement
	 * @param i
	 *            Indizes der FeldElemente, auf denen das Schiff steht
	 * @param orientation
	 *            Die Orientierung des Schiffs (nach oben/unten/rechts/links
	 *            gerichtet)
	 */
	public void setStandort(FieldUnit element, int i, int orientation) {
		this.location[i] = element;
		this.orientation = orientation;
	}

	/**
	 * Gibt den Standort des Schiffs zurueck
	 * 
	 * @return Der Standort des Schiffs
	 */
	public FieldUnit[] getLocation() {
		return this.location;
	}





}
