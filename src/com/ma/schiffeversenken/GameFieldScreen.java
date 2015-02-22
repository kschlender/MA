package com.ma.schiffeversenken;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Iterator;

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
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
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
import com.ma.schiffeversenken.android.AndroidLauncher;
import com.ma.schiffeversenken.android.controller.BluetoothConnectedThread;
import com.ma.schiffeversenken.android.controller.Game;
import com.ma.schiffeversenken.android.model.Allgemeinesdreieck;
import com.ma.schiffeversenken.android.model.Field;
import com.ma.schiffeversenken.android.model.Player;
import com.ma.schiffeversenken.android.view.BackActivity;
/**
 * Klasse Handelt die Game Screens.
 * 
 * @author Klaus Schlender
 */
public class GameFieldScreen implements Screen {

	public static final String TITLE = "Schiffeversenken 1.0";
	public static int buttonwidth;
	public static int buttonheight;

	private TiledMap map;
	private TiledMapTileLayer mapTileLayer;
	private TiledMapTileSet tileSetShips;

	// Renderer haelt einen SpriteBatch fuers zeichnen bereit
	private OrthogonalTiledMapRenderer renderer;
	private Batch batch;
	public static OrthographicCamera camera;

	// Hoehe und Breite
	private int h;
	private int w;

	// Einheitsgroesse der Texturen
	public static final int size = 64;
	
	// Environment fuer Lichtefekkte
	private Environment environment;

	// Spiellogic
	private Player player;

	// Texturen fuer Schrift und buttons
	private Stage stage;
	private Skin skin;
	private Table table;
	private Table table2;
	private TextureAtlas atlas;
	private TextButton buttonGenerateShips=null, buttonSelfPlaceShips=null, buttonStart=null,buttonClearShips=null;
	private BitmapFont white, black;
	private Label heading;
	private LabelStyle headingStyle;

	// Background State
	private int[] background = { 0 }, water = { 1 }, ships = { 2 },
			attack = { 3 };

	// ShapeRenderer fuer GridObjekte
	private ShapeRenderer sr;

	private float layerX;
	private float layerY;
	private float layerZoom;
	// 0=Intro, 1=FullView, 2=GameFieldZoom, 3=PlayerShips, 4=EnemyShips,
	// 5=GameFieldGrid 6=PlayerGrid, 7=EnemyGrid,8=NewGame
	private ArrayList<Boolean> state;

	// Intro Textur
	private Texture introTexture;
	private TextureRegion introTextureRegion;
	private CameraController controller;
	private GestureDetector gestureDetector;
	private Texture randTexture;
	private TextureRegion randTextureRegion;
	private TextureRegion randTextureRegionUp;
	private TextureRegion randTextureRegionUpRight;
	private InputMultiplexer inputMultiplexer;

	//Einstellung wie viele Schiffe zu setzensind.
	private ArrayList<Integer> schiffsEinstellung;

	private boolean primaryBTGame, secondaryBTGame;
	private TextButton buttonRestart;
	private MyGdxGameField parentScreen;
	private boolean restartGame;
	
	public GameFieldScreen(boolean restartGame, MyGdxGameField parentScreen, boolean primaryBTGame, boolean secondaryBTGame){
		this.restartGame=restartGame;
		this.parentScreen=parentScreen;
		this.primaryBTGame = primaryBTGame;
		this.secondaryBTGame = secondaryBTGame;
		//Fuer Meldungen ausserhalb der Anwendung innerhalb BluetoothConnectedThread
		if(parentScreen.getAndroidLauncher()!=null&&primaryBTGame||secondaryBTGame){
		BluetoothConnectedThread.getInstance().setAndroidLauncher(parentScreen.getAndroidLauncher());
		}
	}
	
	@Override
	public void show() {
		// Tiled Maps,Layer und tileSet laden um diese zu nutzen
		map = new TmxMapLoader().load("maps/map.tmx");
		mapTileLayer = (TiledMapTileLayer) map.getLayers().get("0");
		tileSetShips = map.getTileSets().getTileSet("ships");

		// graphics High and width
		h = Gdx.graphics.getHeight();
		w = Gdx.graphics.getWidth();
		
		//Buttongestaltung initialisieren.
		buttonwidth=(int) (w*0.3);
		buttonheight=(int) (w*0.1);
		
		//Kamera einstellung
		//Wegen resize Aufruf nach erstellen ist hier die Uebergabe von w/h unnoetig
		camera = new OrthographicCamera();
		camera.viewportWidth = w;
		camera.viewportHeight = h;
		
		layerX = mapTileLayer.getWidth() * mapTileLayer.getTileWidth() / 2;
		layerY = mapTileLayer.getHeight() * mapTileLayer.getTileHeight() / 2;
		if(restartGame){
			camera.position.set(layerX, layerY, 0);			
		}else{
			camera.position.set(-layerX, layerY, 0);			
		}
		//Eigene Formel fuer Zoom-Arichmetik um jede Aufloesung zu unterstuetzen
		float zoomfaktor = ((0.95f * 1920 / h));
		camera.zoom = zoomfaktor;
		layerZoom = camera.zoom;
		camera.update();

		// State initialisieren
		state = new ArrayList<Boolean>();
		for (int i = 0; i < 9; i++) {
			state.add(Boolean.valueOf(false));
		}
		// Intro
		state.set(0, true);
		
		// TODO LADEN ERWEITERN
		loadPlayerData();
		
		//Standarteinstellung
		schiffsEinstellung = new ArrayList<Integer>(4);
		schiffsEinstellung.add(4);//0, kreuzer
		schiffsEinstellung.add(3);//1,Uboot
		schiffsEinstellung.add(2);//2,schlachtschiff
		schiffsEinstellung.add(1);//3,Zerstoerer
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

		// Neuer ShapeRenderer um Objektlayer zu zeichnen fuers GameGrid
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
		table2 = new Table(skin);
		//Tabellenausrichtung
		table.setBounds(0, 0, w, h*1.76f);// Container f�r Label und Buttons

		//Fonts erstellen
		white = new BitmapFont(Gdx.files.internal("font/Latin_white.fnt"),
				false);
		white.setScale(w*0.002f);
		black = new BitmapFont(Gdx.files.internal("font/Latin_black.fnt"),
				false);
		black.setScale(w*0.002f);
		
		//Erstellen des Headers
		headingStyle = new LabelStyle(white,Color.WHITE);
		heading = new Label("Schiffeversenken", headingStyle);

		// Animationen fuer den Button
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("buttonUp");
		textButtonStyle.down = skin.getDrawable("buttonDown");
		textButtonStyle.pressedOffsetX = 1; // 1 nach x bewegen
		textButtonStyle.pressedOffsetY = -1;// -1 auf y achse bewegen
		textButtonStyle.font = black;

		// Button erstellen mit dem Style
		buttonGenerateShips = new TextButton("generieren",
				textButtonStyle);
		buttonGenerateShips.pad((int)(w*0.02f));
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
		buttonSelfPlaceShips.pad((int)(w*0.02f));
		buttonSelfPlaceShips.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(player.getGame().getFirstFieldPlayer().isAllShipsSet()){
					player.getGame().getFirstFieldPlayer().resetField();
					controller.setShipPlaceHelper(schiffsEinstellung);
				}
				table.clear();
				table.add(heading).colspan(1);
				table.row();
				table.add(buttonStart).minWidth(buttonwidth).minHeight(buttonheight);
				table.row();
				table.add().minHeight(buttonwidth*0.02f);
				table.row();
				table.add(buttonGenerateShips).minWidth(buttonwidth).minHeight(buttonheight);
				table.row();
				table.add().minHeight(buttonwidth*0.02f);
				table.row();
				table.add(buttonClearShips).minWidth(buttonwidth).minHeight(buttonheight);
				
				table2.clear();
				table2.setBounds(0, 0, w, h*0.2f);
				table2.add(new Label("Drücke auf ein Feld um",headingStyle)).colspan(5);
				table2.row();
				table2.add().minHeight(buttonheight*0.1f);
				table2.row();
				table2.add(new Label("ein Schiff zu platzieren.",headingStyle)).colspan(5);
				table2.row();
				table2.add().minHeight(buttonheight*0.1f);
				table2.row();
				table2.add(new Label("Für große Schiffe ziehe es in",headingStyle)).colspan(5);
				table2.row();
				table2.add().minHeight(buttonheight*0.1f);
				table2.row();
				table2.add(new Label("die gewünschte Richtung",headingStyle)).colspan(5);
				CameraController.changeStateTo(3, true,false);
			}
		});
		
		buttonClearShips = new TextButton("löschen",textButtonStyle);
		buttonClearShips.pad((int)(w*0.02f));
		buttonClearShips.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
					player.getGame().getFirstFieldPlayer().resetField();//Spielfeld zuruecksetzen
					controller.setShipPlaceHelper(schiffsEinstellung);
			}
		});
		
		buttonStart = new TextButton("Start",textButtonStyle);
		buttonStart.pad((int)(w*0.02f));
		buttonStart.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {					
				Gdx.input.setInputProcessor(gestureDetector);
				if(player.getGame().getSecondFieldEnemy().isAllShipsSet() || primaryBTGame || secondaryBTGame){
					//Setzen der Schiffe und Starten.
					if(player.getGame().getFirstFieldPlayer().isAllShipsSetManual()){
						//Wenn schiffe manuell gesetzt sind muessen diese aufs Feld plaziert werden.
						player.getGame().getFirstFieldPlayer().setManualNewShipplacement(controller.getPlacedShipUnits());
					}else if(!player.getGame().getFirstFieldPlayer().isAllShipsSet()){
						//Setzen der Schiffe wenn diese noch nicht plaziert sind.
						player.getGame().getFirstFieldPlayer().generateNewShipplacement(schiffsEinstellung);
					}

					//Resetten vom ShipPlaceHelper im CameraController
					ArrayList<Integer> tmpEmptyShipList = new ArrayList<Integer>(4);
					tmpEmptyShipList.add(0);
					tmpEmptyShipList.add(0);
					tmpEmptyShipList.add(0);
					tmpEmptyShipList.add(0);
					controller.setShipPlaceHelper(tmpEmptyShipList);
					
					if(primaryBTGame||secondaryBTGame){
						do{
						//Felduebertragen erneut versuchen bei Fehlschlag.
						try {
							player.getGame().getFirstFieldPlayer().sendFieldUnitsWithBluetooth();
							Thread.sleep(Game.FIVEHUNDRED_MS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						}while(!player.getGame().getFirstFieldPlayer().getFeldUebertragenAntwort());
						CameraController.changeStateTo(2, false,false);
					}else{//Singleplayer
						CameraController.changeStateTo(2, false,false);
					}
					player.getGame().start();
				}
			}
		});
		
		buttonRestart = new TextButton("Neustart",textButtonStyle);
		buttonRestart.pad((int)(w*0.02f));
		buttonRestart.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
//				loadPlayerData();				
				parentScreen.create(true);
			}
		});
		
		// Hinzufuegen vom Elementen zur Tabelle bei Start
		table.add(heading).colspan(1);
		table.row();
		table.add(buttonStart).minWidth(buttonwidth).minHeight(buttonheight);
		table.row();
		table.add().minHeight(buttonwidth*0.02f);
		table.row();
		table.add(buttonGenerateShips).minWidth(buttonwidth).minHeight(buttonheight);
		table.row();
		table.add().minHeight(buttonwidth*0.02f);
		table.row();
		table.add(buttonSelfPlaceShips).minWidth(buttonwidth).minHeight(buttonheight);

		stage.addActor(table);
		stage.addActor(table2);
		
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
			// TODO Erweitern
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

		// Dem Renderer die Kamera uebergeben.
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
		// Weiter Ausserhalb
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
			// auf die cam position/koordinaten.
			sr.setProjectionMatrix(camera.combined);
			sr.setColor(Color.GRAY);
			String objektebene = "";
			if (state.get(5)) {//Arrow when Player Turn
				objektebene = "GameField";
			}
			if (state.get(6)) {
				objektebene = "GameFieldPlayer";
			}
			if (state.get(7)||state.get(2)&&!player.getGame().isEnd()) {
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
			
			//Pfeil wenn Spieler am Zug ist.
			if(player.getGame().getGamersTurn()==0){
				MapObject object = map.getLayers().get("GameField").getObjects().get("arrow");
				Polygon pfeil = ((PolygonMapObject) object).getPolygon();
				float[] vert = pfeil.getTransformedVertices();
				sr.begin(ShapeType.Line);
				sr.polygon(vert);
				sr.end();
			}
		}
		//Menue
		if(state.get(1)||state.get(3)){
			//Wenn Spiel zu ende ist soll State 1 und Text mit Btton aktiv werden.
			if(state.get(8)){
				//Tabelle f�r Neustart vorbereiten.
				table.clear();
				table.add(heading).colspan(1);
				table.row();
				table.add(buttonRestart).minWidth(buttonwidth).minHeight(buttonheight);
				table.row();
				table.add().minHeight(buttonwidth*0.02f);
				table.row();
				table.add(new Label("Ende",headingStyle)).colspan(1);
				table.row();
				table.add().minHeight(buttonwidth*0.02f);
				table.row();
				table.add(new Label("Du hast "+((player.getGame().hasPlayerWon())?"gewonnen!":"leider verloren."),headingStyle)).colspan(1);
				table.row();
				table.add().minHeight(buttonwidth*0.02f);
				table.row();
				table.add(new Label("Druecke Neustart fuer ein neues Spiel.",headingStyle)).colspan(1);
				table2.clear();
				Gdx.input.setInputProcessor(inputMultiplexer);
				CameraController.changeStateTo(8, false,true);
			}
			stage.act(delta);
			stage.draw();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		dispose();

	}

	@Override
	public void pause() {
		// TODO Save game fields
		// try {
		// Player.savePlayer(player);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	@Override
	public void resume() {
		//TODO load game fields
	}

	@Override
	public void dispose() {
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

	}
}
