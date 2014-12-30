package com.ma.schiffeversenken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ma.schiffeversenken.android.controller.Game;
import com.ma.schiffeversenken.android.model.GamePreferences;
import com.ma.schiffeversenken.android.model.Player;
import com.ma.schiffeversenken.android.model.Ship;

public class GameFieldScreen implements Screen {

	public static final String TITLE = "Schiffeversenken";
	public static final String LOG = "gdxlog";

	private TiledMap map;
	private TiledMapTileLayer mapTileLayer;
	private TiledMapTileSet tileSetShips;

	// Renderer h�lt einen SpriteBatch f�rs zeichnen bereit
	private OrthogonalTiledMapRenderer renderer;
	private Batch batch;
	public static OrthographicCamera camera;

	// h�he und breite
	private int h;
	private int w;

	// Einheitsgr��e der Texturen
	public static final int size = 64;

	// Environment f�r Lichtefekkte
	private Environment environment;

	// Spiellogic
	private Player player;
	private ArrayList<EntityShip> tilesPlayerShips;
	private ArrayList<EntityShip> tilesEnemyShips;
	private Iterator<EntityShip> tileIterator;

	// TEstzwecke
	private EntityShip ship;

	// Texturen f�r Schrift und buttons
	private Stage stage;
	private Skin skin;
	private Table table;
	private Table table2;
	private TextureAtlas atlas;
	private TextButton buttonGenerateShips=null, buttonSelfPlaceShips=null, buttonStart=null,buttonClearShips=null;
	private BitmapFont white, black;
	private Label heading;

	// Background State
	private int[] background = { 0 }, water = { 1 }, ships = { 2 },
			attack = { 3 };

	// ShapeRenderer f�r GridObjekte
	private ShapeRenderer sr;

	private float layerX;
	private float layerY;
	private float layerZoom;
	private ArrayList<Boolean> state;// 0=Intro, 1=FullView 2=GameFieldZoom,
										// 3=PlayerShips,4=EnemyShips,5=GameGrid

	// Intro Textur
	private Texture introTexture;
	private TextureRegion introTextureRegion;

	private CameraController controller;

	private GestureDetector gestureDetector;

	private Texture randTexture;

	private TextureRegion randTextureRegion;

	private TextureRegion randTextureRegionUp;

	private TextureRegion randTextureRegionUpRight;

	GamePreferences mGamePreferences;

	private InputMultiplexer inputMultiplexer;

	//Einstellung wie viele Schiffe zu setzensind.
	private ArrayList<Integer> schiffsEinstellung;

	private boolean primaryBTGame, secondaryBTGame;
	public GameFieldScreen(boolean primaryBTGame, boolean secondaryBTGame){
		this.primaryBTGame = primaryBTGame;
		this.secondaryBTGame = secondaryBTGame;
	}
	
	@Override
	public void show() {
		Gdx.app.log(LOG, "Hello World");
		// Tiled Maps,Layer und tileSet laden um diese zu nutzen
		map = new TmxMapLoader().load("maps/map.tmx");
		mapTileLayer = (TiledMapTileLayer) map.getLayers().get("0");
		tileSetShips = map.getTileSets().getTileSet("ships");

		// graphics High and width
		h = Gdx.graphics.getHeight();
		w = Gdx.graphics.getWidth();
		// Wegen resize Aufruf nach erstellen ist die �bergabe von w/h unn�tig
		camera = new OrthographicCamera();
		// camera.zoom 1.4 camera.position.x 510.0 camera.position.y 710.0
		camera.viewportWidth = w;
		camera.viewportHeight = h;
		layerX = mapTileLayer.getWidth() * mapTileLayer.getTileWidth() / 2;
		layerY = mapTileLayer.getHeight() * mapTileLayer.getTileHeight() / 2;
		camera.position.set(-layerX, layerY, 0);
		// zoomarichmetik um jede Aufl�sung zu unterst�tzen
		float zoomfaktor = ((0.95f * 1920 / h));
		camera.zoom = zoomfaktor;
		layerZoom = camera.zoom;
		camera.update();

		// TODO LADEN ERWEITERN
		loadPlayerData();

		// State initialisieren
		state = new ArrayList<Boolean>();
		for (int i = 0; i < 8; i++) {
			state.add(new Boolean(false));
		}
		// Intro
		state.set(0, true);
		
		//TODO CameraController �bergeben wie viele Schiffe zu setzen.
		schiffsEinstellung = new ArrayList<Integer>(4);
		 schiffsEinstellung.add(4);//0, kreuzer
		 schiffsEinstellung.add(3);//1,Uboot
		 schiffsEinstellung.add(2);//2,schlachtschiff
		 schiffsEinstellung.add(1);//3,Zerst�rer
		// Touch Events
		controller = new CameraController(camera, layerX, layerY, layerZoom,
				player,state,schiffsEinstellung);
		gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, controller);

		// Ambiente
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,
				0.8f, 0.8f, 1.0f));

		// renderer kann man noch ein skalierungsfaktor mitgeben.
		renderer = new OrthogonalTiledMapRenderer(map);

		// SpriteBatch vom Renderer
		batch = renderer.getSpriteBatch();

		// Neuer ShapeRenderer um Objektlayer zu zeichnen f�rs GameGrid
		sr = new ShapeRenderer();

		// Background Texturen laden.
		introTexture = new Texture(Gdx.files.internal("graphics//Intro.png"));
		introTextureRegion = new TextureRegion(introTexture);
		randTexture = new Texture(Gdx.files.internal("graphics//Rand.png"));
		randTextureRegion = new TextureRegion(randTexture);
		randTextureRegion.flip(true, false);
		randTextureRegionUp = new TextureRegion(randTexture);
		randTextureRegionUp.flip(false, true);
		randTextureRegionUpRight = new TextureRegion(randTexture);
		randTextureRegionUpRight.flip(true, true);

		// Schrift und Buttons laden
		stage = new Stage();

		atlas = new TextureAtlas("ui/button.pack");
		skin = new Skin(atlas);

		table = new Table(skin);
		// Set table to whole Screen
//		table.setBounds(w*0.1f, h*0.6f, w*0.9f, h*0.7f);// Container f�r Label und Buttons
		table.setBounds(0, 0, w, h*1.9f);// Container f�r Label und Buttons

		//Fonts erstellen
		white = new BitmapFont(Gdx.files.internal("font/Latin_white.fnt"),
				false);
		white.setScale(w*0.002f);
		black = new BitmapFont(Gdx.files.internal("font/Latin_black.fnt"),
				false);
		black.setScale(w*0.002f);
		
		//Erstellen des Headers
		final LabelStyle headingStyle = new LabelStyle(white,Color.WHITE);
		heading = new Label("Schiffe", headingStyle);

		// Animationen f�r den Button
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("buttonUp");
		textButtonStyle.down = skin.getDrawable("buttonDown");
		textButtonStyle.pressedOffsetX = 1; // 1 nach x bewegen
		textButtonStyle.pressedOffsetY = -1;// -1 auf y achse bewegen
		textButtonStyle.font = black;
		

		// Button erstellen mit dem Style
		buttonGenerateShips = new TextButton("generieren",
				textButtonStyle);
		buttonGenerateShips.pad(w*0.02f);
		buttonGenerateShips.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.getGame().getFirstFieldPlayer().generateNewShipplacement(schiffsEinstellung);
				ArrayList<Integer> tmpEmptyShipList = new ArrayList<Integer>(4);
				tmpEmptyShipList.add(0);
				tmpEmptyShipList.add(0);
				tmpEmptyShipList.add(0);
				tmpEmptyShipList.add(0);
				controller.setShipPlaceHelper(tmpEmptyShipList);
			}
		});
		
		buttonSelfPlaceShips = new TextButton("platzieren",textButtonStyle);
		buttonSelfPlaceShips.pad(w*0.02f);
		buttonSelfPlaceShips.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(player.getGame().getFirstFieldPlayer().isAllShipsSet()){
					player.getGame().getFirstFieldPlayer().resetField();//Spielfeld zur�cksetzen
				}
				CameraController.changeStateTo(state, 3, true);
				table.clear();
				table.add(heading).colspan(5);
				table.row();
				table.add(buttonGenerateShips).minWidth(w*0.2f).minHeight(w*0.1f);
				table.add().minWidth(w*0.01f);
				table.add(buttonStart).minWidth(w*0.2f).minHeight(w*0.1f);
				table.add().minWidth(w*0.01f);
				table.add(buttonClearShips).minWidth(w*0.2f).minHeight(w*0.1f);
				
				table2 = new Table(skin);
//				table2.setBounds(w*0.1f, h*0.5f, w*0.9f, h*0.7f);
				table2.setBounds(0, 0, w, h*1.68f);
				table2.add(new Label("Dr�cke auf ein Feld",headingStyle)).colspan(5);
				table2.row();
				table2.add(new Label("um ein Schiff zu platzieren",headingStyle)).colspan(5);
				table2.row();
				table2.add(new Label("und ziehe es in die gew�nschte Richtung",headingStyle)).colspan(5);
				stage.addActor(table2);
			}
		});
		
		buttonClearShips = new TextButton("l�schen",textButtonStyle);
		buttonClearShips.pad(w*0.02f);
		buttonClearShips.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
					player.getGame().getFirstFieldPlayer().resetField();//Spielfeld zur�cksetzen
					controller.setShipPlaceHelper(schiffsEinstellung);
			}
		});
		
		buttonStart = new TextButton("Start",textButtonStyle);
		buttonStart.pad(w*0.02f);
		buttonStart.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(player.getGame().getSecondFieldEnemy().isAllShipsSet() || primaryBTGame || secondaryBTGame){
					if(player.getGame().getFirstFieldPlayer().isAllShipsSet() || primaryBTGame || secondaryBTGame){
						//Setzen der Schiffe und Starten.
						//Wenn schiffe manuell gesetzt m�ssen diese aufs Feld, sonst sind diese schon auf dem Feld
						if(player.getGame().getFirstFieldPlayer().getAllShipsSetManual()){
						//TODO delete startMethode �berpr�fen
//						if(controller.getPlacedShipUnits()!=null){
//							System.out.println("Ships there");
//							for(int i=0;i<controller.getPlacedShipUnits().size();i++){
//								
//								for(int j=0;j<controller.getPlacedShipUnits().get(i).length;j++){
//									System.out.println(controller.getPlacedShipUnits().get(i)[j].getEntityShipDrawUnit().toString());
//									if(controller.getPlacedShipUnits().get(i)[j].getEntityShipDrawUnit().getShipTextureRegion()!=null)
//										System.out.println("Textur");
//									
//								}
//							}
//						}else{
//							System.out.println("Where are the ships gone?");
//						}
						//TODO ende  Methode �berpr�fen
						player.getGame().getFirstFieldPlayer().setManualNewShipplacement(controller.getPlacedShipUnits());
						}
						ArrayList<Integer> tmpEmptyShipList = new ArrayList<Integer>(4);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						controller.setShipPlaceHelper(tmpEmptyShipList);
						
						CameraController.changeStateTo(state, 2, false);
						Gdx.input.setInputProcessor(gestureDetector);

						
						//TODO DEBUGGEN
//						for(Ship s: player.getFirstField().getShips()){
//							Gdx.app.log("Schiff Manuell Plaziert: ", s.getName()+" Back at y:"+s.getLocation()[0].getYpos());
//						}
						
						//TODO Optimieren f�r BLuetooth
						player.getGame().start();
					}else{
						//Setzen der Schiffe und Starten.
						player.getGame().getFirstFieldPlayer().generateNewShipplacement(schiffsEinstellung);
						ArrayList<Integer> tmpEmptyShipList = new ArrayList<Integer>(4);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						tmpEmptyShipList.add(0);
						controller.setShipPlaceHelper(tmpEmptyShipList);
						CameraController.changeStateTo(state, 2, false);
	
						
						//TODO Optimieren f�r BLuetooth
						player.getGame().start();
					}
				}

			}
		});
		

		
		// Hinzuf�gen vom Elementen zur Tabelle Start
//		table.center();
		table.debug();
		table.add(heading).colspan(5);
		table.row();
		table.add(buttonGenerateShips).minWidth(w*0.2f).minHeight(w*0.1f);
		table.add().minWidth(w*0.01f);
		table.add(buttonStart).minWidth(w*0.2f).minHeight(w*0.1f);
		table.add().minWidth(w*0.01f);
		table.add(buttonSelfPlaceShips).minWidth(w*0.2f).minHeight(w*0.1f);
//		table.row();
	

		// table.debug();//Rote lienien zum Debuggen
		stage.addActor(table);
		
		//Setzen von mehreren InputListenern
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(gestureDetector);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	/**
	 * Methode l�dt alte Spieldaten
	 */
	private void loadPlayerData() {
		if (Gdx.files.local("player.bin").exists()
				&& Gdx.files.isLocalStorageAvailable()) {
			System.out.println("Player Exists. Reading File ...");
			try {
				player = Player.readPlayer(tileSetShips, map);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// TODO TEST OB LADEN GEHT
		} else {
			System.out
					.println("Player does not exist. Creating new player ...");
			try {
				player = new Player(tileSetShips, map, primaryBTGame, secondaryBTGame);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO TEST OB LADEN
		}
	}

	@Override
	public void render(float delta) {
	

		// Gdx.app.log(TITLE, "render(...)");
		player.update(camera);
		controller.update();
		camera.update();
		// Render the things after show()
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);

		// Dem Renderer die camera �bergeben.
		renderer.setView(camera);
		renderer.render();

		// Animation bg
//		renderer.render(ships);

		// Draw Stuff
		// Randgebiete
		batch.begin();
		batch.draw(introTextureRegion, -layerX * 2, layerY * 2);
		batch.draw(introTextureRegion, -layerX * 2, -layerY * 2);
		batch.draw(randTextureRegionUp, 0, layerY * 2);
		batch.draw(randTextureRegionUpRight, layerX * 2, layerY * 2);
		batch.draw(introTextureRegion, 0, -layerY * 2);
		batch.draw(introTextureRegion, layerX * 2, -layerY * 2);
		batch.draw(randTextureRegion, layerX * 2, 0);
		batch.draw(introTextureRegion, -layerX * 2, 0);
		// Weiter Au�erhalb
		// links
		batch.draw(introTextureRegion, -layerX * 2 * 2, layerY * 2);
		batch.draw(introTextureRegion, -layerX * 2 * 2, 0);
		batch.draw(introTextureRegion, -layerX * 2 * 2, -layerY * 2);
		// rechts
		batch.draw(introTextureRegion, layerX * 2 * 2, layerY * 2);
		batch.draw(introTextureRegion, layerX * 2 * 2, 0);
		batch.draw(introTextureRegion, layerX * 2 * 2, -layerY * 2);
		// unten
		batch.draw(introTextureRegion, -layerX * 2 * 2, -layerY * 2 * 2);
		batch.draw(introTextureRegion, -layerX * 2, -layerY * 2 * 2);
		batch.draw(introTextureRegion, 0, -layerY * 2 * 2);
		batch.draw(introTextureRegion, layerX * 2, -layerY * 2 * 2);
		batch.draw(introTextureRegion, layerX * 2 * 2, -layerY * 2 * 2);
		// oben
		batch.draw(introTextureRegion, -layerX * 2 * 2, layerY * 2 * 2);
		batch.draw(introTextureRegion, -layerX * 2, layerY * 2 * 2);
		batch.draw(introTextureRegion, 0, layerY * 2 * 2);
		batch.draw(introTextureRegion, layerX * 2, layerY * 2 * 2);
		batch.draw(introTextureRegion, layerX * 2 * 2, layerY * 2 * 2);
		batch.end();

		// Spielfelder
		batch.begin();
		player.draw(batch);
		batch.end();

		// Animation bg Attack layer
//		renderer.render(attack);

		// TODO Animate Fireing some canons and ships getting into position.
//		player.animatedTiles();

		if (state.get(2)||state.get(5) || state.get(6) || state.get(7)) {
			// render Objects
			// Wie renderer.setView(camera.combined) Transformieren der Shapes
			// auf
			// die cam position/koordinaten.
			sr.setProjectionMatrix(camera.combined);
			sr.setColor(Color.GRAY);
			String objektebene = "";
			if (state.get(5)) {
				objektebene = "GameField";
			}
			if (state.get(6)) {
				objektebene = "GameFieldPlayer";
			}
			if (state.get(7)||state.get(2)&&player.getGame().getGamersTurn()==0) {
				objektebene = "GameFieldEnemy";
			}

			if(!objektebene.equals("")){
				
			// RectangleMapObject, CircleMapObject,
			// PolylineMapObject, EllipseMapObject, PolygonMapObject.
			for (MapObject object : map.getLayers().get(objektebene)
					.getObjects()) {
				if (object instanceof RectangleMapObject) {
					Rectangle rt = ((RectangleMapObject) object).getRectangle();
					sr.begin(ShapeType.Line);
					sr.rect(rt.x, rt.y, rt.width, rt.height);
					sr.end();
				}
				}
			}
		}

		
		//InputProzessor
		if(state.get(1)||state.get(3)){
//			Gdx.input.setInputProcessor(stage);
			stage.act(delta);
			stage.draw();
		}else{
//		Gdx.input.setInputProcessor(gestureDetector);
		}

	}

	@Override
	public void resize(int width, int height) {
		// Gdx.app.log(TITLE, "resize(...)");
		// TODO globale variable oder referenz auf map gr��e
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		// camera.position.set(width/2f, height/2f, 0);
		// camera.zoom=0.5f;
		camera.update();
	}

	@Override
	public void hide() {
		// Gdx.app.log(TITLE, "hide()");
		// TODO Auto-generated method stub
		dispose();

	}

	@Override
	public void pause() {
		// Gdx.app.log(TITLE, "pause()");
		// TODO Save game fields
		// try {
		// Player.savePlayer(player);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	@Override
	public void resume() {
		// Gdx.app.log(TITLE, "resume()");
		// //TODO load game fields
	}

	@Override
	public void dispose() {
		// Gdx.app.log(TITLE, "dispose()");
		// TODO implement save
		// try {
		// Player.savePlayer(player);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		atlas.dispose();
		player.dispose();// TODO Rekursiv alle texturen
		batch.dispose();
		map.dispose();
		introTexture.dispose();
		stage.dispose();
		skin.dispose();
		// ship.getTexture().dispose();// Wichtig texturen dispose()

	}

}
