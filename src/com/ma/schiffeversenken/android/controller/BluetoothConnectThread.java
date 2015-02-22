package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.util.UUID;

import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.view.VisitMultiplayerGame;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Verbindet sich mit einem offenem Server Port (Client)
 * @author Maik Steinborn
 */
public class BluetoothConnectThread extends Thread {
	/**Client Bluetooth Socket*/
	private final BluetoothSocket bluetoothSocket;
	/**BluetoothAdapter Objekt, das spaeter an den BluetoothConnectedThread-Thread weitergegeben wird*/
    private final BluetoothAdapter bluetoothAdapter;
    /**Initialisiertes VisitMultiplayerGame Objekt*/
    VisitMultiplayerGame vmgClass;
    /**Initialisiertes Game Objekt, das spaeter an den BluetoothConnectedThread-Thread weitergegeben wird*/
    private boolean reconnect;
 
    /**
     * Erstellt ein BluetoothConnectThread Objekt
     * @param device Verbundene Geraete
     * @param bluetoothAdapter Der Bluetooth Adapter des Geraetes
     * @param vmgClass Initialisiertes VisitMultiplayerGame Objekt
     * @param uuid Eindeutige ID zur Identifizierung der App über Bluetooth
     * @param reconnect true oder false ob dies ein erneuter Verbindungsversuch ist
     */
    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, VisitMultiplayerGame vmgClass, String uuid, boolean reconnect) {
    	//Temporaeres Objekt benutze, da bluetoothSocket final ist
        BluetoothSocket tmp = null;
        this.bluetoothAdapter = bluetoothAdapter;
        this.vmgClass = vmgClass;
        this.reconnect = reconnect;
 
        //Bluetoothsocket erstellen
        try {
        	tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) { }
        catch(Exception ex){
        	ex.printStackTrace();
        }
        bluetoothSocket = tmp;
    }
    
    /**
     * BluetoothConnectThread-Thread starten
     */
    public void run() {
        //Suche stoppen
    	bluetoothAdapter.cancelDiscovery();
 
        try {
            /*
             * Mit Server ueber den Socket verbinden
             * Blockiert, bis Verbindung hergestellt wurde
             * Oder Fehler zurueck kommt
             */
        	bluetoothSocket.connect();
        } catch (IOException connectException) {
            //Fehler beim Connect
            try {
            	vmgClass.showToast(vmgClass.getString(R.string.connection_error));
            	bluetoothSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        //Verbindung in separatem Thread verwalten
        manageConnectedSocket(bluetoothSocket);
        if(!reconnect) vmgClass.startGame();
    }
 
    /**
     * Erstellt ein Thread, das sich um die bestehende Verbindung verwaltet und
     * Ein- und ausgehende Streams verwaltet
     * @param socket Aufgebaute Bluetooth Socket Verbindung zum Server
     */
    private void manageConnectedSocket(BluetoothSocket socket) {
    	BluetoothConnectedThread btConnectedThread = null;
    	
    	if(reconnect){
    		//Bei wiederholtem Verbindungsversuch
    		btConnectedThread = BluetoothConnectedThread.getInstance();
			Game game = btConnectedThread.getGame();
			btConnectedThread = new BluetoothConnectedThread(socket, vmgClass, null, this.bluetoothAdapter);
			btConnectedThread.setGame(game);
			game.setBluetoothConnectedThread(btConnectedThread);
    	}
    	else{
    		//Bei erstem Verbindungsversuch
    		btConnectedThread = new BluetoothConnectedThread(socket, vmgClass, null, this.bluetoothAdapter);
    	}
    	
    	btConnectedThread.start();
	}
}
