package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.util.UUID;

import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.view.VisitMultiplayerGame;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;

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
    Game game;
 
    /**
     * Erstellt ein BluetoothConnectThread Objekt
     * @param device Verbundene Geraete
     * @param bluetoothAdapter Der Bluetooth Adapter des Geraetes
     */
    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, VisitMultiplayerGame vmgClass, Game game, String uuid) {
    	//Temporaeres Objekt benutze, da bluetoothSocket final ist
        BluetoothSocket tmp = null;
        this.bluetoothAdapter = bluetoothAdapter;
        this.vmgClass = vmgClass;
        this.game = game;
 
        //Bluetoothsocket erstellen
        try {
        	tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) { }
        catch(Exception ex){
        	ex.printStackTrace();
        }
        bluetoothSocket = tmp;
    }
    
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
        vmgClass.startGame();
    }
 
    /**
     * Erstellt ein Thread, das sich um die bestehende Verbindung verwaltet und
     * Ein- und ausgehende Streams verwaltet
     * @param socket Aufgebaute Bluetooth Socket Verbindung zum Server
     */
    private void manageConnectedSocket(BluetoothSocket socket) {
    	BluetoothConnectedThread btConnectedThread = new BluetoothConnectedThread(socket, vmgClass, null, this.bluetoothAdapter, this.game);
    	btConnectedThread.start();
    	
    	boolean attackHit = true;
    	boolean shipDestroyed = false;
    	btConnectedThread.write((new String("_RETURN_" + Boolean.toString(attackHit) + "_" + Boolean.toString(shipDestroyed))).getBytes());
	}
}
