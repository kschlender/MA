package com.ma.schiffeversenken;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.ma.schiffeversenken.android.AndroidLauncher;
import com.ma.schiffeversenken.android.model.Field;
import com.ma.schiffeversenken.android.view.Settings;
import com.ma.schiffeversenken.android.view.StartScreen;

/**
 * Klasse Handelt die Game Screens.
 * 
 * @author Klaus Schlender
 */
public class MyGdxGameField extends Game {
	
	private AndroidLauncher androidLauncher;
	private boolean primaryBTGame, secondaryBTGame,wasBluetoothEnabledBevore;

	public MyGdxGameField(boolean primaryBTGame, boolean secondaryBTGame,AndroidLauncher a){
		this.primaryBTGame = primaryBTGame;
		this.secondaryBTGame = secondaryBTGame;
		this.androidLauncher=a;
	}
	
	/**
	 * create initialisiert das Grundgeruest fuer das Zeichnen mit OpenGL ES 2.0
	 * es wird nur einmal ausgefuehrt
	 */
	@Override
	public void create() {
		//Wenn spiel erstellt wird wollen wir den Screen setzen
		setScreen(new GameFieldScreen(false,this,primaryBTGame, secondaryBTGame));
	}
	
	/**
	 * Wird beim Neustart aufgerufen.
	 * @param newGame true, wenn neues spiel
	 */
	public void create(boolean restart) {
		//Wenn spiel erstellt wird wollen wir den Screen setzen
		setScreen(new GameFieldScreen(restart,this,primaryBTGame, secondaryBTGame));
	}

	@Override
	public void dispose() {
		//Disable bluetooth when multiplayer
		if(primaryBTGame||secondaryBTGame){
			Preferences pref = Gdx.app.getPreferences("Main_Preferences");
			boolean wasBluetoothEnabledBevore = Boolean.parseBoolean(pref.getString(StartScreen.SETTINGS_BLUETOOTHWASACTIVATEDBEVORE));
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
			if (mBluetoothAdapter.isEnabled() && !wasBluetoothEnabledBevore) {
			    mBluetoothAdapter.disable(); 
			} 
		}
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
		// TODO SPEICHERN
	}

	@Override
	public void resume() {
		super.resume();
		// TODO LADEN

	}
	
	public AndroidLauncher getAndroidLauncher(){
		return this.androidLauncher;
	}
	
}