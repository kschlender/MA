package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.utils.Json;
import com.ma.schiffeversenken.CameraController;
import com.ma.schiffeversenken.android.AndroidLauncher;
import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.model.Field.ShipsDescriptor;
import com.ma.schiffeversenken.android.view.CreateMultiplayerGame;
import com.ma.schiffeversenken.android.view.VisitMultiplayerGame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

/**
 * Wird aufgerufen, wenn eine Verbindung hergestellt wurde.
 * Wird auf Server- und Clientseite verwendet
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class BluetoothConnectedThread extends Thread {
	/**Buffer Groesse fuer den Bluetooth Stream Reader*/
	public static final int BUFFER_SIZE = 1024*1;
	/**Bluetooth Socket Objekt*/
	private final BluetoothSocket bluetoothSocket;
	/**Eingehender Stream*/
    private final InputStream inStream;
    /**Ausgehender Stream*/
    private final OutputStream outStream;
    /**Initialisiertes Game Objekt*/
    private Game game;
    /**Initialisiertes VisitMultiplayerGame Objekt*/
    private VisitMultiplayerGame vmgClass;
    /**Initialisiertes CreateMultiplayerGame Objekt*/
    private CreateMultiplayerGame cmgClass;
    /**Bluetooth Adapter Objekt*/
    private BluetoothAdapter bluetoothAdapter;
    /**Statische Instanz dieser Klasse*/
    private static BluetoothConnectedThread instance;
    /**String, der bei der Bluetooth Kommunikation genutzt wird.
     * HELLO bedeutet Bluetooth Verbindung erfolgreich aufgebaut*/
    public static final String BLUETOOTH_HELLO = "_HELLO_";
    /**String, der bei der Bluetooth Kommunikation genutzt wird.
     * ATTACK bedeutet Attacke auf eine ID*/
    public static final String BLUETOOTH_ATTACK = "_ATTACK_";
    /**String, der bei der Bluetooth Kommunikation genutzt wird.
     * RETURN bedeutet Ergebnis einer Attacke an den Gegner zurueck senden*/
    public static final String BLUETOOTH_RETURN = "_RETURN_";
    public static final String BLUETOOTH_ENEMY_FIELD = "_FIELD_";
    public static final String BLUETOOTH_ENEMY_FIELD_RETURN = "_FRETURN_";
    public static final String BLUETOOTH_ENEMY_SHIPS = "_SHIPS_";
    public static final String BLUETOOTH_NEWGAME = "_NEWGAME_";
    /** Fuer Notifikationen ausserhalb der App*/
    AndroidLauncher androidLauncher;
	/**
	 * Erstellt ein BluetoothConnectedThread Objekt
	 * @param socket Erstellte Bluetooth Socket Verbindung
	 * @param vmgClass Initialisiertes VisitMultiplayerGame Objekt
	 * @param cmgClass Initialisiertes CreateMultiplayerGame Objekt
	 * @param bluetoothAdapter Bluetooth Adapter Objekt
	 */
    public BluetoothConnectedThread(BluetoothSocket socket, VisitMultiplayerGame vmgClass, CreateMultiplayerGame cmgClass, BluetoothAdapter bluetoothAdapter) {
    	bluetoothSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.vmgClass = vmgClass;
        this.cmgClass = cmgClass;
        this.bluetoothAdapter = bluetoothAdapter;
        
        //Temporaeres Objekt benutze, da Streams final sind
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        inStream = tmpIn;
        outStream = tmpOut;
    	sendHello();
    	instance = this;
    }
 
    /**
     * Gibt die statische Instanz dieser Klasse zurueck
     * @return Statische Instanz dieser Klasse
     */
    public static BluetoothConnectedThread getInstance(){
    	return instance;
    }
    
    /**
     * Game Instanz setzen
     * @param game Game Instanz
     */
    public void setGame(Game game){
    	this.game = game;
    }
    
    /**
     * Gibt das Game Objekt zurueck
     * @return Game Objekt
     */
    public Game getGame(){
    	return this.game;
    }
    
    /**
     * Thread starten
     */
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];  // buffer store for the stream
        int bytes; // bytes returned from read()
        
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
            	if(bluetoothSocket.isConnected()){
	                /*
	                 * Vom InputStream lesen
	                 * Und dann je nach Art der Message Aktion ausfuehren
	                 */
	                bytes = inStream.read(buffer);
	                
	                String readMsg = new String(buffer, 0, bytes);
	                

	                if(readMsg.startsWith(BLUETOOTH_ATTACK)){
	                	if(game.getPrimaryBTGame()){
	                		game.secondGamerAttack(Integer.parseInt(readMsg.substring(readMsg.indexOf(BLUETOOTH_ATTACK) + BLUETOOTH_ATTACK.length())));
	                	}
	                	else if(game.getSecondaryBTGame()){
	                		game.firstGamerAttack(Integer.parseInt(readMsg.substring(readMsg.indexOf(BLUETOOTH_ATTACK) + BLUETOOTH_ATTACK.length())));
	                	}
	                }
	                else if(readMsg.startsWith(BLUETOOTH_HELLO)){
	                	String helloString;
	                	if(vmgClass != null){
	                		helloString = vmgClass.getString(R.string.successfully_connected_with) + readMsg.substring(BLUETOOTH_HELLO.length());
	                		this.vmgClass.showToast(helloString);
	                	}
	                	else{
	                		helloString = cmgClass.getString(R.string.successfully_connected_with) + readMsg.substring(BLUETOOTH_HELLO.length());
	                		this.cmgClass.showToast(helloString);
	                	}
	                }
	                else if(readMsg.startsWith(BLUETOOTH_RETURN)){
	                	String returnAttackHitString = readMsg.substring(readMsg.indexOf(BLUETOOTH_RETURN) + BLUETOOTH_RETURN.length(), readMsg.indexOf("_", readMsg.indexOf(BLUETOOTH_RETURN) + BLUETOOTH_RETURN.length()));
	                	boolean returnAttackHit = Boolean.parseBoolean(returnAttackHitString);                	
	                	
	                	String returnShipDestroyedString = readMsg.substring(readMsg.indexOf(returnAttackHitString) + returnAttackHitString.length() + 1, readMsg.indexOf("_", readMsg.indexOf(returnAttackHitString) + returnAttackHitString.length() + 1));
	                	boolean returnShipDestroyed = Boolean.parseBoolean(returnShipDestroyedString);
	                	
	                	int ind = readMsg.indexOf(returnAttackHitString) + returnAttackHitString.length() + 1 + returnShipDestroyedString.length() + 1;
	                	String returnShipDestroyedIDs = readMsg.substring(ind);
	                	
	                	game.setReturnValues(returnAttackHit, returnShipDestroyed, returnShipDestroyedIDs);
	                }else if(readMsg.startsWith(BLUETOOTH_NEWGAME)){
	                	CameraController.changeStateTo(1, false, false);
	            		CameraController.changeStateTo(8, false, true);
	                }
	                else if(readMsg.startsWith(BLUETOOTH_ENEMY_FIELD_RETURN)){
	                	game.getFirstFieldPlayer().setFeldUebertragenAntwort(true);
	                }
	                else if(readMsg.startsWith(BLUETOOTH_ENEMY_FIELD)){
	                	String jsonPlacedShips=readMsg.substring(BLUETOOTH_ENEMY_FIELD.length());
	                	//Deserialisierung 
	                	Json json = new Json();
	            		ShipsDescriptor desc = json.fromJson(ShipsDescriptor.class, jsonPlacedShips);
	            		
	            		//Feld resetten und Schiffe aus Json Generieren und Platzieren.
	            		boolean erfolgreich = desc.replaceOldFieldPlacedShips(game.getSecondFieldEnemy());
	                	
	                	//Return Schreiben
	            		if(erfolgreich){
						byte[] fieldBytes = (BluetoothConnectedThread.BLUETOOTH_ENEMY_FIELD_RETURN).getBytes();
						write(fieldBytes);
	            		}

//	                	//TODO Erste gehversuche:
//	                	if(readMsg.equals(BLUETOOTH_ENEMY_FIELD+BLUETOOTH_ENEMY_FIELD)){
//	                		//Ende vom Stream
////	                		AndroidLauncher.notificationToUser("FieldUnits.",androidLauncher,androidLauncher.getClass(),BackActivity.class,androidLauncher.getNotificationManager() ,1234568);
//	                		FieldUnit[][] tmpFieldUnits = (FieldUnit[][])Player.fromString(subStringIncomeing);
//	                		game.setEnemyFieldUnits(tmpFieldUnits);
//	                		subStringIncomeing="";
//	                	}else{
//	                		//Stream zu String zusammenfuehren
//	                		subStringIncomeing= subStringIncomeing+readMsg.substring(BLUETOOTH_ENEMY_FIELD.length());
//	                	}
	                }
            	}
            } catch (Exception e){
                break;
			} 
        }
    }
    
    /**
     * Bei Bluetooth Verbindungsabbruch neu verbinden
     */
    public void reconnect(){
    	try{
	    	if(vmgClass != null){
	    		vmgClass.connectToServer(true);
			}
			else{
				cmgClass.startServer(true);
			}
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    /**
     * Bei erfolgreich aufgebauter Verbindung wird der Name dieses Geraets an
     * das Remote Geraet gesendet. Dieser erscheint dort dann als Toast
     */
    private void sendHello(){
    	String sendString = new String(BLUETOOTH_HELLO + " " + bluetoothAdapter.getName());
    	write(sendString.getBytes());
    }
    
    /**
     * Daten ueber Bluetooth Socket senden
     */
    public void write(byte[] bytes) {
        try {
        	outStream.write(bytes);
        } catch (IOException e) { 
        	e.printStackTrace();
        }
    }
    
    /**
     * AndroidLauncher Instanz setzen
     * @param a AndroidLauncher Instanz
     */
    public void setAndroidLauncher(AndroidLauncher a){
    	this.androidLauncher=a;
    }
}
