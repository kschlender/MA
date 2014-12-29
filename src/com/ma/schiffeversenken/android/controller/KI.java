package com.ma.schiffeversenken.android.controller;

import java.util.Random;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ma.schiffeversenken.android.model.*;

/**
 * Spiellogik des Computer Gegners
 * @author Maik Steinborn
 */
public class KI {

	/**Rechte Kante*/
	public static final int EDGE_RIGHT = 0;
	/**Obere Kante*/
	public static final int EDGE_ABOVE = 1;
	/**Linke Kante*/
	public static final int EDGE_LEFT = 2;
	/**Untere Kante*/
	public static final int EDGE_BELOW = 3;
	/**Ausrichtung des Schiffs nach rechts*/
	public static final int SHIP_ORIENTATION_RIGHT = 0;
	/**Ausrichtung des Schiffs nach oben*/
	public static final int SHIP_ORIENTATION_ABOVE = 1;
	/**Ausrichtung des Schiffs nach links*/
	public static final int SHIP_ORIENTATION_LEFT = 2;
	/**Ausrichtung des Schiffs nach unten*/
	public static final int SHIP_ORIENTATION_BELOW = 3;
	/**Anzahl der Angriffe, die in der History gespeichert werden*/
	public static final int NUMBER_OF_HISTORY = 4;
	/**Groesse des Kreuzer*/
	public static final int CRUISER_SIZE = 1;
	/**Groesse des Uboot*/
	public static final int SUBMARINE_SIZE = 2;
	/**Groesse des Zerstoerer*/
	public static final int DESTROYER_SIZE = 3;
	/**Groesse des Schlachtschiff*/
	public static final int BATTLESHIP_SIZE = 4;
	/**Anzahl der Feldelemente auf dem Spielfeld*/
	public static final int FIELD_SIZE = 100;
	/**Anzahl der moeglichen Ausrichtungen eines Schiffs*/
	public static final int NUMBER_OF_ORIENTATIOS_MINUS_ONE = 3;
	/**Das Spielfeld der KI*/
	private Field myField;
	/**Das gegnerische Spielfeld*/
	private Field enemiesField;
	/**Die ID's der letzten 4 Angriffe*/
	private int[] idHistory = new int[NUMBER_OF_HISTORY];
	/**True oder False ob bei den letzten 4 Angriffen ein Schiff getroffen wurde*/
	private boolean[] hitHistory = new boolean[NUMBER_OF_HISTORY];
	/**True oder False ob bei den letzten 4 Angriffen ein Schiff zerstoert wurde*/
	private boolean[] shipDestroyedHistory = new boolean[NUMBER_OF_HISTORY];
	KIStrategy kiStrategy;
	
	/**
	 * Erstellt ein neues KI Objekt
	 * @param myField Spielfeld der KI
	 * @param enemiesField Spielfeld des Gegners
	 * @param loadedGame Boolean ob das Spiel geladen wurde
	 */
	public KI(Field myField, Field enemiesField, boolean test,boolean loadedGame){
		this.myField = myField;
		this.enemiesField = enemiesField;
		this.kiStrategy = new NormalStrategy(this);
		if(!loadedGame){//myField hat schon Schiffe
			setShips(createShips(3, 4, 2, 1), test);
		}
		initHistory();
	}
	
	/**
	 * History updaten
	 * Die History speichert die letzten 4 Angriffe
	 * @param id Die id der letzten Angriffe
	 * @param hit True oder False ob bei den letzten Angriffen ein Schiff getroffen wurde
	 * @param shipDestroyed True oder False ob bei den letzten Angriffen ein Schiff zerstoert wurde
	 */
	public void updateHistory(int id, boolean hit, boolean shipDestroyed){
		for(int i=3; i>0; i--){
			idHistory[i] = idHistory[i-1];
			hitHistory[i] = hitHistory[i-1];
			shipDestroyedHistory[i] = shipDestroyedHistory[i-1];
		}
		
		idHistory[0] = id;
		hitHistory[0] = hit;
		shipDestroyedHistory[0] = shipDestroyed;
	}
	
	/**
	 * History initialisieren
	 */
	private void initHistory(){
		for(int i = 0; i<4; i++){
			idHistory[i] = 0;
			hitHistory[i] = false;
			shipDestroyedHistory[i] = false;
		}
	}
	
	/**
	 * Benoetigte Schiffe erstellen
	 * @return Erstellt Schiffe
	 */
	public static Ship[] createShips( int numberOfCruiser,int numberOfSubmarines,
			int numberOfDestroyer, int numberOfBattleShips){
		Ship[] ships = new Ship[numberOfCruiser + numberOfSubmarines + numberOfDestroyer + numberOfBattleShips];
		
		int i,j,k,l;
		
		for(i=0; i<numberOfSubmarines; i++){
			ships[i] = new Ship("Uboot", SUBMARINE_SIZE);
		}
		
		for(j=0; j<numberOfCruiser; j++){
			ships[j+numberOfSubmarines] = new Ship("Kreuzer", CRUISER_SIZE);
		}
		
		for(k=0; k<numberOfDestroyer; k++){
			ships[k+numberOfSubmarines+numberOfCruiser] = new Ship("Zerstoerer", DESTROYER_SIZE);
		}	
	
		for(l=0; l<numberOfBattleShips; l++){
			ships[l+numberOfSubmarines+numberOfCruiser+numberOfDestroyer] = new Ship("Schlachtschiff", BATTLESHIP_SIZE);
		}
		
		/*Ship[] ships = new Ship[]{new Ship("Uboot", SUBMARINE_SIZE),
				new Ship("Uboot", SUBMARINE_SIZE),
				new Ship("Uboot", SUBMARINE_SIZE),
				new Ship("Kreuzer", CRUISER_SIZE),
				new Ship("Kreuzer", CRUISER_SIZE),
				new Ship("Kreuzer", CRUISER_SIZE),
				new Ship("Kreuzer", CRUISER_SIZE),
				new Ship("Zerstoerer", DESTROYER_SIZE),
				new Ship("Zerstoerer", DESTROYER_SIZE),
				new Ship("Schlachtschiff", BATTLESHIP_SIZE)
				};*/
		
		return ships;
	}
	
	public ShipPlacement sp = new ShipPlacement(); //Nur zu Testzwecken als globale Variable
	
	/**
	 * Schiffe auf dem Spielfeld der KI platzieren
	 * @param ships Die Schiffe, die platziert werden
	 */
	private void setShips(Ship[] ships, boolean test){
		sp = new ShipPlacement();
		sp.placeShips(myField, ships, test);
	}
	
	/**
	 * Den Gegner attackieren
	 * @return ID des Feldes, das attackiert werden soll
	 */
	public int attack(){
		//return kiStrategy.attack();
		
		Random random = new Random();
		int nextAttackID = 0;
		int idForContinueLastAttack = getIDForContinueLastAttack();
		
		if(idForContinueLastAttack != 0){
			//Den letzten Angriff fortfuehren
			nextAttackID = idForContinueLastAttack;
		}
		else{
			//Angriff auf eine neue zufaellige FeldID starten
			do{
				//Zufaellige Zahl erstellen
				nextAttackID = random.nextInt(99) + 1;
			}while(getEnemiesField().getElementByID(nextAttackID).getAttacked());
		}
		
		//Ausgewaehltes FeldElement attackieren
		return nextAttackID;
	}
	
	private boolean idHistoryContains(int id){
		boolean ret = false;
		
		for(int i=0; i<4; i++){
			if(idHistory[i] == id) ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Berechnet die ID, die von der KI angegriffen werden soll,
	 * wenn die KI in der vorherigen Spielrunde ein gegnerisches Schiff getroffen hat
	 * @return Die ID des Feldelements, das angegriffen werden soll
	 */
	int getIDForContinueLastAttack(){
		int ret = 0;
		boolean jump = false;
		//int lastHistory = 1;
		
		for(int i=0; i<4; i++){
			if(!jump && ret == 0){
				if(hitHistory[i]){
					if(shipDestroyedHistory[i]){
						jump = true;
					}
					else{
						//if(i == 3) lastHistory = -1;
						
						if((idHistory[i] + 1) <= FIELD_SIZE){
							//if(getEnemiesField().getElementByID(idHistory[i] + 1).getAttacked()){
							//if((idHistory[i] + 1) == idHistory[i+lastHistory]){
							if(idHistoryContains(idHistory[i] + 1)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != EDGE_LEFT && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != EDGE_LEFT && !getEnemiesField().getElementByID(idHistory[i] - 1).getAttacked()){
									ret = idHistory[i] - 1;
								}
							}
						}
						if((idHistory[i] - 1) > 0){
							//if(getEnemiesField().getElementByID(idHistory[i] - 1).getAttacked()){
							//if((idHistory[i] - 1) == idHistory[i+lastHistory]){
							if(idHistoryContains(idHistory[i] - 1)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != EDGE_RIGHT && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != EDGE_RIGHT && !getEnemiesField().getElementByID(idHistory[i] + 1).getAttacked()){
									ret = idHistory[i] + 1;
								}
							}
						}
						if((idHistory[i] + 10) <= FIELD_SIZE){
							//if(getEnemiesField().getElementByID(idHistory[i] + 10).getAttacked()){
							//if((idHistory[i] + 10) == idHistory[i+lastHistory]){
							if(idHistoryContains(idHistory[i] + 10)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != EDGE_ABOVE && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != EDGE_ABOVE && !getEnemiesField().getElementByID(idHistory[i] - 10).getAttacked()){
									ret = idHistory[i] - 10;
								}
							}
						}
						if((idHistory[i] - 10) > 0){
							//if(getEnemiesField().getElementByID(idHistory[i] - 10).getAttacked()){
							//if((idHistory[i] - 10) == idHistory[i+lastHistory]){
							if(idHistoryContains(idHistory[i] - 10)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != EDGE_BELOW && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != EDGE_BELOW && !getEnemiesField().getElementByID(idHistory[i] + 10).getAttacked()){
									ret = idHistory[i] + 10;
								}
							}
						}
					}
				}
			}
		}
		
		ret = getIDAfterOneAttackToShip(ret);
		
		return ret;
	}

	/**
	 * Wenn das gegnerische Schiff getroffen und erst einmal angegriffen wurde,
	 * wird durch Zufall bestimmt in welche Richtung der nächste Angriff geht
	 * @param ret Die aktuelle ID, die angegriffen wird
	 * @return Die neue ID, die angegriffen werden soll
	 */
	private int getIDAfterOneAttackToShip(int ret) {
		if((ret == 0) && hitHistory[0]){
			Random random = new Random();
			int counter = 0;
			do{
				int randomInt = random.nextInt(NUMBER_OF_ORIENTATIOS_MINUS_ONE);
				
				if(getEnemiesField().getElementByID(idHistory[0]).getEdge(1) != randomInt && getEnemiesField().getElementByID(idHistory[0]).getEdge(2) != randomInt){
					if(randomInt == SHIP_ORIENTATION_ABOVE){
						if(!getEnemiesField().getElementByID(idHistory[0] - 10).getAttacked()){
							ret = idHistory[0] - 10;
						}
					}
					if(randomInt == SHIP_ORIENTATION_BELOW){
						if(!getEnemiesField().getElementByID(idHistory[0] + 10).getAttacked()){
							ret = idHistory[0] + 10;
						}
					}
					if(randomInt == SHIP_ORIENTATION_RIGHT){
						if(!getEnemiesField().getElementByID(idHistory[0] + 1).getAttacked()){
							ret = idHistory[0] + 1;
						}
					}
					if(randomInt == SHIP_ORIENTATION_LEFT){
						if(!getEnemiesField().getElementByID(idHistory[0] - 1).getAttacked()){
							ret = idHistory[0] - 1;
						}
					}
				}
				
				counter++;
			}while(ret == 0 && counter < 4);
		}
		return ret;
	}

	/**
	 * Gibt das gegnerische Feld zurueck
	 * @return Das gegnerische Feld
	 */
	public Field getEnemiesField() {
		return enemiesField;
	}
}

