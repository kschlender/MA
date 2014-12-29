package com.ma.schiffeversenken.android.view;

import com.ma.schiffeversenken.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Android Activity zur Auswahl des Spielermodus
 * @author Maik Steinborn
 */
public class Spielermodus extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielermodus);

		Button singlePlayerButton = (Button) findViewById(R.id.Einzelspieler_Button);
		Button multiPlayerButton = (Button) findViewById(R.id.Mehrspieler_Button);
		
		singlePlayerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), GamePreferencesActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				
				startActivity(intent);
			}
		});
		
		multiPlayerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Multiplayer.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				
				startActivity(intent);
			}
		});
		
	}

	private <E> void createButtons(Button button, int id, String text, final Class <E> c){
		/*
		 * Buttons erstellen
		 */
		Button startSpielButton = (Button) findViewById(id);
		startSpielButton.setText(text);
		startSpielButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent(Spielermodus.this, c);
					startActivity(intent);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.spielermodus, menu);
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
}
