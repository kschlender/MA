package com.ma.schiffeversenken.android.view;

import java.util.Set;

import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.controller.Bluetooth;
import com.ma.schiffeversenken.android.controller.Game;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

/**
 * Android Activity zur Teilnahme an einem Mehrspieler Spiel
 * @author Maik Steinborn
 */
public class VisitMultiplayerGame extends Activity {
	/**Bluetooth Objekt, das die Bluetooth Verbindung verwaltet*/
	Bluetooth bt;
	/**Zeigt Meldungen in der Activity an, die die Bluetooth Verbindung betrifft*/
	TextView status;
	/**ProgressDialog, der angezeigt wird, wenn neue Geraete gesucht werden*/
	ProgressDialog progress;
	/**Menge von gekoppelten Bluetooth Geraeten*/
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	/**Menge von neuen Bluetooth Geraeten*/
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	/**Wird aufgerufen, wenn ein neues Bluetooth Geraet gefunden wurde oder
	 * die Suche beendet wurde*/
	private BroadcastReceiver mReceiver;
	
	/**
	 * Wird aufgerufen, wenn ein Geraet ausgewaehlt wurde
	 */
	private final OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(final AdapterView<?> pAdapterView, final View pView, final int pPosition, final long pID) {
			// Suche abbrechen, weil bereits ein Geraet ausgewaehlt wurde
			bt.stopDiscoverDevices();
			
			// Die MAC-Adresse des Geraets herausfiltern
			final String info = ((TextView) pView).getText().toString();
			final String address = info.substring(info.length() - 17);

			/*
			 *Field Klasse gibt aktuell noch Fehler 
			 *
			Field enemiesField = new Field(0);
			Field myField = new Field(1);*/
			
			Game game = new Game(1, null, null, false, true,false);
			
			bt.connectToServer(address, VisitMultiplayerGame.this, game);
		}
	};
	
	public void startGame(){
		Intent intent = new Intent(getApplicationContext(), GamePreferencesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("bluetoothGame", "true");
		startActivity(intent);
	}
	
	/**
	 * Erstellt eine VisitMultiplayerGame Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_multiplayer_game);
		bt = new Bluetooth();
		status = (TextView) findViewById(R.id.visit_game_status);
		
		try{
			this.mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			this.mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			final ListView pairedListView = (ListView) this.findViewById(R.id.pairedDevicesLV);
			pairedListView.setAdapter(this.mPairedDevicesArrayAdapter);
			pairedListView.setOnItemClickListener(this.mDeviceClickListener);

			final ListView newDevicesListView = (ListView) this.findViewById(R.id.detectedDevicesLV);
			newDevicesListView.setAdapter(this.mNewDevicesArrayAdapter);
			newDevicesListView.setOnItemClickListener(this.mDeviceClickListener);

			mReceiver = new BroadcastReceiver() {
			    public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        
			        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
			        	//Wenn ein neues Bluetooth Geraet gefunden wurde
						final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
							VisitMultiplayerGame.this.mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
							bt.addPairedDevice(device);
						}
					} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
						//Wenn die Suche nach neuen Geraeten abgeschlossen wurde
						VisitMultiplayerGame.this.setProgressBarIndeterminateVisibility(false);
						if (VisitMultiplayerGame.this.mNewDevicesArrayAdapter.getCount() == 0) {
							VisitMultiplayerGame.this.mNewDevicesArrayAdapter.add(getString(R.string.NoDevicesFound));
						}
						else{
							status.setText(getString(R.string.PleaseSelectADevice));
						}
						
						progress.dismiss();
					}
			    }
			};
			
			//Event Handler registrieren, wenn Geraet gefunden wurde
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			this.registerReceiver(this.mReceiver, filter);

			//Event Handler registrieren, wenn Suche beendet wurde gefunden wurde
			filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			this.registerReceiver(this.mReceiver, filter);
			
			// Get a set of currently paired devices
			final Set<BluetoothDevice> pairedDevices = bt.getPairedDevices();
			if (pairedDevices.size() > 0) {
				this.findViewById(R.id.pairedDevicesLV).setVisibility(View.VISIBLE);
				for (final BluetoothDevice device : pairedDevices) {
					this.mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			} else {
				this.mPairedDevicesArrayAdapter.add(getString(R.string.NoPairedDevices));
			}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		
		int btState = bt.blutoothOK();
		
		if(btState == 1){
			/*
			 * Geraet unterstuetzt kein Bluetooth
			 * Meldung ausgeben und zum vorheriger Activity wechseln
			 */
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.BluetoothNotAvailable), Toast.LENGTH_LONG);
			t.show();
			finish();
		}
		else if(btState == 2){
			//Bluetooth ist nicht enabled
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, Bluetooth.REQUEST_ENABLE_BT);
		}
		else if(btState == 0){
			bt.getPairedDevices();
			bt.discoverDevices();
			
			progress = new ProgressDialog(this);
			progress.setMessage(getString(R.string.SearchingForNewDevices));
			progress.setIndeterminate(true);
			progress.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visit_multiplayer_game, menu);
		return true;
	}
	
	/**
	 * Dieser Toast muss ueber den UI Thread ausgeführt werden, da er von außerhalb aufgerufen wird
	 * @param message Text, der als Toast angezeigt wird
	 */
	public void showToast(final String message){
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(VisitMultiplayerGame.this, message, Toast.LENGTH_SHORT).show();
	        }
	    });
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Activity wird beendet
	 * BroadcastReceiver deregistrieren
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
