package com.ma.schiffeversenken.android.controller;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ma.schiffeversenken.android.model.*;

/**
 * Gesamte Spiellogik
 * - Beinhaltet alle Spieldaten
 * - Kommuniziert mit beiden Spielern
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class Game implements Runnable {

	/**Einzelspielermodus*/
	private final static int GAME_MODE_SINGLE_PLAYER = 0;
	/**Mehrspielermodus*/
	private final static int GAME_MODE_MULTI_PLAYER = 1;
	/**500 millisekunden*/
	private final static int FIVEHUNDRED_MS = 500;
	/**1000 millisekunden*/
	private final static int THOUSAND_MS = 1000;
	/**50 millisekunden*/
	private final static int FIFTY_MS = 50;
	/**Erster Spieler*/
	private final static int FIRST_GAMER = 0;
	/**Zweiter Spieler*/
	private final static int SECOND_GAMER = 1;
	/**Einzelspieler (=0) oder Mehrspielermodus (=1)*/
	private int gameMode;
	/**Spielfeld des Spielerstellers*/
	private Field firstFieldPlayer;
	/**Spielfeld des Gastspielers*/
	private Field secondFieldEnemy;
	/**Wird auf true gesetzt, wenn Spieler 1 eine Eingabe getaetigt hat*/
	private boolean firstGamerAction;
	/**Wird auf true gesetzt, wenn Spieler 2 eine Eingabe getaetigt hat*/
	private boolean secondGamerAction;
	/**Die Feld-ID, die Spieler 1 attackiert*/
	private int firstGamerAttackID;
	/**Die Feld-ID, die Spieler 2 attackiert*/
	private int secondGamerAttackID;
	/**Kuenstlicher Computer Gegner*/
	private KI ki;
	private BluetoothConnectedThread btConnectedThread;
	private boolean primaryBTGame;
	private boolean secondaryBTGame;
	private boolean returnAttackHit;
	private boolean returnShipDestroyed;
	private boolean returnValuesAvailable;
	int gamersTurn;
	
	//Nur zu Testzwecken - Maik
	public boolean getroffen = false;
	private boolean end;
	
	/**
	 * Erstellt ein neues Game Objekt
	 * @param gameMode 0=Einzelspieler; 1=Mehrspieler
	 * @param firstField Spielfeld des 1. Spielers
	 * @param secondField Spieldfeld des 2. Spielers
	 * @param loadedGame Boolean ob das Spiel geladen wurde
	 */
	public Game(int gameMode, Field firstFieldPlayer, Field secondField, boolean primaryBTGame, boolean secondaryBTGame,boolean loadedGame){
		this.gameMode = gameMode;
		this.firstFieldPlayer = firstFieldPlayer;
		this.secondFieldEnemy = secondField;
		this.primaryBTGame = primaryBTGame;
		this.secondaryBTGame = secondaryBTGame;
		this.gamersTurn = 0;
		this.end=true;
		
		resetActionVariables();
		
		if(gameMode == GAME_MODE_SINGLE_PLAYER){
			//Wenn GameMode == 0 == Einzelspieler, dann KI erstellen
			//Field kiField = new Field(1);
			ki = new KI(secondField, firstFieldPlayer, false,loadedGame);
		}
	}
	
	public void setConnectedThread(BluetoothConnectedThread btConnectedThread){
		this.btConnectedThread = btConnectedThread;
	}
	
	/**
	 * Wird aufgerufen, wenn ein Touch Event auftritt
	 * Holt die ID, des angegriffenen Feldelements und greift dies an
	 * @param xPos x-Position des Touch-Events
	 * @param yPos y-Position des Touch-Events
	 */
	public void touchEvent(float xPos, float yPos){
		FieldUnit fieldUnit = null;
		
		if(gamersTurn==0){//Wenn Spieler am Zug greife Gegnerfeld an
			fieldUnit = secondFieldEnemy.getElementByXPosYPos(xPos, yPos);
			if(fieldUnit != null) firstGamerAttack(fieldUnit.getID());
		}
		else{
			fieldUnit = firstFieldPlayer.getElementByXPosYPos(xPos, yPos);
			if(fieldUnit != null) secondGamerAttack(fieldUnit.getID());
		}
	}
	
	
	
	/**
	 * Die Methode wird von der GUI aufgerufen,
	 * sobald Spieler 1 auf ein Feld getippt
	 * hat, das er attackieren moechte
	 * @param id ID des Feldes, das angegriffen wird
	 */
	public void firstGamerAttack(int id){
		this.firstGamerAttackID = id;
		this.firstGamerAction = true;
	}
	
	/**
	 * Die Methode wird von der GUI aufgerufen,
	 * sobald Spieler 2 auf ein Feld getippt
	 * hat, das er attackieren moechte
	 * @param id ID des Feldes, das angegriffen wird 
	 */
	public void secondGamerAttack(int id){
		this.secondGamerAttackID = id;
		this.secondGamerAction = true;
	}
	
	/**
	 * Wenn alle FeldElemente eines Schiffes zerstoert wurden,
	 * wird dieses Schiff als zerstoert markiert
	 * @param fe Feld, auf dem das Schiff steht, das geprueft wird
	 */
	private void destroyShip(FieldUnit fe){
		boolean destroyed = true;
		
		for(FieldUnit f : fe.getPlacedShip().getLocation()){
			if(!f.getAttacked()){
				destroyed = false;
			}
		}
		
		fe.getPlacedShip().setDestroyed(destroyed);
	}
	
	/**
	 * Startet das Game. Wird aufgerufen, wenn das Game Thread gestartet wird
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException{
		end = false;
		boolean hitShip = false;
		
		do{
			if(gamersTurn==0){
				//Auf Eingabe von Benutzer warten
				do{
					while(!firstGamerAction){
						Thread.sleep(FIVEHUNDRED_MS);
					}
					hitShip = gamerAction(firstGamerAttackID, gamersTurn);
				}while(hitShip);
				gamersTurn++;
			}
			else{
				if(gameMode == GAME_MODE_SINGLE_PLAYER){
					//Wenn GameMode == 0 == Einzelspieler, dann KI attackieren lassen
					do{
						/*
						 * Die Schleife wird solange durchlaufen,
						 * bis der Spieler ins Leere trifft
						 */
						Thread.sleep(THOUSAND_MS);
						hitShip = gamerAction(ki.attack(), gamersTurn);
					}while(hitShip);
				}
				else{
					//Auf Eingabe von Benutzer warten
					do{
						/*
						 * Die Schleife wird solange durchlaufen,
						 * bis der Spieler ins Leere trifft
						 */
						while(!secondGamerAction){
							Thread.sleep(FIVEHUNDRED_MS);
						}
						hitShip = gamerAction(secondGamerAttackID, gamersTurn);
					}while(hitShip);
				}
				gamersTurn = 0;
			}
			
			if(hasSomebodyWon() != 0){
				end = true;
			}
		}while(!end);
	}
	
	public void setReturnValues(boolean returnAttackHit, boolean returnShipDestroyed){
		this.returnAttackHit = returnAttackHit;
		this.returnShipDestroyed = returnShipDestroyed;
	}
	
	/**
	 * 
	 * @param id ID des Feldes, das angegriffen wird
	 * @param gamer 0= Erster Spieler; 1= zweiter Spieler
	 * @return Boolean ob ein gegnerisches Schiff getroffen wurde
	 */
	private boolean gamerAction(int id, int gamer){
		boolean ret = false;
		FieldUnit fe;
		boolean attackHit = false;
		boolean shipDestroyed = false;
		boolean done = false;
		
		if(gamer == 0){
			fe = secondFieldEnemy.getElementByID(id);
		}
		else{
			fe = firstFieldPlayer.getElementByID(id);
		}
		
		fe.setAttacked(true); //FeldElement als attackiert markieren
		//TODO Event das Angriff zeichnet
		
		if(primaryBTGame || secondaryBTGame){
			byte[] attackString = (new String("_ATTACK_" + Integer.toString(id))).getBytes();
			
			if(primaryBTGame && gamer == FIRST_GAMER){
				btConnectedThread.write(attackString);	
			}
			else if(secondaryBTGame && gamer == SECOND_GAMER){
				btConnectedThread.write(attackString);
			}
			
			while(!returnValuesAvailable){
				try {
					Thread.sleep(FIFTY_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			ret = returnAttackHit;
			destroyShip(fe);//Ob Schiff komplett zerstört
			shipDestroyed = returnShipDestroyed;
			attackHit = returnAttackHit;
			
			done = true;
		}
		
		if(!done){
			if(fe.getOccupied()){
				//Wenn das Feld belegt war
				ret = true;
				destroyShip(fe);
				shipDestroyed = fe.getPlacedShip().isDestroyed();
				attackHit = true;
			}
			
			byte[] returnString = (new String("_RETURN_" + Boolean.toString(attackHit) + "_" + Boolean.toString(shipDestroyed))).getBytes();
			
			if(secondaryBTGame && gamer == 0){
				btConnectedThread.write(returnString);
			}
			else if(primaryBTGame && gamer == 1){
				btConnectedThread.write(returnString);
			}
		}
		
		if(gamer == SECOND_GAMER && gameMode == GAME_MODE_SINGLE_PLAYER){
			/*
			 * Wenn die KI attackiert hat werden zwei Variablen gesetzt,
			 * damit die KI weiss ob ein Schiff getroffen und/oder zerstoert wurden
			 */
			ki.updateHistory(id, attackHit, shipDestroyed);
		}
		
		resetActionVariables();
		
		getroffen = ret; //Nur zu Testzwecken - Maik
		
		return ret;
	}
	
	/**
	 * Variablen nach Angriff zuruecksetzen
	 */
	private void resetActionVariables(){
		firstGamerAction = false;
		firstGamerAttackID = 0;
		secondGamerAction = false;
		secondGamerAttackID = 0;
		returnAttackHit = false;
		returnShipDestroyed = false;
		returnValuesAvailable = false;
	}
	
	/**
	 * Ueberpruefen ob ein Spieler gewonnen hat
	 * @return
	 * - Gibt 1 zurueck, wenn Spieler 1 gewonnen hat
	 * - Gibt 2 zurueck, wenn Spieler 2 gewonnen hat
	 * - Gibt 0 zurueck, wenn niemand gewonnen hat 
	 */
	private int hasSomebodyWon(){
		int ret = 1;
		
		for(Ship ship : secondFieldEnemy.getShips()){
			if(!ship.isDestroyed()){
				ret = 2;
			}
		}
		
		if(ret == 2){
			for(Ship ship : firstFieldPlayer.getShips()){
				if(!ship.isDestroyed()){
					ret = 0;
				}
			}
		}
		
		return ret;
	}

	/**
	 * Startet das Game Thread
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Zeichnet die beiden Spielfelder mit den jeweiligen Zuständen
	 * @param batch SpriteBatch worauf gezeichnet wird.
	 * @param atlas Textures
	 */
//	@Deprecated
	public void draw(Batch batch) {
		// TODO Auto-generated method stub
		firstFieldPlayer.draw(batch);
		secondFieldEnemy.draw(batch);
	}

	public Field getFirstFieldPlayer() {
		return firstFieldPlayer;
	}

	public Field getSecondFieldEnemy() {
		return secondFieldEnemy;
	}
	
	public boolean isEnd(){
		return end;
	}
	
}
