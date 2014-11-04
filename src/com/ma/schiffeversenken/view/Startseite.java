package com.ma.schiffeversenken.view;


import com.ma.schiffeversenken.R;
import com.ma.schiffeversenken.R.id;
import com.ma.schiffeversenken.R.layout;
import com.ma.schiffeversenken.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Startseite extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startseite);
		
		Button startSpielButton=null, einstellungenButton=null, hilfeButton=null;
		
		createButtons(startSpielButton, R.id.Start_Spiel_Button, "Spiel starten", Spielermodus.class);
		createButtons(einstellungenButton, R.id.Einstellungen_Button, "Einstellungen", Einstellungen.class);
		createButtons(hilfeButton, R.id.Hilfe_Button, "Hilfe", Hilfe.class);
		
		SharedPreferences sp = getSharedPreferences("Main_Preferences", MODE_MULTI_PROCESS);
		Editor editor = sp.edit();
		editor.putString("lautlos", "false");
		editor.putString("vibrationaus", "false");
		editor.apply();
	}
	
	private void createButtons(Button button, int id, String text, final Class c){
		/*
		 * Buttons erstellen
		 */
		Button startSpielButton = (Button) findViewById(id);
		startSpielButton.setText(text);
		startSpielButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Startseite.this, c);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.startseite, menu);
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
