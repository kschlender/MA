package com.ma.schiffeversenken.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.ma.schiffeversenken.EntityShip;
import com.ma.schiffeversenken.GameFieldScreen;
import com.ma.schiffeversenken.android.controller.BluetoothConnectedThread;
import com.ma.schiffeversenken.android.controller.Game;
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
	/** Anzahl der Feldelement */
	public static final int FIELD_SIZE = 100;
	/**Audio Datei, die fuer die Schieß Effekte dient*/
	private static final String SHOT_SOUND_PATH = "sounds/Shot.mp3";
	/**Audio Datei, die fuer die Explosions Effekte dient*/
	private static final String EXPLOSION_SOUND_PATH = "sounds/Explosion.mp3";
	/**Laenge der Vibration in ms, wenn ein Schiff getroffen wurde*/
	private static final int VIBRATION_LENGTH = 100;
	/**Sound Variablen fuer das Abspielen der Sound Effekte*/
	Sound explosionSound, shotSound;
	/**true oder false ob die Sound Effekte deaktiviert sind*/
	public static boolean soundOff = false;
	/**true oder false ob die Vibrationen deaktiviert sind*/
	public static boolean vibrationOff = false;
	/**true oder false ob die Cheat eingeschaltet sind*/
	public static boolean cheatsOn = false;
	
	/**
	 * einheiten = Das Spielfeld besteht aus 10x10 Einheiten einheiten[y-Achse
	 * (Zeile)][x-Achse (Spalte)]
	 */
	protected FieldUnit[][] units = new FieldUnit[10][10];
	/**
	 * Menge von platzierten Schiffen auf diesem Spielfeld
	 */
	protected Ship[] placedShips;
	/**
	 * typ = Gibt den Typ des Spielfelds an. Eigenes Spielfeld = 0; Gegnerisches
	 * Spielfeld = 1;
	 */
	int typ;

	// Boolean ob alle Schiffe gesetzt sind und Feld bereit ist.
	private boolean allShipsSet = false;
	public ShipPlacement sp;

	// Einheitsgroesse der Texturen
	float size;
	// graphics High and width
	private int h;
	private int w;
	private TiledMapTileLayer mapTileLayer;
	private TreeMap<String, TextureRegion> shipTextures;
	private TreeMap<String, TiledMapTile> shipTiles;
	private int animationtimerMax = 20;
	private boolean allShipsSetManual;
	private boolean feldUebertragen;
	private boolean feldUebertragenAntwort=false;

	/**
	 * Erstellt ein Field Objekt.
	 * Wird nur zu Testzwecken ausgefuehrt
	 * 
	 * @param typ
	 *            Typ des Spielfelds (0=Eigenes Spielfeld, 1=Gegnerisches
	 *            Spielfeld)
	 */
	public Field(int typ) {
		try {
			this.typ = typ;
			create(true);
			createEdges();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Ueberladener Konstruktor erstellt ein Field Objekt
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
			this.feldUebertragen=false;
			this.feldUebertragenAntwort=false;
			this.explosionSound = Gdx.audio.newSound(Gdx.files.internal(EXPLOSION_SOUND_PATH));
			this.shotSound = Gdx.audio.newSound(Gdx.files.internal(SHOT_SOUND_PATH));
			getShipTileSetTextures(shipTextures);
			create();
			createNeighbors();
			createEdges();
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
		// DEPRECATED
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
		// DEPRECATED
	}

	/**
	 * Methode generiert Schiffe und plaziert diese auf dem SpielFeld.
	 * 
	 * @param schiffsEinstellung
	 */
	public void generateNewShipplacement(ArrayList<Integer> schiffsEinstellung) {

		// Die Alten Schiffe von dem Feld bereinigen
		units = new FieldUnit[10][10];
		create();
		createNeighbors();
		createEdges();

		// Die neuen Schiffe platzieren
		sp = new ShipPlacement();
		sp.placeShips(this, KI.createShips(schiffsEinstellung.get(0),
				schiffsEinstellung.get(1), schiffsEinstellung.get(2),
				schiffsEinstellung.get(3)));
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
		allShipsSetManual = true;
		sp = new ShipPlacement();
		sp.placeShipsManual(this,placedShipUnits);
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
		// Fuer das Zeichnen bekommt jedes Schiff sein EntityShip danach die
		// Koordinaten.
		handleShipEntityDrawUnits(ships,false,false);
		allShipsSet = true;
	}
	
	/**
	 * Methode behandelt fuer jedes Schiff das entsprechende Feldelement und
	 * vergibt diesem das entsprechende Draw Objekt EntityShip
	 * @param ships Die uebergebenen Schiffe
	 * @param setTextureName true, wenn nur die Textur angepasst werden soll.
	 * @param manualPlacement true, wenn die Plazierung manuell gemacht wurde.
	 */
	private void handleShipEntityDrawUnits(Ship[] ships, boolean setTextureName, boolean manualPlacement) {
		for (Ship ship : ships) {
			// Finden der geeigneten Textur
			String textureName = "";
			if (ship.getSize() > 1) {
				for (int s=0;s<ship.location.length;s++) {
					FieldUnit unit = ship.location[s];
					switch (unit.getShipSegment()) {
					case 2:// 2=Hinterteil
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
							if (unit.getPlacedShip().getOrientation() == 1) {// oben
							if (!unit.getPlacedShip().isDestroyed()) {
								textureName = (manualPlacement)?"uvb":"dvb";
							} else {
								textureName = (manualPlacement)?"uvba":"dvba";
							}
							}else{//unten
								if (!unit.getPlacedShip().isDestroyed()) {
									// texture
									textureName = (manualPlacement)?"dvb":"uvb";
								} else {
									textureName = (manualPlacement)?"dvba":"uvba";
								}
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
							if (unit.getPlacedShip().getOrientation() == 1) {// oben	
							if (!unit.getPlacedShip().isDestroyed()) {
								textureName = "uvm";
							} else {
								textureName = "uvma";
							}
							}else{//unten
								if (!unit.getPlacedShip().isDestroyed()) {
									textureName = "dvm";
								} else {
									textureName = "dvma";
								}
							}
						}
						break;
					case 0:// 0=Front
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
							if (unit.getPlacedShip().getOrientation() == 1) {// oben
							if (!unit.getPlacedShip().isDestroyed()) {
								textureName = (manualPlacement)?"uvf":"dvf";
							} else {
								textureName = "uvfa";
							}
							}else{//unten
								if (!unit.getPlacedShip().isDestroyed()) {
									// texture
									textureName = (manualPlacement)?"dvf":"uvf";
								} else {
									textureName = (manualPlacement)?"dvfa":"uvfa";
								}
							}
						}
						break;
					}
					
					
					if(setTextureName&&unit.getEntityShipDrawUnit()!=null){
					//Bearbeiten von Schiffsteil.
						unit.getEntityShipDrawUnit().setShipTextureRegion(textureName);
					}else{
					// Hinzufuegen von Schiffsteil
					EntityShip tmpShip = new EntityShip(textureName, new Vector2(
									unit.getXpos(), unit.getYpos()),
							new Vector2(size, size));
					unit.setEntityShipDrawUnit(tmpShip);
					}
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
				
				if(setTextureName&&unit.getEntityShipDrawUnit()!=null){
					//Bearbeiten von Schiffsteil.
						unit.getEntityShipDrawUnit().setShipTextureRegion(textureName);
				}else{
				// Hinzufuegen von Schiffsteil
				EntityShip tmpShip = new EntityShip(textureName, new Vector2(
								unit.getXpos(), unit.getYpos()), new Vector2(
								size, size));
				unit.setEntityShipDrawUnit(tmpShip);
				}
			}
		}
	}

	/**
	 * Setzt die platzierten Schiffe nach manueller Erstellung.
	 * 
	 * @param ships
	 *            Schiffe, die zu diesem Spielfeld zugeordnet werden sollen
	 */
	public void setShipsManual(Ship[] ships) {
		this.placedShips = ships;
		//Fuer die Korrektur der Texturen nach der Datenuebertragung des Json Objektes
		handleShipEntityDrawUnits(placedShips,true,true);
		allShipsSet = true;
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
	 * auch die Position je Feld in der Scene uebergeben.
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
						* mapTileLayer.getTileHeight());
			}
		}
	}

	/**
	 * Wird nur zu Testzwecken verwendet, da das Junit 
	 * keine Gradle Funktionen unterstuetzt
	 * @param test True, wenn ein Testlauf
	 */
	private void create(boolean test) {
		int id = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				id++;
				units[i][j] = new FieldUnit(id);
			}
		}
	}

	/**
	 * Markiert, dass dieses FeldElement an einer Kante platziert ist und in
	 * welcher Richtung die Kante liegt
	 */
	private void createEdges() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				FieldUnit e = units[i][j];
				int id = e.getID();
				if (id < 11) {
					// Obere Kante
					if (e.getEdge(1) == -1)
						e.setEdge(1, EDGE_ABOVE);
					else
						e.setEdge(2, EDGE_ABOVE);
				}
				if (id > 89) {
					// Untere Kante
					if (e.getEdge(1) == -1)
						e.setEdge(1, EDGE_BELOW);
					else
						e.setEdge(2, EDGE_BELOW);
				}
				if ((id - (10 * (i + 1))) == 0) {
					// Rechte Kante
					if (e.getEdge(1) == -1)
						e.setEdge(1, EDGE_RIGHT);
					else
						e.setEdge(2, EDGE_RIGHT);
				}
				if ((id - (10 * i)) == 1) {
					// Linke Kante
					if (e.getEdge(1) == -1)
						e.setEdge(1, EDGE_LEFT);
					else
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
	 * Iteriert ueber alle Feldelemente die gezeichnet werden.
	 * 
	 * @param batch
	 *            SpriteBatch fuers Zeichnen
	 */
	// @Deprecated
	public void draw(Batch batch) {
		try{
			//Cheats, man sieht das gegnerische Spielfeld
			if(Field.cheatsOn){
			// Eigenes Spielfeld
						for (int i = 0; i < 10; i++) {
							for (int j = 0; j < 10; j++) {
								if (units[i][j].getAttacked()) {// Wenn Feld angegriffen wurde
									if (!units[i][j].getOccupied()) {// Nicht belegt,
										// Wasserplatscher
										if (units[i][j].getAnimationtimer() < animationtimerMax) {
											batch.draw(shipTextures.get("gunattack"),
													units[i][j].getXpos(),
													units[i][j].getYpos(), size, size);
											units[i][j].setAnimationtimer(units[i][j]
													.getAnimationtimer() + 1);
											
											if(units[i][j].getAnimationtimer() == 1){
												if(!Field.soundOff){
													shotSound.play();
												}
											}
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
										// Schiffsteil beschaedigt, malen wenn Vorhanden.
										if(units[i][j].getEntityShipDrawUnit()!=null)
										units[i][j].getEntityShipDrawUnit().render(batch,
												true,shipTextures);
										// Bombenanimation
										if (units[i][j].getAnimationtimer() < animationtimerMax) {
											batch.draw(shipTextures.get("gunattack"),
													units[i][j].getXpos(),
													units[i][j].getYpos(), size, size);
											units[i][j].setAnimationtimer(units[i][j]
													.getAnimationtimer() + 1);
											
											if(units[i][j].getAnimationtimer() == 1){
												if(!Field.soundOff){
													explosionSound.play();
												}
												if(!Field.vibrationOff){
													Gdx.input.vibrate(VIBRATION_LENGTH);
												}
											}
										}
									}
								} else {// Wenn Feld nicht angegriffen
										// Schiffsteil vorhanden auf dem Feld
									if (units[i][j].getOccupied()) {
										units[i][j].getEntityShipDrawUnit().render(batch,
												false,shipTextures);
									}
								}

							}
						}
			}
		// Rendern vom jeweiligen Spielfeld
		if (this.typ == 0) {// Eigenes Spielfeld
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (units[i][j].getAttacked()) {// Wenn Feld angegriffen wurde
						if (!units[i][j].getOccupied()) {// Nicht belegt,
							// Wasserplatscher
							if (units[i][j].getAnimationtimer() < animationtimerMax) {
								batch.draw(shipTextures.get("gunattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								units[i][j].setAnimationtimer(units[i][j]
										.getAnimationtimer() + 1);
								
								if(units[i][j].getAnimationtimer() == 1){
									if(!Field.soundOff){
										shotSound.play();
									}
								}
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
							// Schiffsteil beschaedigt, malen
							units[i][j].getEntityShipDrawUnit().render(batch,
									true,shipTextures);
							// Bombenanimation
							if (units[i][j].getAnimationtimer() < animationtimerMax) {
								batch.draw(shipTextures.get("gunattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								units[i][j].setAnimationtimer(units[i][j]
										.getAnimationtimer() + 1);
								
								if(units[i][j].getAnimationtimer() == 1){
									if(!Field.soundOff){
										explosionSound.play();
									}
									if(!Field.vibrationOff){
										Gdx.input.vibrate(VIBRATION_LENGTH);
									}
								}
							}
						}
					} else {// Wenn Feld nicht angegriffen
							// Schiffsteil vorhanden auf dem Feld
						if (units[i][j].getOccupied()) {
							units[i][j].getEntityShipDrawUnit().render(batch,
									false,shipTextures);
						}
					}

				}
			}
		} else if(!Field.cheatsOn) {// Gegnerisches Feld
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (units[i][j].getAttacked()) {// Wenn Feld attakiert wurde
						//Feueranimation nach Beschuss
						if (units[i][j].getAnimationtimer() < animationtimerMax) {
							batch.draw(shipTextures.get("gunattack"),
									units[i][j].getXpos(),
									units[i][j].getYpos(), size, size);
							units[i][j].setAnimationtimer(units[i][j]
									.getAnimationtimer() + 1);
						}
						if (!units[i][j].getOccupied()) {// Nicht belegt,
								// Wasserplatscher
								batch.draw(shipTextures.get("waterattack"),
										units[i][j].getXpos(),
										units[i][j].getYpos(), size, size);
								if(units[i][j].getAnimationtimer() == 1){
									if(!Field.soundOff){
										shotSound.play();
									}
								}
						} else {
							if (units[i][j].getPlacedShip().isDestroyed()) {
								// Komplettes Schiff ist zerstoert und wird angezeigt
								units[i][j].getEntityShipDrawUnit().render(
										batch, true,shipTextures);
							} else if (!units[i][j].getPlacedShip().isDestroyed()) {
								// Schiff nicht komplett zerstoert

									// Nach animation Feueratakke anzeigen wenn schiff beschaedigt
									batch.draw(shipTextures.get("gunattack"),
											units[i][j].getXpos(),
											units[i][j].getYpos(), size, size);	
							}
							if(units[i][j].getAnimationtimer() == 1){
								if(!Field.soundOff){
									explosionSound.play();
								}
							}
						}
					}
				}
			}
		
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Methode gibt den Status zurueck ob Schiffe auf Feld Plaziert sind.
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

	/**
	 * Feld zuruecksetzen
	 */
	public void resetField() {
		try {
			allShipsSet = false;
			allShipsSetManual=false;
			create();
			createNeighbors();
			createEdges();
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

	public boolean isAllShipsSetManual() {
		return allShipsSetManual;

	}

	public void setAllShipsSetManual(boolean b) {
		allShipsSetManual = b;

	}

	public void setFieldUnits(FieldUnit[][] u) {
		this.units=u;
	}
	
	public void setFieldUnitsBluetooth(FieldUnit[][] u) {
		if(!feldUebertragen){
			//TODO Nachricht feldï¿½bertragen
			BluetoothConnectedThread btcThread = BluetoothConnectedThread.getInstance();
			byte[] returnString = (new String(btcThread.BLUETOOTH_ENEMY_FIELD_RETURN)).getBytes();
			btcThread.write(returnString);
		}
		this.units=u;
	}
	
	/**
	 * Die platzierten Schiffe auf diesem Feld setzen
	 * @param s Die platzierten Schiffe
	 */
	public void setFieldShips(Ship[] s) {
		this.placedShips=s;
	}

	public void sendFieldUnitsWithBluetooth() {

		//Fuer Bluetooth eigenes Feld uebertragen
		if(!feldUebertragen){
			BluetoothConnectedThread btcThread = BluetoothConnectedThread.getInstance();
			byte[] fieldBytes;
			
			//Speichern nach Json String
			Json json = new Json();
			String jsonPlacedShips =json.toJson(new ShipsDescriptor().newShipsDescriptor(placedShips),ShipsDescriptor.class);
				
			//Deserialisierung
			fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+jsonPlacedShips).getBytes();
			//Schreiben
			btcThread.write(fieldBytes);
		}

//				//DEPRECATED ab hier, Java.io.Serialisable macht Android Probleme.
//				try {
//					BluetoothConnectedThread btcThread = BluetoothConnectedThread.getInstance();
//					//Serialisierung
//					byte[] fieldBytes;
//					String subString = Player.toString(units);
//					String subStringAll = new String(subString);
//					String subStringPart = subStringAll.substring(0, BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length());
//									
//					fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+subStringPart).getBytes();					
//					
//					//Deserialisierung
//					int bytesToTransfer = subStringAll.length();		
//					while (bytesToTransfer >= 0) {
//						if(bytesToTransfer>0){
//							//Part Bytes senden	
//							if(subStringAll.length()>=(BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length())){									
//								subStringPart = subStringAll.substring(0, (BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length()));
//							}else{//letzter Part
//								subStringPart = subStringAll;
//							}
//						
//						fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+subStringPart).getBytes();
//						//Schreiben
//						btcThread.write(fieldBytes);
//					
//						if(subStringPart.equals(subStringAll)){
//							bytesToTransfer -= subStringAll.length();
//						}else{//Rest definieren
//						subStringAll=subStringAll.substring(BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length());			    
//					    bytesToTransfer -= BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length();
//						}
//						}else{
//							//Ende senden
//							fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD).getBytes();
//							bytesToTransfer -= BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length();
//							//Schreiben
//							btcThread.write(fieldBytes);
//						}
//					}
//					} catch (IOException e) {
//					e.printStackTrace();
//				}
	}

	/**
	 * Methode dient der localen Serialisierung.
	 * @param game Das Game Objekt
	 */
	@Deprecated
	public void serialisierungstestLocal(Game game) {
		//Speichern nach Json
		Json json = new Json();
		String jsonPlacedShips =json.toJson(new ShipsDescriptor().newShipsDescriptor(placedShips), ShipsDescriptor.class);
		System.out.println(json.prettyPrint(jsonPlacedShips));
		
		//erzeugen von Json
		ShipsDescriptor desc = json.fromJson(ShipsDescriptor.class, jsonPlacedShips);
		//Feld resetten und Schiffe aus Json Generieren und Platzieren.
		desc.replaceOldFieldPlacedShips(game.getSecondFieldEnemy());	
		
//				//Java Serialisierungstest mit Java.io.Serialisable 
//				//--> funktioniert local, Android hat damit Probleme
//				try {
//					//Serialisierung
//					byte[] fieldBytes;
//					FieldUnit[][] units = getFieldUnits();
//					FieldUnit[][] units2;
//					System.out.println("FIELDID VORHER: "+units[1][1].getID());					
//					String subString = Player.toString(units);
//					String subStringAll = new String(subString);
//					String subStringPart = subStringAll.substring(0, BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length());
//									
//					fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+subStringPart).getBytes();					
//					
//					System.out.println("Send_Sub    "+subStringPart+"      grï¿½ï¿½e:"+subStringPart.length());
//					System.out.println("Send_Full   "+fieldBytes+"      grï¿½ï¿½e:"+fieldBytes.length);		
//					//Deserialisierung
//					String readMsg="";
//					String subStringIncomeing="";
//					int bytesToTransfer = subStringAll.length();		
//					while (bytesToTransfer >= 0) {			
//						System.out.println("bytesToTransfer vorher:"+bytesToTransfer);
//						if(bytesToTransfer>0){
//							//1024 Bytes senden	
//							System.out.println("substringSend lastindex:"+subStringAll.length());
//							System.out.println("substringSend lastindextocopy:"+(BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length()));
//							if(subStringAll.length()>=(BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length())){									
//								subStringPart = subStringAll.substring(0, (BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length()));
//							}else{
//								subStringPart = subStringAll;
//							}
//						
//						fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+subStringPart).getBytes();
//						//Schreiben simulieren
//						readMsg = new String(fieldBytes, 0, fieldBytes.length);
//						subStringIncomeing= subStringIncomeing+readMsg.substring(BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length());
//						btcThread.write(fieldBytes);
//						
//						if(subStringPart.equals(subStringAll)){
//							bytesToTransfer -= subStringAll.length();
//						}else{
//						subStringAll=subStringAll.substring(BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length());			    
//					    bytesToTransfer -= BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length();
//						}
//						System.out.println("bytesToTransfer later:"+bytesToTransfer);
//						}else{
//							//Ende senden
//							fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD).getBytes();
//							readMsg = new String(fieldBytes, 0, fieldBytes.length);
//							
//							bytesToTransfer -= BluetoothConnectedThread.BUFFER_SIZE-BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD.length();
//						}
//						
//						if(readMsg.startsWith(BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD+BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD)){
//							//Ende simulieren
//							System.out.println("Substring lï¿½nge muss:"+subString.length());
//							System.out.println("Substring lï¿½nge ist :"+subStringIncomeing.length());
//							units2=(FieldUnit[][])Player.fromString(subStringIncomeing);					
//							System.out.println("FIELDID NACHHER: "+units2[1][1].getID());
//							
//						}
//					}
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
	}
	
	/**
	 * Variablen werden durch gesetzt, wenn diese
	 * Variablen vom Bluetooth Gegner gesendet werden
	 * @param returnFieldSet
	 */
	public void setFeldUebertragen(boolean returnFieldSet){
		this.feldUebertragen = returnFieldSet;
	}
	
	/**
	 * Variablen werden durch gesetzt, wenn
	 * vom Bluetooth Gegner ein Feld empfangen wurde.
	 * @param b
	 */
	public void setFeldUebertragenAntwort(boolean b){
		this.feldUebertragenAntwort = b;
	}
	
	/**
	 * True wenn vom Bluetooth Gegner ein Feld uebertragen wurde.
	 * 
	 */
	public boolean getFeldUebertragen(){
		return this.feldUebertragen;
	}
	
	/**
	 * True wenn vom Bluetooth Gegner das eigene Feld empfangen wurde.
	 * 
	 */
	public boolean getFeldUebertragenAntwort(){
		return this.feldUebertragenAntwort;
	}

	/**
	 * Klasse erstellt ein Json lesefaehiges Abbild von placedShips
	 * um dieses dan fuer den Objektbau wiederzuverwenden.
	 * @author Klaus Schlender
	 *
	 */
	public static class ShipsDescriptor {
		public ArrayList<ShipDescriptor> shipsPlaced;
		
		/**
		 * Methode erstellt aus shipPlacement das Json kompatible Objekt
		 * @return
		 */
		public ShipsDescriptor newShipsDescriptor(Ship[] placedShips){
			ShipsDescriptor desc = new ShipsDescriptor();
			desc.shipsPlaced = new ArrayList<ShipDescriptor>(placedShips.length);
			for (Ship ship : placedShips) {
				ShipDescriptor shipDesc = new ShipDescriptor().newShipDescriptor(ship);
				desc.shipsPlaced.add(shipDesc);
			}
			return desc;
		}

		/**
		 * Methode erstellt die Schiffe aus den
		 * Json Daten in ShipsDescriptor.
		 * 
		 * @return boolean Gibt true zurueck bei Erfolg.
		 */
		public boolean replaceOldFieldPlacedShips(Field field){
			//Hilfsvariablen
			String shipBack ="dvk";
			String shipMiddle ="dvk";
			String shipFront ="dvk";
			//Schiffe und Feld zuruecksetzen
			field.resetField();
			
			//Neue Units erstellen und verarbeiten
			ArrayList<FieldUnit[]> placedShipUnits = new ArrayList<FieldUnit[]>();
			
			for(int i=0 ;i<shipsPlaced.size();i++){
			
				FieldUnit[] unitLocation = new FieldUnit[1];
				int shiftDirection =-1;
				FieldUnit shipLastUnit;
				EntityShip tmpShip;
				int orientation=-1;
				
				//Units Rekonstruieren
				ArrayList<FieldUnitDescriptor> fieldUnitsDesc = shipsPlaced.get(i).location.fieldUnits;
				for(int j=0;j<fieldUnitsDesc.size();j++){
					int id = fieldUnitsDesc.get(j).id;
					FieldUnit unit = field.getElementByID(id);
					shipLastUnit=unit;
						//Behandeln von Schiffsanfang
						if(j<1){
							//kleine Schiffstexturen
							shipBack = "dvk";
							shipMiddle = "dvk";
							shipFront ="dvk";

							
							// Hinzufuegen von Schiffsteil
							tmpShip = new EntityShip(shipBack,
									new Vector2(unit.getXpos(), unit.getYpos()), new Vector2(
											GameFieldScreen.size, GameFieldScreen.size));
							unit.setEntityShipDrawUnit(tmpShip);
							unit.setOccupied(true);
							
							// Schiffslaenge hinzufuegen
							unitLocation = new FieldUnit[1];
							unitLocation[0] = unit;
							shipLastUnit = unit;
							// unten Kreuzer
							orientation = 3;
					
						}else if(j==1){//Behandeln von zweitem Schiffsteil
							//Definieren der Schiffstexturen lt. Ausrichtung gemï¿½ï¿½ Vorgï¿½nger
							if (unit.equals(unitLocation[0].get_lNeighbor())) {
								shiftDirection = 0;
								orientation = 2;//links
								shipBack = "lhb";
								shipMiddle ="lhm";
								shipFront = "lhf";
								unit.get_lNeighbor();
							}
							if (unit.equals(unitLocation[0].get_rNeighbor())) {
								shiftDirection = 1;
								orientation = 0;//rechts
								shipBack ="rhb";
								shipMiddle = "rhm";
								shipFront = "rhf";
								unit.get_rNeighbor();
							}
							if (unit.equals(unitLocation[0].get_uNeighbor())) {
								shiftDirection = 2;
								orientation = 1;//oben
								shipBack ="uvb";		
								shipMiddle = "uvm";
								shipFront = "uvf";
								unit.get_uNeighbor();
							}

							if (unit.equals(shipLastUnit.get_oNeighbor())) {
								shiftDirection = 3;
								orientation = 3;//unten
								shipBack = ("uvf");
								shipMiddle = ("uvm");
								shipFront = ("uvb");
								unit.get_oNeighbor();
							}
							
							// Hinzufï¿½gen von Schiffsteil
							tmpShip = new EntityShip(shipFront,
									new Vector2(unit.getXpos(), unit.getYpos()), new Vector2(
											GameFieldScreen.size, GameFieldScreen.size));
							unit.setEntityShipDrawUnit(tmpShip);
							unit.setOccupied(true);

							// Schiffslaenge hinzufuegen
							FieldUnit[] tmpUnitLocation = new FieldUnit[unitLocation.length + 1];
							tmpUnitLocation[0] = unitLocation[0];
							tmpUnitLocation[1] = unit;
							unitLocation = tmpUnitLocation;

							// Setzen der richtigen Schiffstextur fuer Hinten
							unitLocation[0].getEntityShipDrawUnit().setShipTextureRegion(
									shipBack);
							shipLastUnit = unit;
						}else if(j<fieldUnitsDesc.size()){//Behandeln von Schiffsmitte bis Front
							// Hinzufuegen von Schiffsteil
							tmpShip = new EntityShip(shipFront,
									new Vector2(unit.getXpos(), unit.getYpos()), new Vector2(
											GameFieldScreen.size, GameFieldScreen.size));
							unit.setEntityShipDrawUnit(tmpShip);
							unit.setOccupied(true);
							
							// shipNextUnit festlegen
							switch (shiftDirection) {
							case 0:// links
								unit.get_lNeighbor();
								shipBack = "lhb";
								shipMiddle ="lhm";
								shipFront = "lhf";
								break;
							case 1:// rechts
								unit.get_rNeighbor();
								shipBack ="rhb";
								shipMiddle = "rhm";
								shipFront = "rhf";
								break;
							case 2:// oben
								unit.get_uNeighbor();
								shipBack ="uvb";		
								shipMiddle = "uvm";
								shipFront = "uvf";
								break;
							case 3:// unten
								unit.get_oNeighbor();
								shipBack = ("uvf");
								shipMiddle = ("uvm");
								shipFront = ("uvb");
								break;
							default:
								break;
							}

							// Setzen der richtigen Schiffstextur fuer letzen Teil
							unitLocation[unitLocation.length - 1].getEntityShipDrawUnit()
									.setShipTextureRegion(shipMiddle);

							// Schiffslï¿½nge hinzufï¿½gen
							FieldUnit[] tmpUnitLocation = new FieldUnit[unitLocation.length + 1];
							for (int k = 0; k < unitLocation.length; k++) {
								tmpUnitLocation[k] = unitLocation[k];
							}
							tmpUnitLocation[tmpUnitLocation.length - 1] = unit;
							unitLocation = tmpUnitLocation;
							shipLastUnit = unit;
						}
					
					unitLocation[0].setShipOrientation(orientation);
					
				}	
				// Hinzufuegen der fertigen unitLocation
				placedShipUnits.add(unitLocation);
			}		
			// Die neuen Schiffe platzieren
			field.setManualNewShipplacement(placedShipUnits);
			
			if(field.getShips().length==shipsPlaced.size()){
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * Klasse erstellt ein Json lesefaehiges Abbild von Ship
	 * @author Klaus Schlender
	 *
	 */
	public static class ShipDescriptor {
		public LocationDescriptor location;
		
		/**
		 * Methode erstellt aus shipPlacement das Json kompatible Objekt
		 * @return
		 */
		public ShipDescriptor newShipDescriptor(Ship ship){
			ShipDescriptor shipDesc = new ShipDescriptor();
			shipDesc.location = new LocationDescriptor().newLocationDescriptor(ship.location);
			return shipDesc;
		}
		
	}
	
	/**
	 * Klasse erstellt ein Json lesefaehiges Abbild von FieldUnit[size]
	 * das zu einem Schiff gehoert.
	 * @author Klaus Schlender
	 *
	 */
	public static class LocationDescriptor {
		public ArrayList<FieldUnitDescriptor> fieldUnits;

		public LocationDescriptor newLocationDescriptor(FieldUnit[] loc){
			LocationDescriptor locDesc = new LocationDescriptor();
			locDesc.fieldUnits= new ArrayList<Field.FieldUnitDescriptor>(loc.length);
			for (FieldUnit fieldUnit : loc) {
				FieldUnitDescriptor unitDesc = new FieldUnitDescriptor().newFieldUnitDescriptor(fieldUnit);
				locDesc.fieldUnits.add(unitDesc);
			}
			return locDesc;
		}
	}
	
	/**
	 * Klasse erstellt ein Json lesefaehiges Abbild von FieldUnit[size]
	 * das zu einem Schiff gehoert.
	 * @author Klaus Schlender
	 *
	 */
	public static class FieldUnitDescriptor {
		public int id;
		
		public FieldUnitDescriptor newFieldUnitDescriptor(FieldUnit fieldUnit){
			FieldUnitDescriptor unitDesc = new FieldUnitDescriptor();
			unitDesc.id = fieldUnit.id;
			return unitDesc;
		}
		
	}
}
