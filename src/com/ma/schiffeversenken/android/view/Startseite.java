package com.ma.schiffeversenken.android.view;


import com.ma.schiffeversenken.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Android Activity zur Auswahl folgender Optionen:
 * Spiel starten, Einstellungen, Hilfe
 * @author Maik Steinborn
 */
public class Startseite extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startseite);
		
		Button startSpielButton=null, einstellungenButton=null, hilfeButton=null;
		try{
			
			createButtons(startSpielButton, R.id.Start_Spiel_Button, "Spiel starten", Spielermodus.class);
			createButtons(einstellungenButton, R.id.Einstellungen_Button, "Einstellungen", Einstellungen.class);
			createButtons(hilfeButton, R.id.Hilfe_Button, "Hilfe", Hilfe.class);
			//testButton();
			SharedPreferences sp = getSharedPreferences("Main_Preferences", MODE_MULTI_PROCESS);
			Editor editor = sp.edit();
			editor.putString("lautlos", "false");
			editor.putString("vibrationaus", "false");
			editor.apply();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//	private void testButton(){
//		Button startSpielButton = (Button) findViewById(R.id.Test_Button);
//		startSpielButton.setText("Test");
//		startSpielButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				try{
//					Destroyer z = new Destroyer("Zerstoerer");
//					Submarine u = new Submarine("Uboot");
//					
//					Ship[] ships = new Ship[]{new Submarine("Uboot"),
//							new Submarine("Uboot"),
//							new Submarine("Uboot"),
//							new Cruiser("Kreuzer"),
//							new Cruiser("Kreuzer"),
//							new Cruiser("Kreuzer"),
//							new Cruiser("Kreuzer"),
//							new Destroyer("Zerstoerer"),
//							new Destroyer("Zerstoerer"),
//							new Battleship("Schlachtschiff")
//							};
//					Field field = new Field(0);
//					
//					ShipPlacement sp = new ShipPlacement();
//					sp.placeShips(field, ships);
//					
//					Intent inte = new Intent(Startseite.this, TestAusgabe.class);
//					inte.putExtra("Test", sp.print());
//					startActivity(inte);
//				}
//				catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		});
//	}
	
	private <E> void createButtons(Button button, int id, String text, final Class<E> c){
		/*
		 * Buttons erstellen
		 */
		Button startSpielButton = (Button) findViewById(id);
		startSpielButton.setText(text);
		startSpielButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent(Startseite.this, c);
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
