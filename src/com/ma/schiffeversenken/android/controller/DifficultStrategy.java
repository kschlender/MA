package com.ma.schiffeversenken.android.controller;

import java.util.Random;

import com.ma.schiffeversenken.android.model.Field;
import com.ma.schiffeversenken.android.model.FieldUnit;
import com.ma.schiffeversenken.android.model.Ship;

/**
 * Spielstrategie der KI in der Schwierigkeitsstufe Schwierig
 * @author Maik Steinborn
 */
public class DifficultStrategy implements KIStrategy{
	/**Das initialisierte KI Objekt*/
	KI ki;
	
	/**
	 * Erstellt ein DifficultStrategy Objekt
	 * @param ki Das initialisierte KI Objekt
	 */
	public DifficultStrategy(KI ki) {
		this.ki = ki;
	}
	
	/**
	 * Den Gegner attackieren
	 * @return ID des Feldes, das attackiert werden soll
	 */
	@Override
	public int attack() {
		int nextAttackID = 0;
		Random random = new Random();
		int idForContinueLastAttack = ki.getIDForContinueLastAttack();
		
		if(idForContinueLastAttack != 0){
			//Den letzten Angriff fortfuehren
			nextAttackID = idForContinueLastAttack;
		}
		else{
			boolean secureAttack = random.nextBoolean();
			
			if(secureAttack){
				nextAttackID = getIDForSecureAttack();
			}
			else{
				//Angriff auf eine neue zufaellige FeldID starten
				do{
					//Zufaellige Zahl erstellen
					nextAttackID = random.nextInt(Field.FIELD_SIZE) + 1;
				}while(ki.getEnemiesField().getElementByID(nextAttackID).getAttacked());
			}
		}

		return nextAttackID;
	}
	
	/**
	 * Fuehrt eine sichere Attacke aus,
	 * das heisst, dass auf jeden Fall ein gegnerisches Schiff getroffen wird
	 * @return Die ID des Feldelements, das angegriffen wird
	 */
	private int getIDForSecureAttack(){
		int ret = 0;
		
		outerloop:
		for(Ship ship : ki.getEnemiesField().getShips()){
			if(!ship.isDestroyed()){
				for(FieldUnit fu : ship.getLocation()){
					if(!fu.getAttacked()){
						ret = fu.getID();
						break outerloop; 
					}
				}
			}
		}
		
		return ret;
	}
}
