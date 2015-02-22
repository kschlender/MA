package com.ma.schiffeversenken.android.controller;
import java.util.Random;

import com.ma.schiffeversenken.android.model.*;
/**
* Spiellogik des Computer Gegners
* @author Maik Steinborn
*/
public class KI {
	/**Statischer String, der fuer die Uebergabe von Eigenschaften über SharedPrefereces dient*/
	public static final String KI_SIMPLE = "Einfach";
	/**Statischer String, der fuer die Uebergabe von Eigenschaften über SharedPrefereces dient*/
	public static final String KI_NORMAL = "Mittel";
	/**Statischer String, der fuer die Uebergabe von Eigenschaften über SharedPrefereces dient*/
	public static final String KI_DIFFICULT = "Schwer";
	/**Anzahl der Angriffe, die in der History gespeichert werden*/
	public static final int NUMBER_OF_HISTORY = 6;
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
	/**Strategie bzw. Schwierigkeitsstufe der KI*/
	KIStrategy kiStrategy;
	
	/**
	 * Erstellt ein neues KI Objekt
	 * @param myField Spielfeld der KI
	 * @param enemiesField Spielfeld des Gegners
	 * @param loadedGame Boolean ob das Spiel geladen wurde
	 * @param difficultyLevel Schwierigkeitsstufe der KI
	 */
	public KI(Field myField, Field enemiesField, boolean loadedGame, int difficultyLevel){
		this.myField = myField;
		this.enemiesField = enemiesField;
		this.kiStrategy = new NormalStrategy(this);
		
		if(difficultyLevel == 1){
			this.kiStrategy = new SimpleStrategy(this);
		}
		else if(difficultyLevel == 2){
			this.kiStrategy = new NormalStrategy(this);
		}
		else if(difficultyLevel == 3){
			this.kiStrategy = new DifficultStrategy(this);
		//-1, its only to run Test on Desktop
		}else if(difficultyLevel == -1){
			this.kiStrategy = new DifficultStrategy(this);
		}
		
		if(!loadedGame){//myField hat schon Schiffe
			setShips(createShips(3, 4, 2, 1));
		}
			
		initHistory();
	}
	
	/**
	* History updaten
	* Die History speichert die letzten 6 Angriffe
	* @param id Die id der letzten Angriffe
	* @param hit True oder False ob bei den letzten Angriffen ein Schiff getroffen wurde
	* @param shipDestroyed True oder False ob bei den letzten Angriffen ein Schiff zerstoert wurde
	* @param fieldUnits Sämtliche Feldelemente, die zu dem Schiff gehoeren, das gerade getroffen wurde
	*/
	public void updateHistory(int id, boolean hit, boolean shipDestroyed, FieldUnit[] fieldUnits){
		for(int i=NUMBER_OF_HISTORY-1; i>0; i--){
			idHistory[i] = idHistory[i-1];
			hitHistory[i] = hitHistory[i-1];
			shipDestroyedHistory[i] = shipDestroyedHistory[i-1];
			
			if(shipDestroyed && fieldUnits != null){
				for(FieldUnit fu : fieldUnits){
					if(idHistory[i] == fu.getID()){
						shipDestroyedHistory[i] = true;
					}
				}
			}
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
			ships[i] = new Ship(Ship.SUBMARINE_SIZE);
		}
		
		for(j=0; j<numberOfCruiser; j++){
			ships[j+numberOfSubmarines] = new Ship(Ship.CRUISER_SIZE);
		}
		
		for(k=0; k<numberOfDestroyer; k++){
			ships[k+numberOfSubmarines+numberOfCruiser] = new Ship(Ship.DESTROYER_SIZE);
		}
		
		for(l=0; l<numberOfBattleShips; l++){
			ships[l+numberOfSubmarines+numberOfCruiser+numberOfDestroyer] = new Ship(Ship.BATTLESHIP_SIZE);
		}
		
		return ships;
	}
	
	/**
	* Schiffe auf dem Spielfeld der KI platzieren
	* @param ships Die Schiffe, die platziert werden
	*/
	private void setShips(Ship[] ships){
		ShipPlacement sp = new ShipPlacement();
		sp = new ShipPlacement();
		sp.placeShips(myField, ships);
	}
	
	/**
	* Den Gegner attackieren
	* @return ID des Feldes, das attackiert werden soll
	*/
	public int attack(){
		return kiStrategy.attack();
	}
	
	/**
	 * Prueft ob eine ID in der History vorhanden ist
	 * @param id ID, die ueberprueft werden soll
	 * @return True, wenn die gesuchte ID in der History vorhanden ist
	 */
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
		
		for(int i=0; i<NUMBER_OF_HISTORY; i++){
			if(ret == 0){
				if(hitHistory[i]){
					if(!shipDestroyedHistory[i]){
						if((idHistory[i] - 1) > 0 && (idHistory[i] + 1) <= Field.FIELD_SIZE){
							if(idHistoryContains(idHistory[i] + 1)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != FieldUnit.EDGE_LEFT && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != FieldUnit.EDGE_LEFT && !getEnemiesField().getElementByID(idHistory[i] - 1).getAttacked()){
									ret = idHistory[i] - 1;
								}
							}
						}
						if((idHistory[i] - 1) > 0 && (idHistory[i] + 1) <= Field.FIELD_SIZE){
							if(idHistoryContains(idHistory[i] - 1)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != FieldUnit.EDGE_RIGHT && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != FieldUnit.EDGE_RIGHT && !getEnemiesField().getElementByID(idHistory[i] + 1).getAttacked()){
									ret = idHistory[i] + 1;
								}
							}
						}
						if((idHistory[i] - 10) > 0 && (idHistory[i] + 10) <= Field.FIELD_SIZE){
							if(idHistoryContains(idHistory[i] + 10)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != FieldUnit.EDGE_ABOVE && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != FieldUnit.EDGE_ABOVE && !getEnemiesField().getElementByID(idHistory[i] - 10).getAttacked()){
									ret = idHistory[i] - 10;
								}
							}
						}
						if((idHistory[i] - 10) > 0 && (idHistory[i] + 10) <= Field.FIELD_SIZE){
							if(idHistoryContains(idHistory[i] - 10)){
								if(getEnemiesField().getElementByID(idHistory[i]).getEdge(1) != FieldUnit.EDGE_BELOW && getEnemiesField().getElementByID(idHistory[i]).getEdge(2) != FieldUnit.EDGE_BELOW && !getEnemiesField().getElementByID(idHistory[i] + 10).getAttacked()){
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
	* wird durch Zufall bestimmt in welche Richtung der naechste Angriff geht
	* @param ret Die aktuelle ID, die angegriffen wird
	* @return Die neue ID, die angegriffen werden soll
	*/
	private int getIDAfterOneAttackToShip(int ret) {
		int historyIndex = 0;
		
		if(ret == 0){
			for(int i=0; i<NUMBER_OF_HISTORY; i++){
				if(hitHistory[i] && !shipDestroyedHistory[i]){
					historyIndex = i;
					break;
				}
			}
			
			if(hitHistory[historyIndex]){
				Random random = new Random();
				int counter = 0;
				do{
					int randomInt = random.nextInt(Ship.NUMBER_OF_ORIENTATIOS);
					if(getEnemiesField().getElementByID(idHistory[historyIndex]).getEdge(1) != randomInt && getEnemiesField().getElementByID(idHistory[historyIndex]).getEdge(2) != randomInt){
						if(randomInt == Ship.SHIP_ORIENTATION_ABOVE){
							if(!getEnemiesField().getElementByID(idHistory[historyIndex] - 10).getAttacked()){
								ret = idHistory[historyIndex] - 10;
							}
						}
						if(randomInt == Ship.SHIP_ORIENTATION_BELOW){
							if(!getEnemiesField().getElementByID(idHistory[historyIndex] + 10).getAttacked()){
								ret = idHistory[historyIndex] + 10;
							}
						}
						if(randomInt == Ship.SHIP_ORIENTATION_RIGHT){
							if(!getEnemiesField().getElementByID(idHistory[historyIndex] + 1).getAttacked()){
								ret = idHistory[historyIndex] + 1;
							}
						}
						if(randomInt == Ship.SHIP_ORIENTATION_LEFT){
							if(!getEnemiesField().getElementByID(idHistory[historyIndex] - 1).getAttacked()){
								ret = idHistory[historyIndex] - 1;
							}
						}
					}
					counter++;
				}while(ret == 0 && counter < NUMBER_OF_HISTORY);
			}
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
