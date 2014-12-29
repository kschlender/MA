package com.ma.schiffeversenken.android.view;

import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.controller.Bluetooth;
import com.ma.schiffeversenken.android.controller.Game;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Android Activity zum Erstellen eines Mehrspieler Spiels
 * @author Maik Steinborn
 */
public class CreateMultiplayerGame extends Activity {
	/**Bluetooth Objekt zur Verwaltung der Bluetooth Verbindung*/
	Bluetooth bt;
	/**ProgressDialog, der angezeigt wird, wenn auf ein Mitspieler gewartet wird*/
	public ProgressDialog progress;
	
	/**
	 * Erstellt eine CreateMultiplayerGame Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_multiplayer_game);
		
		bt = new Bluetooth();
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
		else{
			Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(getVisible, 0);
			progress = new ProgressDialog(this);
			progress.setMessage(getString(R.string.WaitForTakers));
			progress.setIndeterminate(true);
			progress.show();
			
			/*
			 * Field Klasse gibt aktuell noch Fehler
			Field firstField = new Field(0);
			Field secondField = new Field(1);*/
			Game game = new Game(1, null, null, true, false,false);
			
			bt.startServer(this, game);
		}
	}

	public void startGame(){
			Intent intent = new Intent(getApplicationContext(), GamePreferencesActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("bluetoothGame", "true");
			startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_multiplayer_game, menu);
		return true;
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
	 * Dieser Toast muss ueber den UI Thread ausgeführt werden, da er von außerhalb aufgerufen wird
	 * @param message Text, der als Toast angezeigt wird
	 */
	public void showToast(final String message){
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(CreateMultiplayerGame.this, message, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
}
