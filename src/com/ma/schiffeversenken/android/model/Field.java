package com.ma.schiffeversenken.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.ma.schiffeversenken.EntityShip;
import com.ma.schiffeversenken.android.controller.KI;
import com.ma.schiffeversenken.android.controller.ShipPlacement;

/**
 * Das Spielfeld
 * 
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class Field {
	/** Rechte Kante */
	public static final int EDGE_RIGHT = 0;
	/** Obere Kante */
	public static final int EDGE_ABOVE = 1;
	/** Linke Kante */
	public static final int EDGE_LEFT = 2;
	/** Untere Kante */
	public static final int EDGE_BELOW = 3;
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

	// Boolean ob alle Schiffe gesetzt sind und Feld bereit ist.
	private boolean allShipsSet = false;
	public ShipPlacement sp;

	// Einheitsgröße der Texturen
	float size;
	// graphics High and width
	private int h;
	private int w;
	private TiledMapTileLayer mapTileLayer;
	// TODO EntityShip or Tile
	private ArrayList<EntityShip> drawShips;
	private Iterator<EntityShip> tileIterator;
	private TreeMap<String, TextureRegion> shipTextures;
	private TreeMap<String, TiledMapTile> shipTiles;
	private int animationtimerMax = 10;
	private boolean allShipsSetManual;

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
			create(true);
			// createNeighbors();
			createKante();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Überladener Konstruktor erstellt ein Field Objekt
	 * 
	 * @param typ
	 *            Typ des Spielfelds (0=Eigenes Spielfeld, 1=Gegnerisches
	 *            Spielfeld)
	 * @param shipTextures
	 *            SchiffsTexturen aus TiledMapTileSet
	 * @param mtl
	 *            Tilemap Layer aus TiledMapTileLayer
	 */
	public Field(int typ, TiledMapTileSet shipTextures, TiledMapTileLayer mtl) {
		try {
			h = Gdx.graphics.getHeight();
			w = Gdx.graphics.getWidth();
			this.typ = typ;
			this.mapTileLayer = mtl;
			this.size = mtl.getTileHeight();
			this.allShipsSetManual = false;
			this.allShipsSet = false;
			drawShips = new ArrayList<EntityShip>();
			getShipTileSetTextures(shipTextures);
			create();
			createNeighbors();
			createKante();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Diese Methode speichert alle Schiffstexturen in einer Treemap
	 * 
	 * @param st
	 *            TiledMapTileSet wo die Texturen gespeichert sind
	 */
	private void getShipTileSetTextures(TiledMapTileSet st) {

		// DEPRECATED START
		// int colTexture = 5;
		// int rowTexture = 6;
		//
		// //Holen der einzelenn Schifftexturen aus der Gesamten Textur in ein
		// 2D Array.
		// Texture tmpST = st.iterator().next().getTextureRegion().getTexture();
		// TextureRegion[][] tmp = TextureRegion.split(tmpST,
		// tmpST.getWidth()/colTexture, tmpST.getHeight()/rowTexture);
		//
		// //Holen der Texturen in ein 1D Array
		// TextureRegion[] shipFrames = new
		// TextureRegion[colTexture*rowTexture];
		// int index = 0;
		// for (int i = 0; i < rowTexture; i++) {
		// for (int j = 0; j < colTexture; j++) {
		// shipFrames[index++] = tmp[i][j];
		// }
		// }
		// DEPRECATED END

		// Setzen der Einzelnen Texturen in eine TreeMap<key,value>.
		this.shipTextures = new TreeMap<String, TextureRegion>();
		Iterator<TiledMapTile> iter = st.iterator();
		TiledMapTile tile;
		while (iter.hasNext()) {
			tile = iter.next();
			// Wenn wir ein Tile mit propertie name haben
			if (tile.getProperties().get("name") != null) {
				shipTextures.put(tile.getProperties().get("name").toString(),
						tile.getTextureRegion());
			}
		}

		this.shipTiles = new TreeMap<String, TiledMapTile>();
		iter = st.iterator();
		while (iter.hasNext()) {
			tile = iter.next();
			// Wenn wir ein Tile mit propertie name haben
			if (tile.getProperties().get("name") != null) {
				shipTiles
						.put(tile.getProperties().get("name").toString(), tile);
			}
		}

	}

	/**
	 * Methode generiert Schiffe und plaziert diese auf dem SpielFeld.
	 * 
	 * @param schiffsEinstellung
	 * @param ships
	 *            Die Schiffe, die platziert werden
	 */
	public void generateNewShipplacement(ArrayList<Integer> schiffsEinstellung) {

		// Die Alten Schiffe von dem Feld bereinigen
		units = new FieldUnit[10][10];
		create();
		createNeighbors();
		createKante();

		// Die neuen Schiffe platzieren
		sp = new ShipPlacement();
		sp.placeShips(this, KI.createShips(schiffsEinstellung.get(0),
				schiffsEinstellung.get(1), schiffsEinstellung.get(2),
				schiffsEinstellung.get(3)), false);
		allShipsSetManual = false;

	}

	/**
	 * Methode setzt die Schiffe Manuell und plaziert diese auf dem SpielFeld.
	 * 
	 * @param placedShipUnits
	 *            Manuell gesetzen Schiffe aus dem CameraController
	 */
	public void setManualNewShipplacement(ArrayList<FieldUnit[]> placedShipUnits) {

		
		// Die neuen Schiffe platzieren
		sp = new ShipPlacement();
		sp.placeShipsManual(this,placedShipUnits);
		allShipsSetManual = true;

	}

	/**
	 * Setzt die platzierten Schiffe nach Erstellung.
	 * 
	 * @param ships
	 *            Schiffe, die zu diesem Spielfeld zugeordnet werden sollen
	 */
	public void setShips(Ship[] ships) {
		// Vorher das setzen der Schiffe
		this.placedShips = ships;
		// Für das Zeichnen bekommt jedes Schiff sein EntityShip danach die
		// Koordinaten.
		for (Ship ship : ships) {
			// Finden der geeigneten Textur
			String textureName = "";
			int index = 0;
			if (ship.getSize() > 1) {
				for (FieldUnit unit : ship.location) {
					switch (unit.getShipSegment()) {
					case 2:// 0=Vorderteil
						if (unit.getPlacedShip().getOrientation() == 0
								|| unit.getPlacedShip().getOrientation() == 2) {// Horizontal
							if (unit.getPlacedShip().getOrientation() == 0) {// Rechts
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "rhb";
								} else {
									textureName = "rhba";
								}
							} else {// Links
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "lhb";
								} else {
									textureName = "lhba";
								}
							}
						} else {// Vertikal
							if (!unit.getPlacedShip().isDestroyed()) {
								// textureName = "dvb";//TODO Add upstairs
								// texture
								textureName = "uvf";
							} else {
								// textureName = "dvba";
								textureName = "uvfa";
							}
						}
						break;
					case 1:// 1=Mittelteil
						if (unit.getPlacedShip().getOrientation() == 0
								|| unit.getPlacedShip().getOrientation() == 2) {// Horizontal
							if (unit.getPlacedShip().getOrientation() == 0) {// rechts

								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "rhm";
								} else {
									textureName = "rhma";
								}
							} else {// links
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "lhm";
								} else {
									textureName = "lhma";
								}
							}
						} else {// Vertikal
							if (!unit.getPlacedShip().isDestroyed()) {
								// textureName = "dvm";//TODO Add upstairs
								// texture
								textureName = "uvm";
							} else {
								// textureName = "dvma";
								textureName = "uvma";
							}
						}
						break;
					case 0:// 2=Hinterteil
						if (unit.getPlacedShip().getOrientation() == 0
								|| unit.getPlacedShip().getOrientation() == 2) {// Horizontal
							if (unit.getPlacedShip().getOrientation() == 0) {// rechts
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "rhf";
								} else {
									textureName = "rhfa";
								}

							} else {// links
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "lhf";
								} else {
									textureName = "lhfa";
								}
							}
						} else {// Vertikal
							if (!unit.getPlacedShip().isDestroyed()) {
								// textureName = "dvf";// TODO Add downstairs
								// texture
								textureName = "uvb";
							} else {
								// textureName = "dvfa";
								textureName = "uvba";
							}
						}
						break;
					}
					// Hinzufügen von Schiffsteil
					EntityShip tmpShip = new EntityShip(
							shipTextures.get(textureName),
							shipTextures.get(textureName + "a"), new Vector2(
									unit.getXpos(), unit.getYpos()),
							new Vector2(size, size), mapTileLayer);
					unit.setEntityShipDrawUnit(tmpShip);

					// TODO Deprecated
					drawShips.add(tmpShip);
				}
			} else {// Kleines Schiff
				FieldUnit unit = ship.location[0];

				if (unit.getPlacedShip().getOrientation() == 0) {// Rechts
					if (!ship.isDestroyed()) {
						textureName = "rhk";
					} else {
						textureName = "rhka";
					}
				} else if (unit.getPlacedShip().getOrientation() == 1) {// Oben
					if (!unit.getPlacedShip().isDestroyed()) {
						textureName = "uvk";
					} else {
						textureName = "uvka";
					}
				} else if (unit.getPlacedShip().getOrientation() == 2) {// links
					if (!unit.getPlacedShip().isDestroyed()) {
						// textureName = "lvk";//TODO add left texture
						textureName = "rhk";
					} else {
						// textureName = "lvka";
						textureName = "rhka";
					}
				} else if (unit.getPlacedShip().getOrientation() == 3) {// unten
					if (!unit.getPlacedShip().isDestroyed()) {
						textureName = "dvk";
					} else {
						textureName = "dvka";
					}
				}
				// Hinzufügen von Schiffsteil
				EntityShip tmpShip = new EntityShip(
						shipTextures.get(textureName),
						shipTextures.get(textureName + "a"), new Vector2(
								unit.getXpos(), unit.getYpos()), new Vector2(
								size, size), mapTileLayer);
				unit.setEntityShipDrawUnit(tmpShip);

				// TODO Deprecated
				drawShips.add(tmpShip);
			}
		}

		allShipsSet = true;
	}
	
	/**
	 * Setzt die platzierten Schiffe nach manueller Erstellung.
	 * 
	 * @param ships
	 *            Schiffe, die zu diesem Spielfeld zugeordnet werden sollen
	 */
	public void setShipsManual(Ship[] ships) {
		this.placedShips = ships;
		allShipsSet = true;

		//TODO Ab hier Deprecated
		for (Ship ship : ships) {
			if (ship.getSize() > 1) {
				for (FieldUnit unit : ship.location) {		
					// TODO Deprecated
					// Hinzufügen von Schiffsteil
					drawShips.add(unit.getEntityShipDrawUnit());
				}
			} else {// Kleines Schiff
				// TODO Deprecated
				FieldUnit unit = ship.location[0];
				drawShips.add(unit.getEntityShipDrawUnit());
			}
		}
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
	 * Methode Findet anhand der Weltkoordinaten das entsprechende FeldElement
	 * 
	 * @param xPos
	 *            X Weltkoordinate
	 * @param yPos
	 *            Y Weltkoordinate
	 * @return FieldUnit Das gefundene Feldelement oder null
	 */
	public FieldUnit getElementByXPosYPos(float xPos, float yPos) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				// Gdx.app.log("getElementByXPosYPos", " x:"+xPos+" y:"+yPos);
				if ((units[i][j].getXpos() <= xPos && xPos < units[i][j]
						.getXpos() + size)
						&& (units[i][j].getYpos() <= yPos && yPos < units[i][j]
								.getYpos() + size)) {

					// Gdx.app.log("getElementByXPosYPos",
					// "Feldtyp:"+typ+" Unit ID:"+units[i][j].getID()+" x:"+units[i][j].getXpos()+" y:"+units[i][j].getYpos());
					return units[i][j];
					// }else{
					// Gdx.app.log("nichtGefunden",
					// "Feldtyp:"+typ+" Unit ID:"+units[i][j].getID()+" x:"+units[i][j].getXpos()+" y:"+units[i][j].getYpos());
				}
			}
		}

		return null;
	}

	/**
	 * Erstellt das Spielfeld, das aus 10x10 Feldelementen besteht Hierbei wird
	 * auch die Position je Feld in der Scene übergeben.
	 */
	private void create() {
		int cellPosX = 1;
		int cellPosY = 5;
		if (this.typ == 1) {// Weltkoordinaten sind umgekehrt PlayerFeld unten
			cellPosY += 10;
		}
		int id = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				id++;
				units[i][j] = new FieldUnit(id, (j + cellPosX)
						* mapTileLayer.getTileWidth(), (i + cellPosY)
						* mapTileLayer.getTileHeight(), this);
			}
		}
	}

	private void create(boolean test) {
		create();// Wir brauchen die positionen aus create();
		// int id = 0;
		// for (int i = 0; i < 10; i++) {
		// for (int j = 0; j < 10; j++) {
		// id++;
		//
		// // TODO Testen auf funktion beim Zeichen
		// units[i][j] = new FieldUnit(id);
		// }
		// }
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
						// e.setEdge(1, 1);
						e.setEdge(1, EDGE_ABOVE);
					else
						// e.setEdge(2, 1);
						e.setEdge(2, EDGE_ABOVE);
				}
				if (id > 89) {
					// Untere Kante
					if (e.getEdge(1) == 0)
						// e.setEdge(1, 2);
						e.setEdge(1, EDGE_BELOW);
					else
						// e.setEdge(2, 2);
						e.setEdge(2, EDGE_BELOW);
				}
				if ((id - (10 * (i + 1))) == 0) {
					// Rechte Kante
					if (e.getEdge(1) == 0)
						// e.setEdge(1, 3);
						e.setEdge(1, EDGE_RIGHT);
					else
						// e.setEdge(2, 3);
						e.setEdge(2, EDGE_RIGHT);
				}
				if ((id - (10 * i)) == 1) {
					// Linke Kante
					if (e.getEdge(1) == 0)
						// e.setEdge(1, 4);
						e.setEdge(1, EDGE_LEFT);
					else
						// e.setEdge(2, 4);
						e.setEdge(2, EDGE_LEFT);
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

				if (i > 0 && i < 10) {
					oNeighbor = units[i - 1][j];
				}
				if (i >= 0 && i < 9) {
					uNeighbor = units[i + 1][j];
				}

				if (j > 0 && j < 10) {
					lNeighbor = units[i][j - 1];
				}

				if (j >= 0 && j < 9) {
					rNeighbor = units[i][j + 1];
				}

				units[i][j].setNeighbors(lNeighbor, rNeighbor, oNeighbor,
						uNeighbor);
			}
		}
	}

	/**
	 * Iteriert über alle Feldelemente die gezeichnet werden.
	 * 
	 * @param batch
	 *            SpriteBatch fürs Zeichnen
	 * @param typ
	 *            Spielfeld Typ 0=Spieler, 1=Gegner
	 */
	// @Deprecated
	public void draw(Batch batch) {
		// Rendern vom Spielfeld
		if (this.typ == 0) {// Eigenes Spielfeld
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (units[i][j].getAttacked()) {// Wenn Feld angegriffen
													// wurde
						if (!units[i][j].getOccupied()) {// Nicht belegt,
															// Wasserplatscher
							if (units[i][j].getAnimationtimer() < animationtimerMax) {
								batch.draw(shipTextures.get("gunattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								units[i][j].setAnimationtimer(units[i][j]
										.getAnimationtimer() + 1);
							} else if (units[i][j].getAnimationtimer() < animationtimerMax
									+ animationtimerMax) {// Nach animation
															// Wasserattacke
															// anzeigen
								batch.draw(shipTextures.get("waterattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								units[i][j].setAnimationtimer(units[i][j]
										.getAnimationtimer() + 1);
							}
						} else {
							// Schiffsteil beschädigt
							units[i][j].getEntityShipDrawUnit().render(batch,
									true);
							// Bombenanimation
							if (units[i][j].getAnimationtimer() < animationtimerMax) {
								batch.draw(shipTextures.get("gunattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								units[i][j].setAnimationtimer(units[i][j]
										.getAnimationtimer() + 1);
							}
						}
					} else {// Wenn Feld nicht angegriffen
							// Schiffsteil vorhanden auf dem Feld
						if (units[i][j].getOccupied()) {
							units[i][j].getEntityShipDrawUnit().render(batch,
									false);
						}
					}

				}
			}
		} else {// Gegnerisches Feld
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (units[i][j].getAttacked()) {// Wenn Feld attakiert wurde
						if (!units[i][j].getOccupied()) {// Nicht belegt,
															// Wasserplatscher
							batch.draw(shipTextures.get("waterattack"),
									units[i][j].getXpos(),
									units[i][j].getYpos(), size, size);
						} else {
							// Komplettes Schiff ist zerstört und wird angezeigt
							if (units[i][j].getPlacedShip().isDestroyed()) {
								units[i][j].getEntityShipDrawUnit().render(
										batch, true);
								// Schiff nicht komplett zerstört
							} else if (!units[i][j].getPlacedShip()
									.isDestroyed()) {
								if (units[i][j].getAnimationtimer() < animationtimerMax) {
									batch.draw(shipTextures.get("gunattack"),
											units[i][j].getXpos(),
											units[i][j].getYpos(), size, size);
									units[i][j].setAnimationtimer(units[i][j]
											.getAnimationtimer() + 1);
								} else {// Nach animation Wasserattacke anzeigen
									batch.draw(shipTextures.get("waterattack"),
											units[i][j].getXpos(),
											units[i][j].getYpos(), size, size);
								}
							}
						}
					}
				}
			}
		}

		// // Rendern aller Schiffe
		// tileIterator = drawShips.iterator();
		// EntityShip curShip;
		// while (tileIterator.hasNext()) {
		// curShip = tileIterator.next();
		// curShip.render(batch);
		//
		// }

	}

	/**
	 * Methode gibt ein Array von EntityShip zurück die gezeichnet werden
	 * können.
	 * 
	 * @return out rrayList<EntityShip> fürs Zeichnen
	 */
	public ArrayList<EntityShip> getTiledShips() {
		// ArrayList<EntityShip> out = new ArrayList<EntityShip>();
		//
		// if(placedShips==null)
		// System.out.println("LOL!");
		// for (Ship ship : placedShips) {
		// for (FieldUnit unit : ship.location) {
		// out.add(unit.getPlacedShip().getEntityShipDrawUnit());
		// System.out.println("Schiffsteilkoord: x"
		// + unit.getPlacedShip().getEntityShipDrawUnit().getX() + " y"
		// + unit.getPlacedShip().getEntityShipDrawUnit().getY());
		// }
		// }
		return drawShips;
	}

	/**
	 * Methode gibt den Status zurück ob Schiffe auf Feld Plaziert sind.
	 * 
	 * @return boolean true oder false
	 */
	public boolean isAllShipsSet() {
		return allShipsSet;
	}

	/**
	 * Methode setzt den Status ob Schiffe auf Feld Plaziert sind.
	 * 
	 * @param setTo
	 *            true oder false
	 */
	public void setAllShipsSet(boolean setTo) {
		allShipsSet = setTo;
	}

	public void resetField() {
		try {
			drawShips = new ArrayList<EntityShip>();
			allShipsSet = false;
			create();
			createNeighbors();
			createKante();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public TreeMap<String, TextureRegion> getShipTextures() {
		return shipTextures;
	}

	public TreeMap<String, TiledMapTile> getShipTiles() {
		return shipTiles;
	}

	public TiledMapTileLayer getMapTileLayer() {
		return mapTileLayer;
	}

	public boolean getAllShipsSetManual() {
		return allShipsSetManual;

	}

	public void setAllShipsSetManual(boolean b) {
		allShipsSetManual = b;

	}

}
