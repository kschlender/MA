package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.util.UUID;
import com.ma.schiffeversenken.android.view.CreateMultiplayerGame;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

/**
 * Ein Thread der auf einkommende Verbindungsanfragen wartet (Server)
 * @author Maik Steinborn 
 */
public class BluetoothListenThread extends Thread{
	/**Lauschender Server Socket, der auf eine Clientverbindung wartet*/
	private final BluetoothServerSocket serverSocket;
	/**Initialisiertes CreateMultiplayerGame Objekt*/
	private CreateMultiplayerGame cmgClass;
	/**BluetoothAdapter Objekt, das spaeter an den BluetoothConnectedThread-Thread weitergegeben wird*/
	private BluetoothAdapter bluetoothAdapter;
	/**Initialisiertes Game Objekt, das spaeter an den BluetoothConnectedThread-Thread weitergegeben wird*/
	private boolean reconnect;
	
	/**
	 * Erstellt ein BluetoothListenThread Objekt
	 * @param bluetoothAdapter Der Bluetooth Adapter des Gereats
	 * @param cmgClass Initialisiertes CreateMultiplayerGame Objekt
	 * @param uuid Eindeutige ID zur Identifizierung der App über Bluetooth
	 * @param reconnect true oder false ob dies ein erneuter Verbindungsversuch ist
	 */
	public BluetoothListenThread(BluetoothAdapter bluetoothAdapter, CreateMultiplayerGame cmgClass, String uuid, boolean reconnect){
        //Temporaeres Objekt benutze, da serverSocket final ist
        BluetoothServerSocket tmp = null;
        this.bluetoothAdapter = bluetoothAdapter;
        this.cmgClass = cmgClass;
        this.reconnect = reconnect;
        
        try {
        	tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Schiffeversenken", UUID.fromString(uuid));
        } catch (IOException e) { }
        serverSocket = tmp;
	}
	
	/**
	 * BluetoothListenThread-Thread starten
	 */
	public void run() {
        BluetoothSocket socket = null;
        
        while (true) {
            try {
            	/*
            	 * Auf Verbindung vom Client warten
            	 * Blockiert, bis Client Verbindung aufbaut
            	 * Oder Fehler zurueckgegeben wird
            	 */
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }
            //Wenn eine Verbindung angenommen wurde
            this.cmgClass.progress.dismiss();
            
            if (socket != null) {
                //Verbindung in separatem Thread verwalten
                manageConnectedSocket(socket);
                
                try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
                break;
            }
        }
    }

	/**
     * Erstellt ein Thread, das sich um die bestehende Verbindung verwaltet und
     * Ein- und ausgehende Streams verwaltet
     * @param mmSocket Aufgebaute Bluetooth Socket Verbindung zum Server
     */
	private void manageConnectedSocket(BluetoothSocket mmSocket) {
		BluetoothConnectedThread btConnectedThread = null;
		
		if(reconnect){
			//Bei wiederholtem Verbindungsversuch
			btConnectedThread = BluetoothConnectedThread.getInstance();
			Game game = btConnectedThread.getGame();
			btConnectedThread = new BluetoothConnectedThread(mmSocket, null, cmgClass, this.bluetoothAdapter);
			btConnectedThread.setGame(game);
			game.setBluetoothConnectedThread(btConnectedThread);
		}
		else{
			//Bei erstem Verbindungsversuch
			btConnectedThread = new BluetoothConnectedThread(mmSocket, null, cmgClass, this.bluetoothAdapter);
			cmgClass.startGame(btConnectedThread);	
		}
		
		btConnectedThread.start();
	}
}
