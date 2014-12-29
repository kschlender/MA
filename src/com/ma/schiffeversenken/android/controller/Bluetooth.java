package com.ma.schiffeversenken.android.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.ma.schiffeversenken.android.view.CreateMultiplayerGame;
import com.ma.schiffeversenken.android.view.GamePreferencesActivity;
import com.ma.schiffeversenken.android.view.VisitMultiplayerGame;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

/**
 * Verwaltet die Bluetooth Verbindung
 * @author Maik Steinborn
 */
public class Bluetooth extends Activity {
	private static final int BLUETOOTH_NOT_SUPPORTED = 1;
	private static final int BLUETOOTH_DISABLED = 2;
	/**Eine Konstante, die an die Methode startActivityForResult() uebergeben wird,
	 * wenn das Bluetooth nicht eingeschaltet*/
	public static final int REQUEST_ENABLE_BT = 12;
	/**Der Bluetooth Adapter des Geraets*/
	private BluetoothAdapter bluetoothAdapter;
	/**Die verbundenen Geraete*/
	private Set<BluetoothDevice> pairedDevices;
	/**Alle Geraete mit denen eine Verbindung aufgebaut werden kann*/
	private List<BluetoothDevice> allDevices;
	/**Eindeutige UUID, die auf Server- und Clientseite beim Aufbau der Bluetooth Verbindung genutzt wird*/
	private final String appUUID = "00001101-0000-1000-8000-00805f9b34fb";
	
	
	/**
	 * Erzeugt ein neues Bluetooth Objekt
	 */
	public Bluetooth(){
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		allDevices = new ArrayList<BluetoothDevice>();
	}
	
	/**Sucht verfuegbare Geraete*/
	public boolean discoverDevices(){
		return bluetoothAdapter.startDiscovery();		
	}

	/**Stoppt die Suche nach verfuegbaren Geraeten*/
	public void stopDiscoverDevices(){
		bluetoothAdapter.cancelDiscovery();
	}
		
	/**
	 * Gibt die verbundenen Geraete zurueck
	 * @return Die verbundenen Geraete
	 */
	public Set<BluetoothDevice> getPairedDevices(){
		pairedDevices = bluetoothAdapter.getBondedDevices();	
		
		for(Iterator<BluetoothDevice> it = pairedDevices.iterator(); it.hasNext();){
			allDevices.add(it.next());
		}
		
		return pairedDevices; 
	}
	
	/**
	 * Fuegt zu den verfuegbaren Geraeten ein neues Geraet hinzu
	 * @param device Das Geraet, das zu den verfuegbaren Geraeten hinzugefuegt wird
	 */
	public void addPairedDevice(BluetoothDevice device){
		allDevices.add(device);
	}
	
	/**
	 * Bluetooth Server Socket in einem Thread starten und auf Verbindung von Client warten
	 * @param cmgClass Das initialisierte CreateMultiplayerGame Objekt
	 * @param game Das initialisierte Game Objekt
	 */
	public void startServer(CreateMultiplayerGame cmgClass, Game game){
		BluetoothListenThread btListenThread = new BluetoothListenThread(bluetoothAdapter, cmgClass, game, appUUID);
		btListenThread.start();
	}
	
	/**
	 * Verbindung mit Server aufbauen
	 * @param mac Die MAC-Adresse des Servers, mit dem die Verbindung aufgebaut werden soll
	 * @param vmgClass Das initialisierte VisitMultiplayerGame Objekt
	 * @param game Das initialisierte Game Objekt
	 */
	public void connectToServer(String mac, VisitMultiplayerGame vmgClass, Game game){
		BluetoothDevice device = null;
		
		for(BluetoothDevice dev : allDevices){
			if(dev.getAddress().equals(mac)){
				device = dev;
				break;
			}
		}
		
		if(device != null){
			BluetoothConnectThread btConnectThread = new BluetoothConnectThread(device, bluetoothAdapter, vmgClass, game, appUUID);
			btConnectThread.start();
		}
	}
	
	/**
	 * Prueft ob Bluetooth verfuegbar und enabled ist
	 * @return 0=Bluetooth ist eingeschaltet, 1=Geraet unterstuetzt kein Bluetooth, 2=Bluetooth ist disabled
	 */
	public int blutoothOK(){
		int ret = 0;
		
		if(bluetoothAdapter == null){
			//Geraet unterstuetzt kein Bluetooth
			ret = BLUETOOTH_NOT_SUPPORTED;
		}
		else{
			if(!bluetoothAdapter.isEnabled()){
				//Wenn Bluetooth disabled ist, Bluetooth enablen
				ret = BLUETOOTH_DISABLED;
			}
		}
		
		return ret;
	}
}
