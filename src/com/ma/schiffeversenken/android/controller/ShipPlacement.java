package com.ma.schiffeversenken.android.controller;

import java.util.ArrayList;
import java.util.Random;

import com.ma.schiffeversenken.android.model.*;

/**
 * Steuert das automatische Platzieren der Schiffe fuer beide Spiele
 * 
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class ShipPlacement {
	/** Anzahl der Bloecke */
	public static final int NUMBER_OF_BLOCKS = 25;
	/**Wird bei einem JUnit Test auf True gesetzt*/
	public static boolean jUnitTest = false;
	
	/**
	 * Das Spielfeld wird in 25 Bloecke aufgeteilt. Die Bloecke werden in dieser
	 * Variable gespeichert
	 */
	Block blocks[];
	/** Das Spielfeld */
	Field t;

	/**
	 * Erstellt das ShipPlacement Objekt
	 */
	public ShipPlacement() {
		blocks = new Block[NUMBER_OF_BLOCKS];
	}

	/**
	 * Platziert die Schiffe automatisch auf dem Spielfeld Erst das Erstellen
	 * von ships, danach das Platzieren in field
	 * 
	 * @param field
	 * @param ships
	 */
	public void placeShips(Field field, Ship[] ships) {
		this.t = field;

		createBlocks(field);
		Random random = new Random();
		// Erstellen der Schiffe danach erst im field setzen.
		for (Ship ship : ships) {
			int randomID = 0;
			int orientation = 0;
			int run = 0;
			boolean ok = false;

			do {
				do {
					// Pruefen ob der Block schon belegt ist.
					// Zufaellige Zahl erstellen
					randomID = random.nextInt(Field.FIELD_SIZE) + 1;
				} while (!checkBlock(randomID, ship));

				do {
					// Pruefen ob das FeldElement schon belegt ist
					orientation = random
							.nextInt(Ship.NUMBER_OF_ORIENTATIOS);
					run++;
					ok = checkEinheit(orientation, ship, randomID, field);
				} while (!ok && run < 4);
			} while (!ok);

			// Wenn die gewuenschten Felder frei sind, kann hier jetzt das
			// Schiff platziert werden..
			field.getElementByID(randomID).setOccupied(true);
			FieldUnit tempElement = field.getElementByID(randomID);
			tempElement.setOccupied(true);
			// TODO: X\Y Koordinate an Schiff uebergeben, wenn belegt
			tempElement.placeShip(ship, Ship.SHIP_SEGMENT_BACK);
			ship.setStandort(tempElement, 0, orientation);
			int schiffSize = ship.getSize();

			if (schiffSize > 1) {
				if (orientation == Ship.SHIP_ORIENTATION_RIGHT) {
					markElements(1, schiffSize, randomID, field, ship,
							orientation);
				} else if (orientation == Ship.SHIP_ORIENTATION_ABOVE) {
					markElements(-10, schiffSize, randomID, field, ship,
							orientation);
				} else if (orientation == Ship.SHIP_ORIENTATION_LEFT) {
					markElements(-1, schiffSize, randomID, field, ship,
							orientation);
				} else if (orientation == Ship.SHIP_ORIENTATION_BELOW) {
					markElements(10, schiffSize, randomID, field, ship,
							orientation);
				}
			}
		}

		if(!jUnitTest){
			// Setzen von ships
			field.setShips(ships);
		}
	}

	/**
	 * Platziert die Schiffe die uebergeben werden auf dem Spielfeld Erst das
	 * Erstellen von ships, danach das Platzieren in field
	 * 
	 * @param field Feld auf dem Platziert werden soll
	 * @param placedShipUnits Schiffe, die platziert werden sollen
	 */
	public void placeShipsManual(Field field,
			ArrayList<FieldUnit[]> placedShipUnits) {
//		createBlocks(field);
		// Erstellen der Schiffe
		Ship[] ships = new Ship[placedShipUnits.size()];

		for (int i=0;i<placedShipUnits.size();i++) {
			FieldUnit[] u=placedShipUnits.get(i);
			
			//Initialisierung der Schiffe
			switch (u.length - 1) {
			case 0:
				ships[i]= new Ship(Ship.CRUISER_SIZE,u);
				for(FieldUnit unit:u){
					unit.setOccupied(true); 
					unit.placeShip(ships[i], Ship.SHIP_SEGMENT_BACK);
				}
				break;
			case 1:
				ships[i] = new Ship(Ship.SUBMARINE_SIZE,u);
				for(FieldUnit unit:u){
					unit.setOccupied(true);
					unit.placeShip(ships[i], Ship.SHIP_SEGMENT_BACK);
				}
				break;
			case 2:
				ships[i] = new Ship(Ship.DESTROYER_SIZE,u);
				for(FieldUnit unit:u){
					unit.setOccupied(true);
					unit.placeShip(ships[i], Ship.SHIP_SEGMENT_BACK);
				}
				break;
			case 3:
				ships[i] = new Ship(Ship.BATTLESHIP_SIZE,u);
				for(FieldUnit unit:u){
					unit.setOccupied(true);
					unit.placeShip(ships[i], Ship.SHIP_SEGMENT_BACK);
				}
				break;
			default:
				break;
			}
			
			//Fals ein Kreuzer mit einem Feld
			u[0].setOccupied(true);
			u[0].placeShip(ships[i], Ship.SHIP_SEGMENT_BACK);
			ships[i].setStandortManual(u,u[0].getShipOrientation());
			int schiffSize = u.length;
			
			if (schiffSize > 1) {
				if (ships[i].getOrientation() == Ship.SHIP_ORIENTATION_RIGHT) {
					markElements(1, schiffSize, u[0].getID(), field, ships[i],
							ships[i].getOrientation());
				} else if (ships[i].getOrientation() == Ship.SHIP_ORIENTATION_ABOVE) {
					markElements(10, schiffSize, u[0].getID(), field, ships[i],
							ships[i].getOrientation());
				} else if (ships[i].getOrientation() == Ship.SHIP_ORIENTATION_LEFT) {
					markElements(-1, schiffSize, u[0].getID(), field, ships[i],
							ships[i].getOrientation());
				} else if (ships[i].getOrientation() == Ship.SHIP_ORIENTATION_BELOW) {
					markElements(-10, schiffSize, u[0].getID(), field, ships[i],
							ships[i].getOrientation());
				}
			}
			
			
		}

		// Setzen von ships
		field.setShipsManual(ships);

	}

	/**
	 * FeldElement als markiert markieren und das Schiff setzen
	 * 
	 * @param counter
	 *            Gibt an in welche Richtung das Schiff platziert wurde (oben,
	 *            unten, rechts, links)
	 * @param size
	 *            Groesse des Schiffs
	 * @param id
	 *            ID des Startfeldes
	 * @param field
	 *            Spielfeld
	 * @param ship
	 *            Das zu platzierende Schiff
	 * @param orientation
	 *            Die Orientierung des Schiffs (nach oben/unten/rechts/links
	 *            gerichtet)
	 */
	private void markElements(int counter, int size, int id, Field field,
			Ship ship, int orientation) {
		int temp = 0;
		int shipSegment = Ship.SHIP_SEGMENT_MIDDLE;
		int finalCounter = 0;
		for (int i = 1; i < size; i++) {
			finalCounter = counter * i;
			temp = id + finalCounter;
			if (temp > 0 && temp < 101) {
				FieldUnit tempElement = field.getElementByID(temp);
				if (i == (size - 1))
					shipSegment = Ship.SHIP_SEGMENT_FRONT;
				tempElement.setOccupied(true);
				tempElement.placeShip(ship, shipSegment);
				ship.setStandort(tempElement, i, orientation);
			}
		}
	}

	/**
	 * Prueft ob die benoetigten Felder frei oder belegt sind
	 * 
	 * @param orientation
	 *            Gibt an in welcher Richtung das Schiff platziert werden soll
	 *            (oben, unten, rechts, links)
	 * @param ship
	 *            Das zu platzierende Schiff
	 * @param id
	 *            Die ID des Startfeldes
	 * @param field
	 *            Spielfeld
	 * @return Gibt zurueck ob die Felder frei oder belegt sind
	 */
	private boolean checkEinheit(int orientation, Ship ship, int id, Field field) {
		int size = ship.getSize();
		boolean ret = true;
		FieldUnit fu = field.getElementByID(id);
		
		if (fu.getOccupied()) {
			ret = false;
		} else {
			if (orientation == Ship.SHIP_ORIENTATION_RIGHT) {
				// Nach rechts gerichtetes Schiff
				ret = checkID(1, size, id, field);
			} else if (orientation == Ship.SHIP_ORIENTATION_ABOVE) {
				// Nach oben gerichtetes Schiff
				ret = checkID(-10, size, id, field);
			} else if (orientation == Ship.SHIP_ORIENTATION_LEFT) {
				// Nach links gerichtetes Schiff
				ret = checkID(-1, size, id, field);
			} else if (orientation == Ship.SHIP_ORIENTATION_BELOW) {
				// Nach unten gerichtetes Schiff
				ret = checkID(10, size, id, field);
			}
		}

		return ret;
	}

	/**
	 * Pr�ft eine Reihe von Feldern ob diese frei oder belegt sind
	 * 
	 * @param counter
	 *            Die ID wird mit dem counter multipliziert, um nach oben,
	 *            unten, rechts oder links zu gehen
	 * @param size
	 *            Groesse des Schiffs
	 * @param id
	 *            ID des Startfeldes
	 * @param field
	 *            Spielfeld
	 * @return Gibt zurueck ob die Felder frei oder belegt sind
	 */
	private boolean checkID(int counter, int size, int id, Field field) {
		boolean ret = true;
		int temp = 0;
		int finalCounter = 0;
		for (int i = 0; i < size; i++) {
			finalCounter = counter * i;
			temp = id + finalCounter;
			if (temp > 0 && temp < 101) {
				FieldUnit tempElement = field.getElementByID(temp);
				if (tempElement.getOccupied())
					ret = false;
				else if ((size - i) > 0) {
					if (checkEdge(counter, tempElement))
						ret = false;
				}
			} else
				ret = false;
		}
		return ret;
	}

	/**
	 * Prueft ob das Feld an einer Kante liegt
	 * 
	 * @param counter
	 *            Die ID wird mit dem counter multipliziert, um nach oben,
	 *            unten, rechts oder links zu gehen
	 * @param element
	 *            Das FeldElement, das uberprueft werden soll
	 * @return Gibt true zurueck, wenn das Element an einer Kante liegt
	 */
	private boolean checkEdge(int counter, FieldUnit element) {
		int kRichtung = 0;
		boolean ret = false;
		int id = element.getID();
		if ((id > 0 && id < 11) || (id > 89 && id < 101) || id == 11
				|| id == 21 || id == 31 || id == 41 || id == 51 || id == 61
				|| id == 71 || id == 81 || id == 20 || id == 30 || id == 40
				|| id == 50 || id == 60 || id == 70 || id == 80) {
		}

		if (counter == 1)
			kRichtung = FieldUnit.EDGE_RIGHT;
		else if (counter == -1)
			kRichtung = FieldUnit.EDGE_LEFT;
		else if (counter == 10)
			kRichtung = FieldUnit.EDGE_BELOW;
		else if (counter == -10)
			kRichtung = FieldUnit.EDGE_ABOVE;

		if (element.getEdge(1) == kRichtung || element.getEdge(2) == kRichtung)
			ret = true;

		return ret;
	}

	/**
	 * Sucht den passenden Block zu einer Feld-ID
	 * 
	 * @param id
	 *            Die ID, die gesucht wird
	 * @return Der Block, zu der gesuchten ID
	 */
	private Block getBlockById(int id) {
		for (Block b : blocks) {
			for (int i : b.getFieldUnits()) {
				if (i == id) {
					return b;
				}
			}
		}
		return null;
	}

	/**
	 * Prueft ob der Block belegt ist
	 * 
	 * @param random
	 *            Die ID des Blocks
	 * @param ship
	 *            Das zu platzierende Schiff
	 * @return Gibt zurueck ob der Block frei oder belegt ist
	 */
	private boolean checkBlock(int random, Ship ship) {
		boolean ret = true;

		try {
			if (getBlockById(random).getOccupied())
				ret = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	/**
	 * Teil das Spielfeld in 25 identische Bloecke auf
	 * 
	 * @param feld
	 *            Spielfeld
	 */
	private void createBlocks(Field feld) {
		int k = 0;
		FieldUnit[][] einheiten = feld.getFieldUnits();
		for (int i = 0; i < 10; i = i + 2) {
			for (int j = 0; j < 10; j = j + 2) {
				blocks[k] = new Block(einheiten[i][j].getID(),
						einheiten[i][j + 1].getID(),
						einheiten[i + 1][j].getID(),
						einheiten[i + 1][j + 1].getID());
				k++;
			}
		}
	}

}
