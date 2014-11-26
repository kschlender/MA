package com.ma.schiffeversenken.android.controller;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/*
 * Verbindet sich mit einem offenem Server Port (Client)
 */
public class BluetoothConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter bluetoothAdapter;
 
    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        this.mmDevice = device;
        this.bluetoothAdapter = bluetoothAdapter;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
    	bluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }
 
    private void manageConnectedSocket(BluetoothSocket mmSocket) {
    	BluetoothConnectedThread btConnectedThread = new BluetoothConnectedThread(mmSocket);
    	btConnectedThread.start();
	}

	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}