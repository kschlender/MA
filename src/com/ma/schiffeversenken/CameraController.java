package com.ma.schiffeversenken;

import java.util.ArrayList;

import android.media.CameraProfile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ma.schiffeversenken.android.controller.Game;
import com.ma.schiffeversenken.android.model.FieldUnit;
import com.ma.schiffeversenken.android.model.Player;

class CameraController implements GestureListener {
	float velX, velY;
	boolean flinging = false;
	float initialScale = 1;
	OrthographicCamera camera;
	float layerX, layerY, layerZoom;
	Game game;
	private float faktorX;
	private float faktorZoom;
	private float faktorY;
	private boolean panStart = false;
	// 0=Intro, 1=FullView, 2=GameFieldZoom, 3=PlayerShips, 4=EnemyShips,
	// 5=GameFieldGrid 6=PlayerGrid, 7=EnemyGrid
	ArrayList<Boolean> state;
	// Wenn true wird gerade ein Schiff plaziert.
	private boolean aktivatorSchiffSetzen;
	private TextureRegion shipBack;
	private TextureRegion shipMiddle;
	private TextureRegion shipFront;
	private FieldUnit[] unitLocation;
	private ArrayList<FieldUnit[]> placedShipUnits;
	private Vector3 touchOld;
	private int shiftDirection;
	private boolean firstShift;
	private TextureRegion shipBackA;
	private TextureRegion shipMiddleA;
	private TextureRegion shipFrontA;
	private ArrayList<Integer> shipPlaceHelper;
	private FieldUnit shipLastUnit;
	private FieldUnit shipNextUnit;

	public CameraController(OrthographicCamera c, float mx, float my,
			float mzoom, Player player, ArrayList<Boolean> state,
			ArrayList<Integer> aktiveSchiffsListe) {
		this.state = state;
		this.camera = c;
		this.layerX = mx;
		this.layerY = my;
		this.layerZoom = mzoom;
		this.game = player.getGame();
		this.aktivatorSchiffSetzen = false;
		this.shiftDirection = -1;
		this.firstShift = true;
		this.shipPlaceHelper = new ArrayList<Integer>(aktiveSchiffsListe);
		placedShipUnits = new ArrayList<FieldUnit[]>();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// Gdx.app.log("GestureDetectorTest", "tochDown at " + x + ", " + y);
		flinging = false;
		initialScale = camera.zoom;

		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y
		// + ", count: " + count);

		// Bildschirmkoordinaten transformieren zu Weldkoordinaten
		// touch.x und touch.y mit count für anzahl der tap
		Vector3 touch = new Vector3(x, y, 0);
		camera.unproject(touch);

		// Spielverlauf
		if (state.get(3) || state.get(3) && !game.isEnd()) {
			// game.touchEvent(touch.x, touch.y);
		}

		// Spieler Spielfeld Schiffe platzieren
		// if(state.get(3)){
		// //TODO TESTZWECK entwecher erweitern oder löschen.
		// FieldUnit unit =
		// game.getFirstFieldPlayer().getElementByXPosYPos(touch.x, touch.y);
		// if(unit!=null){
		// Gdx.app.log("GestureDetectorTest", "tap at " + touch.x + ", " +
		// touch.y
		// + ", count: " + count);
		// Gdx.app.log("tap", "unit Gefunden!");
		// }
		// }

		// // Hinzufügen von Schiffsteil
		// EntityShip tmpShip = new EntityShip(
		// game.getFirstFieldPlayer().getShipTextures().get("rhk"),game.getFirstFieldPlayer().getShipTextures().get("rhka"),
		// new Vector2(
		// unit.getXpos(), unit.getYpos()),
		// new Vector2(GameFieldScreen.size, GameFieldScreen.size),
		// game.getFirstFieldPlayer().getMapTileLayer());
		// unit.setEntityShipDrawUnit(tmpShip);
		// unit.setOccupied(true);
		//
		//
		// }else{
		// Gdx.app.log("GestureDetectorTest", "tap at " + touch.x + ", " +
		// touch.y
		// + ", count: " + count);
		// Gdx.app.log("tap", "unit nicht Gefunden!");
		// }
		// }

		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		Gdx.app.log("GestureDetectorTest", "long press at " + x + ", " + y);
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		flinging = true;
		velX = camera.zoom * velocityX * 0.5f;
		velY = camera.zoom * velocityY * 0.5f;
		return false;
	}

	public boolean panStart(float x, float y, float deltaX, float deltaY) {
		panStart = true;
		// Gdx.app.log("GestureDetectorTest", "pan Start at " + x + ", " + y);
		// Bildschirmkoordinaten transformieren zu Weldkoordinaten
		Vector3 touch = new Vector3(x, y, 0);
		camera.unproject(touch);
		touchOld = new Vector3(touch.x, touch.y, 0);
//		System.out.println("Start Bildschirm zu Weltkoordinaten: " + "X: "
//				+ touch.x + " Y: " + touch.y);

		// Spieler Spielfeld Schiffe platzieren
		if (state.get(3)) {
			FieldUnit unit = game.getFirstFieldPlayer().getElementByXPosYPos(
					touch.x, touch.y);
			if (unit != null
					&& (shipPlaceHelper.get(3) > 0
							|| shipPlaceHelper.get(2) > 0
							|| shipPlaceHelper.get(1) > 0 || shipPlaceHelper
							.get(0) > 0)&&!overlayingOtherShips(unit)) {
//				System.out.println("unit Start");
				aktivatorSchiffSetzen = true;
				// Initialisieren der Schiffstexturen
				shipBack = game.getFirstFieldPlayer().getShipTextures()
						.get("uvk");
				shipBackA = game.getFirstFieldPlayer().getShipTextures()
						.get("uvka");
				// Hinzufügen von Schiffsteil
				EntityShip tmpShip = new EntityShip(
						shipBack,
						shipBackA,
						new Vector2(unit.getXpos(), unit.getYpos()),
						new Vector2(GameFieldScreen.size, GameFieldScreen.size),
						game.getFirstFieldPlayer().getMapTileLayer());
				unit.setEntityShipDrawUnit(tmpShip);
				unit.setOccupied(true);

				// Schiffslänge hinzufügen
				unitLocation = new FieldUnit[1];
				unitLocation[0] = unit;
				shipLastUnit = unit;
			}

		}

		return false;
	}



	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// Gdx.app.log("GestureDetectorTest", "pan at " + x + ", " + y);
		Vector3 touch = new Vector3(x, y, 0);
		camera.unproject(touch);
		// System.out.println("pan Bildschirm zu Weltkoordinaten: " + "X: "
		// + touch.x + " Y: " + touch.y);

		if (!panStart) {
			panStart(x, y, deltaX, deltaY);
		} else {
			// Spieler Spielfeld Schiffe platzieren
			if (aktivatorSchiffSetzen) {
				FieldUnit unit = game.getFirstFieldPlayer()
						.getElementByXPosYPos(touch.x, touch.y);
				if (unit != null
						&& !unit.equals(unitLocation[0])
						&& firstShift
						&& (shipPlaceHelper.get(3) > 0
								|| shipPlaceHelper.get(2) > 0 || shipPlaceHelper
								.get(1) > 0)
						&&!overlayingOtherShips(unit)
						&& !isNextUnitinsideCorner(shipLastUnit, unit)) {
					// Beim ersten shift werden Texturen festgelegt werden.
					firstShift = false;
//					System.out.println("Unit Gefunden firstShift");
					if (unit.equals(unitLocation[0].get_lNeighbor())) {
						shiftDirection = 0;
//						Gdx.app.log("FirstShift", "leftShift");
						// Texturen setzen
						shipBack = game.getFirstFieldPlayer()
								.getShipTextures().get("lhb");
						shipBackA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("lhba");
						shipMiddle =game
								.getFirstFieldPlayer().getShipTextures()
								.get("lhm");
						shipMiddleA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("lhma");
						shipFront =game
								.getFirstFieldPlayer().getShipTextures()
								.get("lhf");
						shipFrontA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("lhfa");
							shipNextUnit = unit.get_lNeighbor();
					}
					if (unit.equals(unitLocation[0].get_rNeighbor())) {
						shiftDirection = 1;
//						Gdx.app.log("FirstShift", "rightShift");
						shipBack = game.getFirstFieldPlayer()
								.getShipTextures().get("rhb");
						shipBackA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("rhba");
						shipMiddle = game
								.getFirstFieldPlayer().getShipTextures()
								.get("rhm");
						shipMiddleA =game
								.getFirstFieldPlayer().getShipTextures()
								.get("rhma");
						shipFront = game
								.getFirstFieldPlayer().getShipTextures()
								.get("rhf");
						shipFrontA =game
								.getFirstFieldPlayer().getShipTextures()
								.get("rhfa");
						shipNextUnit = unit.get_rNeighbor();
					}
					if (unit.equals(unitLocation[0].get_uNeighbor())) {
						shiftDirection = 2;
//						Gdx.app.log("FirstShift", "upShift");
						shipBack = game.getFirstFieldPlayer()
								.getShipTextures().get("uvb");
						shipBackA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvba");
						shipMiddle = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvm");
						shipMiddleA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvma");
						shipFront = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvf");
						shipFrontA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvfa");
						shipNextUnit = unit.get_uNeighbor();
					}

					if (unit.equals(shipLastUnit.get_oNeighbor())) {
						shiftDirection = 3;
//						Gdx.app.log("FirstShift", "downShift");
						// TODO Texturen nach unten hinzufügen
						shipBack = game.getFirstFieldPlayer()
								.getShipTextures().get("uvf");
						shipBackA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvfa");
						shipMiddle = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvm");
						shipMiddleA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvma");
						shipFront = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvb");
						shipFrontA = game
								.getFirstFieldPlayer().getShipTextures()
								.get("uvba");
						shipNextUnit = unit.get_oNeighbor();
					}

					// Hinzufügen von Schiffsteil
					EntityShip tmpShip = new EntityShip(shipFront, shipFrontA,
							new Vector2(unit.getXpos(), unit.getYpos()),
							new Vector2(GameFieldScreen.size,
									GameFieldScreen.size), game
									.getFirstFieldPlayer().getMapTileLayer());
					unit.setEntityShipDrawUnit(tmpShip);
					unit.setOccupied(true);



					// Schiffslänge hinzufügen
					FieldUnit[] tmpUnitLocation = new FieldUnit[unitLocation.length + 1];
					tmpUnitLocation[0] = unitLocation[0];
					tmpUnitLocation[1] = unit;
					unitLocation = tmpUnitLocation;
					
					// Setzen der richtigen Schiffstextur für Hinten
					unitLocation[0].getEntityShipDrawUnit()
							.setShipTextureRegion(shipBack, shipBackA);
					shipLastUnit = unit;

					// Wenn keine Zerstörer und Schlachtschiffe da sind.
					if (shipPlaceHelper.get(3) == 0
							&& shipPlaceHelper.get(2) == 0) {
						shipNextUnit = null;
					}
				}
				if (unit != null && !firstShift
						&& !unit.equals(unitLocation[unitLocation.length - 1])
						&& unit.equals(shipNextUnit)
						&& !overlayingOtherShips(unit)
						&& !isNextUnitinsideCorner(shipLastUnit, unit)) {
//					System.out.println("Unit Gefunden secondShift");
					// Hinzufügen von Schiffsteil
					EntityShip tmpShip = new EntityShip(shipFront, shipFrontA,
							new Vector2(unit.getXpos(), unit.getYpos()),
							new Vector2(GameFieldScreen.size,
									GameFieldScreen.size), game
									.getFirstFieldPlayer().getMapTileLayer());
					unit.setEntityShipDrawUnit(tmpShip);
					unit.setOccupied(true);

					// shipNextUnit festlegen
					if (shipPlaceHelper.get(3) > 0 && unitLocation.length <= 2) {
						switch (shiftDirection) {
						case 0:// links
							shipNextUnit = unit.get_lNeighbor();
							break;
						case 1:// rechts
							shipNextUnit = unit.get_rNeighbor();
							break;
						case 2:// oben
							shipNextUnit = unit.get_uNeighbor();
							break;
						case 3:// unten
							shipNextUnit = unit.get_oNeighbor();
							break;
						default:
							break;
						}
					}

					// Setzen der richtigen Schiffstextur für letzen
					unitLocation[unitLocation.length - 1]
							.getEntityShipDrawUnit().setShipTextureRegion(
									shipMiddle, shipMiddleA);

					// Schiffslänge hinzufügen
					FieldUnit[] tmpUnitLocation = new FieldUnit[unitLocation.length + 1];
					for (int i = 0; i < unitLocation.length; i++) {
						tmpUnitLocation[i] = unitLocation[i];
					}
					tmpUnitLocation[tmpUnitLocation.length - 1] = unit;
					unitLocation = tmpUnitLocation;
					shipLastUnit = unit;
				}

			}
		}
		// camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0);
		return false;
	}



	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// Gdx.app.log("GestureDetectorTest", "pan stop at " + x + ", " + y);
		panStart = false;
		// Bildschirmkoordinaten transformieren zu Weldkoordinaten
		Vector3 touch = new Vector3(x, y, 0);
		camera.unproject(touch);
//		System.out.println("Stop Bildschirm zu Weltkoordinaten: " + "X: "
//				+ touch.x + " Y: " + touch.y);

		if (aktivatorSchiffSetzen) {
			// boolean zurücksetzen
			aktivatorSchiffSetzen = false;
			firstShift = true;
			shiftDirection = -1;
			shipPlaceHelper.set(unitLocation.length - 1,
					shipPlaceHelper.get(unitLocation.length - 1) - 1);
			unitLocation[0].get_myField().setAllShipsSet(true);
			unitLocation[0].get_myField().setAllShipsSetManual(true);
			/* Klasse Ship: Ausrichtung des Schiffs: 0=rechts, 1=oben, 2=links, 3=unten */
			/* Hilfsfunktion ShipDirection 0=links 1=recht 2=oben 3=unten*/
			int orientation=1;
			switch (shiftDirection) {
			case 0:// links
				orientation=2;
				break;
			case 1:// rechts
				orientation=0;
				break;
			case 2:// oben
				orientation=1;
				break;
			case 3:// unten
				orientation=3;
				break;
			default:
				break;
			}
			
			unitLocation[0].setShipOrientation(orientation);
			
			// Hinzufügen der fertigen unitLocation
			placedShipUnits.add(unitLocation);

//			System.out.println("Anzahl Schiffe auf Feld: "
//					+ placedShipUnits.size());
			// TODO ende des schiffes.
		}
		return false;
	}
	
	/**
	 * Methode prüft ob die aktuelle Unit im Touch nicht in einer der
	 * Ecken von der letzen Unit liegt.
	 * @param lastUnit Die letze Unit
	 * @param unit Die aktuelle Unit im Touch
	 * @return boolean true oder false
	 */
	private boolean isNextUnitinsideCorner(FieldUnit lastUnit, FieldUnit unit) {
		
		if (lastUnit.get_lNeighbor()!=null&&lastUnit.get_lNeighbor().get_oNeighbor() != null) {
			if (unit.equals(lastUnit.get_lNeighbor().get_oNeighbor()))
				return true;
		}
		if (lastUnit.get_lNeighbor()!=null&&lastUnit.get_lNeighbor().get_uNeighbor() != null) {

			if (unit.equals(lastUnit.get_lNeighbor().get_uNeighbor()))
				return true;
		}
		if (lastUnit.get_rNeighbor()!=null&&lastUnit.get_rNeighbor().get_oNeighbor() != null) {

			if (unit.equals(lastUnit.get_rNeighbor().get_oNeighbor()))
				return true;
		}
		if (lastUnit.get_rNeighbor()!=null&&lastUnit.get_rNeighbor().get_uNeighbor() != null) {

			if (unit.equals(lastUnit.get_rNeighbor().get_uNeighbor()))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Methode prüft ob die aktuelle Unit im Touch ein bereits
	 * plaziertes Schiff überdeckt.
	 * @param unit Das Aktuelle Unit im Touch
	 * @return boolean true oder false
	 */
	private boolean overlayingOtherShips(FieldUnit unit) {
		for (FieldUnit[] fieldUnits : placedShipUnits) {
			for (FieldUnit fieldUnit : fieldUnits) {
				if(fieldUnit.equals(unit)){
					return true;
				}
			}
			
		}
		return false;
	}

	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
		if (camera.zoom <= layerZoom) {
			float ratio = originalDistance / currentDistance;
			camera.zoom = initialScale * ratio;
			System.out.println(camera.zoom);
		} else if (camera.zoom >= layerZoom * 2) {
			camera.zoom -= layerZoom * 0.5f;
		}
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void update() {
		if (flinging) {
			velX *= 0.98f;
			velY *= 0.98f;
			camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY
					* Gdx.graphics.getDeltaTime(), 0);
			if (Math.abs(velX) < 0.01f)
				velX = 0;
			if (Math.abs(velY) < 0.01f)
				velY = 0;
		}
		setNewCameraStatePosition();

	}

	/**
	 * Methode setzt die Kamera auf gewünschte position je nach Status. 0=Intro,
	 * 1=FullView, 2=GameFieldZoom, 3=PlayerShips, 4=EnemyShips, 5=GameFieldGrid
	 * 6=PlayerGrid, 7=EnemyGrid
	 * 
	 * @param layerY
	 * @param layerX
	 * @param layerZoom
	 */
	private void setNewCameraStatePosition() {
		if (!state.get(0)) {
			// game.
		}

		// Intro
		if (state.get(0) && (camera.position.x < layerX)) {
			camera.position.y = layerY;
			camera.zoom = layerZoom;
			camera.position.x += 5;
			// Gdx.app.log("Schiffeversenken 1.0: ","Intro Ausführen");
		} else if (state.get(0)) {
			camera.position.x = layerX;
			// State Wechsel
			changeStateTo(state, 1, false);
		}
		// 1=FullView
		if (state.get(1)) {
			if (camera.position.x > layerX) {
				camera.position.x -= 20f;
				if (camera.position.x < layerX)
					camera.position.x = layerX;
			}

			if (camera.position.x < layerX) {
				camera.position.x += 20f;
				if (camera.position.x > layerX)
					camera.position.x = layerX;
			}

			if (camera.position.y > layerY) {
				camera.position.y -= 20f;
				if (camera.position.y < layerY)
					camera.position.y = layerY;
			}

			if (camera.position.y < layerY) {
				camera.position.y += 20f;
				if (camera.position.y > layerY)
					camera.position.y = layerY;
			}

			if (camera.zoom > layerZoom) {
				camera.zoom -= 0.03f;
				if (camera.zoom < layerZoom)
					camera.zoom = layerZoom;
			}

			if (camera.zoom < layerZoom) {
				camera.zoom += 0.03f;
				if (camera.zoom > layerZoom)
					camera.zoom = layerZoom;
			}

		}

		// 2=GameField Zoom
		if (state.get(2)) {
			faktorX = 0.8f;
			faktorY = 1.0f;
			faktorZoom = 0.72f;
			if (camera.position.x > layerX * faktorX) {
				camera.position.x -= 20f;
				if (camera.position.x < layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.x < layerX * faktorX) {
				camera.position.x += 20f;
				if (camera.position.x > layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.y > layerY * faktorY) {
				camera.position.y -= 20f;
				if (camera.position.y < layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.position.y < layerY * faktorY) {
				camera.position.y += 20f;
				if (camera.position.y > layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.zoom > layerZoom * faktorZoom) {
				camera.zoom -= 0.03f;
				if (camera.zoom < layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}

			if (camera.zoom < layerZoom * faktorZoom) {
				camera.zoom += 0.03f;
				if (camera.zoom > layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}

		}

		// PlayerGrid
		if (state.get(3)) {
			faktorX = 0.75f;
			faktorY = 0.7f;
			faktorZoom = 0.65f;
			if (camera.position.x > layerX * faktorX) {
				camera.position.x -= 20f;
				if (camera.position.x < layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.x < layerX * faktorX) {
				camera.position.x += 20f;
				if (camera.position.x > layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.y > layerY * faktorY) {
				camera.position.y -= 20f;
				if (camera.position.y < layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.position.y < layerY * faktorY) {
				camera.position.y += 20f;
				if (camera.position.y > layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.zoom > layerZoom * faktorZoom) {
				camera.zoom -= 0.03f;
				if (camera.zoom < layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}

			if (camera.zoom < layerZoom * faktorZoom) {
				camera.zoom += 0.03f;
				if (camera.zoom > layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}
		}

		// EnemyGrid
		if (state.get(4)) {
			faktorX = 0.75f;
			faktorY = 1.3f;
			faktorZoom = 0.65f;
			if (camera.position.x > layerX * faktorX) {
				camera.position.x -= 20f;
				if (camera.position.x < layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.x < layerX * faktorX) {
				camera.position.x += 20f;
				if (camera.position.x > layerX * faktorX)
					camera.position.x = layerX * faktorX;
			}

			if (camera.position.y > layerY * faktorY) {
				camera.position.y -= 20f;
				if (camera.position.y < layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.position.y < layerY * faktorY) {
				camera.position.y += 20f;
				if (camera.position.y > layerY * faktorY)
					camera.position.y = layerY * faktorY;
			}

			if (camera.zoom > layerZoom * faktorZoom) {
				camera.zoom -= 0.03f;
				if (camera.zoom < layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}

			if (camera.zoom < layerZoom * faktorZoom) {
				camera.zoom += 0.03f;
				if (camera.zoom > layerZoom * faktorZoom)
					camera.zoom = layerZoom * faktorZoom;
			}
		}
	}

	/**
	 * Hilfsmethode, dient dem setzen eines Kamerazustandes.
	 * 
	 * 0=Intro, 1=FullView, 2=GameFieldZoom, 3=PlayerShips, 4=EnemyShips,
	 * 5=GameFieldGrid 6=PlayerGrid, 7=EnemyGrid
	 * 
	 * @param state
	 *            ArrayList<Boolean> mit den jeweiligen Zuständen
	 * @param toStateNumber
	 *            Der ausgewählte Zustand
	 * @param grid
	 *            Boolean der im jeweiligen Zustand den Grid Zustand mit
	 *            aktiviert.
	 */
	public static void changeStateTo(ArrayList<Boolean> state,
			int toStateNumber, boolean grid) {
		for (int i = 0; i < state.size(); i++) {
			if (i == toStateNumber) {
				state.set(i, true);
				if (grid) {
					if (toStateNumber == 2) {
						state.set(5, true);
						state.set(6, false);
						state.set(7, false);
					}
					if (toStateNumber == 3) {
						state.set(5, false);
						state.set(6, true);
						state.set(7, false);
					}
					if (toStateNumber == 4) {
						state.set(5, false);
						state.set(6, false);
						state.set(7, true);
					}
				}
			} else {
				if (grid && (i == 5 || i == 6 || i == 7))
					continue;
				state.set(i, false);
			}
		}

	}

	public void setShipPlaceHelper(ArrayList<Integer> shipPlaceHelper) {
		this.shipPlaceHelper = new ArrayList<Integer>(shipPlaceHelper);
		;
	}

	public ArrayList<FieldUnit[]> getPlacedShipUnits() {
		return placedShipUnits;
	}

}