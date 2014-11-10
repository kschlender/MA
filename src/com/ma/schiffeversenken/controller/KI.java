package com.ma.schiffeversenken.controller;

import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter; 
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.ma.schiffeversenken.R;
import com.ma.schiffeversenken.model.*;
import com.ma.schiffeversenken.view.Startseite;

public class KI extends Activity {
	Block bloecke[];
	Spielfeld t;
	
	public KI(){
		bloecke = new Block[25];
	}
	
	public void platziereSchiffe(Spielfeld feld, Schiff[] schiffe){
		this.t = feld;
		//Platziert die Schiffe automatisch auf dem Spielfeld
		bloeckeErstellen(feld);
		Random random = new Random();
		
		for(Schiff schiff:schiffe){
			int randomID = 0;
			int horver = 0;
			int run=0;
			boolean ok = false;
			
			do{
				do{
					//Pruefen ob der Block schon belegt ist.
					do{
						//Zufaellige Zahl erstellen
						randomID = random.nextInt(100);
					}while(randomID == 0);
				}while(!checkBlock(randomID, schiff));
				
				do{
					//Pruefen ob das FeldElement schon belegt ist
					horver = random.nextInt(3);
					run++;
					ok = checkEinheit(horver, schiff, randomID, feld);
				}while(!ok && run<4);
			}while(!ok);
			
			//Wenn die gew�nschten Felder frei sind, kann hier jetzt das 
			//Schiff platziert werden..
			feld.getElementByID(randomID).setBelegt(true);
			FeldElement tempElement = feld.getElementByID(randomID);
			tempElement.setBelegt(true);
			tempElement.platziereSchiff(schiff);
			schiff.setStandort(tempElement, 0);
			int schiffSize=schiff.getSize();
			
			if(schiffSize>1){
				if(horver == 0){
					markElements(1, schiffSize, randomID, feld, schiff);
				}
				else if(horver == 1){
					markElements(-10, schiffSize, randomID, feld, schiff);
				}
				else if(horver == 2){
					markElements(-1, schiffSize, randomID, feld, schiff);
				}
				else if(horver == 3){
					markElements(10, schiffSize, randomID, feld, schiff);
				}
			}
		}
		String hallo="test";
	}
	
	private void markElements(int counter, int size, int id, Spielfeld feld, Schiff schiff){
		int temp=0;
		int finalCounter = 0;
		for(int i=1;i<size;i++){
			finalCounter = counter*i;
			temp=id+finalCounter;
			if(temp>0 && temp<101){
				FeldElement tempElement = feld.getElementByID(temp);
				tempElement.setBelegt(true);
				tempElement.platziereSchiff(schiff);
				schiff.setStandort(tempElement, i);
			}
		}
	}
	
	public String print(){
		//Spielfeld mit platziertes Schiffen wird ausgegeben
		//Dient nur zum testen
		String ret="#############\r\n";
		for(int i=0;i<10;i++){
			ret += "#";
			for(int j=0;j<10;j++){
				FeldElement temp = t.getEinheiten()[i][j];
				if(temp.getBelegt()){
					try{
						if(temp.getPlatziertesSchiff().getName()=="Uboot")ret += "U";
						if(temp.getPlatziertesSchiff().getName()=="Schlachtschiff")ret += "S";
						if(temp.getPlatziertesSchiff().getName()=="Kreuzer")ret += "K";
						if(temp.getPlatziertesSchiff().getName()=="Zerstoerer")ret += "Z";
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}
				else{
					ret += "O";
				}
			}
			ret += "#\r\n";
		}
		ret += "#############\r\n";
		
		return ret;
	}
	
	private boolean checkEinheit(int horver, Schiff schiff, int id, Spielfeld feld){
		int size = schiff.getSize();
		boolean ret = true;
		
		if(feld.getElementByID(id).getBelegt()){
			ret = false;
		}
		else {
			if(horver == 0){
				//Nach rechts gerichtetes Schiff
				ret = checkID(1, size, id, feld);
			}
			else if(horver == 1){
				//Nach oben gerichtetes Schiff
				ret = checkID(-10, size, id, feld);
			}
			else if(horver == 2){
				//Nach links gerichtetes Schiff
				ret = checkID(-1, size, id, feld);
			}
			else if(horver == 3){
				//Nach unten gerichtetes Schiff
				ret = checkID(10, size, id, feld);
			}
		}
		
		return ret;
	}
	
	private boolean checkID(int counter, int size, int id, Spielfeld feld){
		boolean ret = true;
		int temp=0;
		int finalCounter = 0;
		for(int i=1;i<=size;i++){
			finalCounter = counter*i;
			temp=id+finalCounter;
			if(temp>0 && temp<101){
				FeldElement tempElement = feld.getElementByID(temp); 
				if(tempElement.getBelegt()) ret = false;
				if((size-i)>0){
					if(checkKante(counter, tempElement)) ret = false;
				}
			}
			else ret=false;
		}
		return ret;
	}
	
	private boolean checkKante(int counter, FeldElement element){
		int kRichtung = 0;
		boolean ret = false;
		int id = element.getID();
		if((id>0 && id<11) ||
				(id>89 && id<101) ||
				id==11 || id==21 || id==31 || id==41 || id==51 || id==61 ||
				id==71 || id==81 || id==20 || id==30 || id==40 || id==50 ||
				id==60 || id==70 || id==80){
			String test="Hallo";
		}
		
		if(counter == 1) kRichtung = 3;
		else if(counter == -1) kRichtung = 4;
		else if(counter == 10) kRichtung = 2;
		else if(counter == -10) kRichtung = 1;
		
		if(element.getKante(1) == kRichtung || element.getKante(2) == kRichtung) ret = true;
		
		return ret;
	}
	
	private Block getBlockById(int id){
		for(Block b : bloecke){
			for(int i:b.getFelder()){
				if(i==id){
					return b;
				}
			}
		}
		return null;
	}
	
	private boolean checkBlock(int random, Schiff schiff){
		boolean ret = true;
		
		try{
			if(getBlockById(random).getBelegt()) ret = false;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	private void bloeckeErstellen(Spielfeld feld){
		//Teil das Spielfeld in 25 identische Bl�cke auf
		int k=0;
		FeldElement[][] einheiten = feld.getEinheiten();
		for(int i=0; i<10; i=i+2){
			for(int j=0; j<10; j=j+2){
				bloecke[k] = new Block(einheiten[i][j].getID(),
						einheiten[i][j+1].getID(),
						einheiten[i+1][j].getID(),
						einheiten[i+1][j+1].getID());
				k++;
			}
		}
	}
}

