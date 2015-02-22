package com.ma.schiffeversenken.android.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.ma.schiffeversenken.GameFieldScreen;
import com.ma.schiffeversenken.android.controller.Game;
import com.ma.schiffeversenken.android.controller.KI;
import com.ma.schiffeversenken.android.view.Settings;
import com.ma.schiffeversenken.android.view.StartScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;


/**
 * Der Player traegt die Spielelemente und das Spielgeschehen.
 * 
 * @author Klaus Schlender
 */
public class Player {
	private static Game game;
	private static Field firstField;
	private static Field secondField;
	TiledMap map;
	private int gameMode;
	private int theKiLevel;
	private static ArrayList<Integer> gameSettings;

	/**
	 * TODO Make Animation
	 * @param tileSet
	 * @param m
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Player(TiledMapTileSet tileSet, TiledMap m, boolean primaryBTGame, boolean secondaryBTGame)
			throws ClassNotFoundException, IOException {
		super();
		Preferences pref = Gdx.app.getPreferences("Main_Preferences");
		Field.soundOff = Boolean.parseBoolean(pref.getString(Settings.SETTINGS_SOUNDOFF));
		Field.vibrationOff = Boolean.parseBoolean(pref.getString(Settings.SETTINGS_VIBRATIONOFF));
		Field.cheatsOn = Boolean.parseBoolean(pref.getString(Settings.SETTINGS_CHEATSOFF));
		if(pref.contains(StartScreen.SETTINGS_BUTTONWIDTH)){
		GameFieldScreen.buttonwidth = Integer.parseInt(pref.getString(StartScreen.SETTINGS_BUTTONWIDTH));
		GameFieldScreen.buttonheight = Integer.parseInt(pref.getString(StartScreen.SETTINGS_BUTTONHEIGHT));
		}
		map = m;
		firstField = new Field(0, tileSet, (TiledMapTileLayer) map.getLayers()
				.get("0"));
		secondField = new Field(1, tileSet, (TiledMapTileLayer) map.getLayers()
				.get("0"));
		
		//Load Preferences on Ki Difficulty
		String kiLevel = pref.getString("ki");
		Gdx.app.log(GameFieldScreen.TITLE, "Kilevel: "+kiLevel);
		theKiLevel=-1;
		if (kiLevel.equals(KI.KI_SIMPLE)){
			theKiLevel=1;
		}else if (kiLevel.equals(KI.KI_NORMAL)){
			theKiLevel=2;
		}else if (kiLevel.equals(KI.KI_DIFFICULT)){
			theKiLevel=3;
		}

		gameMode = 0;
		if(primaryBTGame || secondaryBTGame) gameMode = 1;
		this.game = new Game(gameMode, firstField, secondField, primaryBTGame, secondaryBTGame, false, theKiLevel);

		if (Gdx.files.isLocalStorageAvailable()
				&& Gdx.files.local("preferences.bin").exists()) {
			FileHandle file = Gdx.files.local("preferences.bin");
			gameSettings = (ArrayList<Integer>) deserialize(file.readBytes());
			// TODO Etwas mit den settings tun
		} else {
			System.out
					.println("GamePreferences does not exist. Creating new Standard GamePreferences ...");
			gameSettings = new ArrayList<Integer>();
			gameSettings.add(1);// Z
			gameSettings.add(2);// S
			gameSettings.add(3);// U
			gameSettings.add(4);// K
			gameSettings.add(1);// KI
		}
	}
	
	/**
	 * TODO
	 * @param tileSet
	 * @param m
	 * @param tmpgame
	 * @param tmpfirstField
	 * @param tmpsecondField
	 * @param tmpgameSettings
	 */
	public Player(TiledMapTileSet tileSet, TiledMap m, Game tmpgame,
			Field tmpfirstField, Field tmpsecondField,
			ArrayList<Integer> tmpgameSettings) {
		super();
		map = m;
		firstField = tmpfirstField;
		secondField = tmpsecondField;
		// TODO Support moere gameModes...
		this.game = tmpgame;
		gameSettings = tmpgameSettings;
		// TODO Etwas mit den settings tun

	}

	public void update(OrthographicCamera camera) {

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			// camera.translate(0, 10);
		} else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			// camera.translate(-10, 0);
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			// camera.translate(0, -10);
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			// camera.translate(+10, 0);
		} else if (Gdx.input.isKeyPressed(Keys.PLUS)) {
			if (camera.zoom > 0.1f)
				camera.zoom = camera.zoom - 0.1f;
		} else if (Gdx.input.isKeyPressed(Keys.MINUS)) {
			if (camera.zoom < 2.0f)
				camera.zoom = camera.zoom + 0.1f;
		}

	}

	/**
	 * Callback Methode fuer das Object Player
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		// Maybe serialice object ;)

	}

	public Field getFirstField() {
		return firstField;
	}

	public void setFirstField(Field firstField) {
		this.firstField = firstField;
	}

	public Field getSecondField() {
		return secondField;
	}

	public void setSecondField(Field secondField) {
		this.secondField = secondField;
	}

	/**
	 * Methode zum Zeichnen der Szene
	 * 
	 * @param batch
	 *            SpriteBatch wird fuers Zeichnen uebergeben.
	 */
	// @Deprecated
	public void draw(Batch batch) {
		game.draw(batch);
	}

	public void animatedTiles() {
		// Animatad Tiles
		// FrameArray fuer zwei verschiedene Bilder
		Array<StaticTiledMapTile> frameTiles = new Array<StaticTiledMapTile>(3);

		// Iterieren und holen der Animierten tiles fuer das FrameArray
		Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet("ships")
				.iterator();
		while (tiles.hasNext()) {
			TiledMapTile tile = tiles.next();
			if (tile.getProperties().containsKey("gunattack")
					&& tile.getProperties().get("gunattack", String.class)
							.equals("1"))
				frameTiles.add((StaticTiledMapTile) tile);
		}

		// Erstellen der Animierten Tile
		AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(1 / 3f,
				frameTiles);

		// 3=attack,2=ships,1=water, 0=land
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("3");
		//
		//
		//

		// iteration ueber das TileGrid
		for (int x = 0; x < layer.getHeight(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					// Wenn das die gesuchte zelle ist.
					if (cell.getTile().getProperties().containsKey("gunattack")
							&& cell.getTile().getProperties()
									.get("gunattack", String.class).equals("1")) {
						// Animierte Tile der cell zuordnen.
						cell.setTile(animatedTile);
					}
				}
			}
		}

	}

	public Game getGame() {
		return game;
	}

	public static void savePlayer(Player player) throws IOException {
		if (Gdx.files.isLocalStorageAvailable()) {

			
			FileHandle file = Gdx.files.local("player_firstField.bin");
			try {
				file.writeBytes(serialize(firstField), false);
				file = Gdx.files.local("player_secondField.bin");
				file.writeBytes(serialize(secondField), false);
				// TiledMap muss manuell geladen werden.
				file = Gdx.files.local("player_gameSettings.bin");
				file.writeBytes(serialize(gameSettings), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Player readPlayer(TiledMapTileSet tileSet, TiledMap m)
			throws IOException, ClassNotFoundException {
		Player player = null;

		Field tmpfirstField;
		Field tmpsecondField;
		ArrayList<Integer> tmpgameSettings;

		if (Gdx.files.isLocalStorageAvailable()) {

			FileHandle file = Gdx.files.local("player_firstField.bin");
			tmpfirstField = (Field) deserialize(file.readBytes());
			file = Gdx.files.local("player_secondField.bin");
			tmpsecondField = (Field) deserialize(file.readBytes());
			file = Gdx.files.local("player_gameSettings.bin");
			tmpgameSettings = (ArrayList<Integer>) deserialize(file.readBytes());

			player = new Player(tileSet, m, new Game(0, tmpfirstField, tmpsecondField, false, false, true, 1), tmpfirstField,
					tmpsecondField, tmpgameSettings);
		}
		return player;
	}

	/**
	 * Methode dient der serialisierung eines Objectes zu einem ByteArray.
	 * 
	 * @param obj
	 *            Das zu serialisierende Object.
	 * @return ByteArray der das serialiserte Object haelt.
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	/**
	 * Methode dient der deserialisierung eines Objectes aus einem ByteArray.
	 * 
	 * @param bytes
	 *            ByteArray eines Objectes.
	 * @return deserialisiertes Objekt wird zurueckgeliefert.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
	
	/**
	 * Liest ein String Objekt ein und schreibt diesen in Base64.
	 * @param s String dieser haelt ein Objekt
	 * @return o Objekt wird zurueckgeliefert
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	   public static Object fromString( String s ) throws IOException ,
	                                                       ClassNotFoundException {
	        byte [] data = Base64Coder.decode( s );
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(  data ) );
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
	   }

	   /**
	    * Liest ein Base64 Objekt und schreibt dieses in einen String.
	    * @param o Objekt welches in String geschrieben werden soll.
	    * @return String der resultierende String
	    */
	   public static String toString( Serializable o ) {
		   try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
				oos = new ObjectOutputStream( baos );
	        oos.writeObject(o);
	        oos.close();
	        return new String( Base64Coder.encode( baos.toByteArray() ) );
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   return "";
	    }
		
	public int getGameMode(){
		return gameMode;
	}
	
	public int getKiLevel(){
		return theKiLevel;
	}

	public void setNewGame(Game game2) {
		 try
	      {
	       game.sleep( 500 );
	       game.interrupt();
	      }
	      catch ( InterruptedException e )
	      {
	       System.out.println( "Unterbrechung in sleep()" );
	       this.game=game2;
	      }
		
	}
}
