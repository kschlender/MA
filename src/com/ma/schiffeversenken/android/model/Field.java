package com.ma.schiffeversenken.android.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ma.schiffeversenken.GameFieldScreen;
import com.ma.schiffeversenken.MyGdxGameField;

/**
 * Das Spielfeld
 * 
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class Field {
	/**
	 * einheiten = Das Spielfeld besteht aus 10x10 Einheiten einheiten[y-Achse
	 * (Zeile)][x-Achse (Spalte)]
	 */
	FieldUnit[][] units = new FieldUnit[10][10];
	/**
	 * Menge von platzierten Schiffen auf diesem Spielfeld
	 */
	Ship[] placedShips;
	/**
	 * typ = Gibt den Typ des Spielfelds an. Eigenes Spielfeld = 0; Gegnerisches
	 * Spielfeld = 1;
	 */
	int typ;
	
	int size=GameFieldScreen.size;
	// graphics High and width
	private int h = Gdx.graphics.getHeight();
	private int w = Gdx.graphics.getWidth();

	/**
	 * Erstellt ein Field Objekt
	 * 
	 * @param typ
	 *            Typ des Spielfelds (0=Eigenes Spielfeld, 1=Gegnerisches
	 *            Spielfeld)
	 */
	public Field(int typ) {
		try {
			this.typ = typ;
			create();
			createNeighbors();
			createKante();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Setzt die platzierten Schiffe
	 * 
	 * @param ships
	 *            Schiffe, die zu diesem Spielfeld zugeordnet werden sollen
	 */
	public void setShips(Ship[] ships) {
		this.placedShips = ships;
	}

	/**
	 * Gibt die platzierten Schiffe zurueck
	 * 
	 * @return Die platzierten Schiffe
	 */
	public Ship[] getShips() {
		return this.placedShips;
	}

	/**
	 * Gibt die FeldEinheiten zurueck
	 * 
	 * @return Die FeldEinheiten
	 */
	public FieldUnit[][] getFieldUnits() {
		return units;
	}

	/**
	 * Gibt das FeldElement passend zu der ID zurueck
	 * 
	 * @param id
	 *            Die gesuchte ID
	 * @return Das FeldElement passend zu der ID
	 */
	public FieldUnit getElementByID(int id) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (units[i][j].getID() == id)
					return units[i][j];
			}
		}
		return null;
	}

	/**
	 * Erstellt das Spielfeld, das aus 10x10 Feldelementen besteht Hierbei wird
	 * auch die Position je Feld in der Scene �bergeben.
	 */
	private void create() {
		// TODO Optimierung der Drawables
		int xverschiebung = size;
		int yverschiebung = size;
		if (this.typ == 1) {
			yverschiebung *= 11;
		}
		int xpos = 0;
		int ypos = 0;
		int id = 0;

		for (int i = 0; i < 10; i++) {
			if (i == 0) {
				ypos = i * xverschiebung;
			} else {
				ypos = i * xverschiebung;
			}
			for (int j = 0; j < 10; j++) {
				id++;
				
				// TODO Testen auf funktion beim Zeichen
				units[i][j] = new FieldUnit(id, (xpos + j * xverschiebung),
						ypos + yverschiebung);
			}
		}
		
		//Verschiebung in die scene
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				units[i][j].setXpos(units[i][j].getXpos()-w-size);
				units[i][j].setYpos(units[i][j].getYpos()-h*1.8f-size);	
				//Debugging
				System.out.println("Field Erstellt: x:"+units[i][j].getXpos()
						+" y:"+units[i][j].getYpos());
			}
		}
	}

	/**
	 * Markiert, dass dieses FeldElement an einer Kante platziert ist und in
	 * welcher Richtung die Kante liegt
	 */
	private void createKante() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				FieldUnit e = units[i][j];
				int id = e.getID();
				if (id < 11) {
					// Obere Kante
					if (e.getEdge(1) == 0)
						e.setEdge(1, 1);
					else
						e.setEdge(2, 1);
				}
				if (id > 89) {
					// Untere Kante
					if (e.getEdge(1) == 0)
						e.setEdge(1, 2);
					else
						e.setEdge(2, 2);
				}
				if ((id - (10 * (i + 1))) == 0) {
					// Rechte Kante
					if (e.getEdge(1) == 0)
						e.setEdge(1, 3);
					else
						e.setEdge(2, 3);
				}
				if ((id - (10 * i)) == 1) {
					// Linke Kante
					if (e.getEdge(1) == 0)
						e.setEdge(1, 4);
					else
						e.setEdge(2, 4);
				}
			}
		}
	}

	/**
	 * Weist jedem Feldelement seine direkten Nachbarn zu
	 */
	private void createNeighbors() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				FieldUnit lNeighbor = null, rNeighbor = null, oNeighbor = null, uNeighbor = null;

				if (i > 1 && i < 10) {
					oNeighbor = units[i - 1][j];
				}
				if (i > 0 && i < 9) {
					uNeighbor = units[i + 1][j];
				}

				if (j > 1 && j < 10) {
					lNeighbor = units[i][j - 1];
				}

				if (j > 0 && j < 9) {
					rNeighbor = units[i][j + 1];
				}

				units[i][j].setNeighbors(lNeighbor, rNeighbor, oNeighbor,
						uNeighbor);
			}
		}
	}

	
	
	/**
	 * Iteriert �ber alle Feldelemente die gezeichnet werden.
	 * 
	 * @param batch
	 *            SpriteBatch f�rs Zeichnen
	 * @param atlas
	 */
	public void draw(SpriteBatch batch, TextureAtlas atlas) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				// TODO Drawing
				units[i][j].draw(batch, atlas, size);
			}
		}
	}
}
