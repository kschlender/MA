package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ma.schiffeversenken.android.view.CreateMultiplayerGame;
import com.ma.schiffeversenken.android.view.VisitMultiplayerGame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

/**
 * Wird aufgerufen, wenn eine Verbindung hergestellt wurde.
 * Wird auf Server- und Clientseite verwendet
 * @author Maik Steinborn
 */
public class BluetoothConnectedThread extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final int BUFFER_SIZE = 1024;
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

    /**
     * Erstellt ein BluetoothConnectedThread Objekt
     * @param socket Erstellte Bluetooth Socket Verbindung
     * @param vmgClass Initialisiertes VisitMultiplayerGame Objekt
     * @param cmgClass Initialisiertes CreateMultiplayerGame Objekt
     * @param bluetoothAdapter Bluetooth Adapter Objekt
     * @param game Initialisiertes Game Objekt
     */
    public BluetoothConnectedThread(BluetoothSocket socket, VisitMultiplayerGame vmgClass, CreateMultiplayerGame cmgClass, BluetoothAdapter bluetoothAdapter, Game game) {
    	bluetoothSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.vmgClass = vmgClass;
        this.cmgClass = cmgClass;
        this.bluetoothAdapter = bluetoothAdapter;
        this.game = game;
 
        this.game.setConnectedThread(this);
        
        //Temporaeres Objekt benutze, da Streams final sind
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        inStream = tmpIn;
        outStream = tmpOut;
    	sendHello();
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
	                String attackString = "_ATTACK_";
	                String welcomeString = "_HELLO_";
	                String returnString = "_RETURN_";
	                
	                if(readMsg.startsWith(attackString)){
	                	game.secondGamerAttack(Integer.parseInt(readMsg.substring(readMsg.indexOf(attackString) + attackString.length() + 1))); 
	                }
	                else if(readMsg.startsWith(welcomeString)){
	                	String helloString = "Erfolgreich verbunden mit " + readMsg.substring(welcomeString.length());
	                	if(vmgClass != null){
	                		this.vmgClass.showToast(helloString);
	                	}
	                	else{
	                		this.cmgClass.showToast(helloString);
	                	}
	                }
	                else if(readMsg.startsWith(returnString)){
	                	boolean returnAttackHit = Boolean.parseBoolean(readMsg.substring(readMsg.indexOf(returnString) + returnString.length() + 1, returnString.indexOf("_", returnString.indexOf("_" + 1))));                	
	                	boolean returnShipDestroyed = Boolean.parseBoolean(readMsg.substring(readMsg.indexOf(returnString) + returnString.length() + 1, returnString.indexOf("_", returnString.indexOf("_" + 1))));;
	                	
	                	game.setReturnValues(returnAttackHit, returnShipDestroyed);
	                }
            	}
            	else{
            		//Wenn die Bluetooth Verbindung unterbrochen ist
            	}
            } catch (IOException e) {
                break;
            }
        }
    }
 
    /**
     * Bei erfolgreich aufgebauter Verbindung wird der Namen dieses Geraetes an
     * das Remote Geraet gesendet. Dieser erscheint dort dann als Toast
     */
    private void sendHello(){
    	String sendString = new String("_HELLO_" + bluetoothAdapter.getName());
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

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}
