//package com.ma.schiffeversenken.android.controller;
//
//import com.ma.schiffeversenken.android.R;
//import com.ma.schiffeversenken.android.model.Battleship;
//import com.ma.schiffeversenken.android.model.Cruiser;
//import com.ma.schiffeversenken.android.model.Destroyer;
//import com.ma.schiffeversenken.android.model.Field;
//import com.ma.schiffeversenken.android.model.Ship;
//import com.ma.schiffeversenken.android.model.Submarine;
//import com.ma.schiffeversenken.android.view.Startseite;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class TestAusgabe extends Activity {
//	Game game;
//	Thread gameThread; 
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		try{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_test_ausgabe);
//		
//		Ship[] myships = new Ship[]{new Submarine("Uboot"),
//				new Submarine("Uboot"),
//				new Submarine("Uboot"),
//				new Cruiser("Kreuzer"),
//				new Cruiser("Kreuzer"),
//				new Cruiser("Kreuzer"),
//				new Cruiser("Kreuzer"),
//				new Destroyer("Zerstoerer"),
//				new Destroyer("Zerstoerer"),
//				new Battleship("Schlachtschiff")
//				};
//		
//		Field firstField = new Field(0);
//		Field secondField = new Field(1);
//		
//		ShipPlacement sp = new ShipPlacement();
//		sp.placeShips(firstField, myships);
//
//		game = new Game(0,firstField, secondField);
//		gameThread = new Thread(game);
//		gameThread.start();
//		
//		TextView ed = (TextView) findViewById(R.id.Test_Text);
//		Intent i = getIntent();
//		ed.setText(game.ki.sp.print());
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
//		}
//		
//		Button okButton = (Button) findViewById(R.id.Test_Ausgabe_OK_Button);
//		okButton.setText("OK");
//		okButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				try{
//					TextView tv = (TextView) findViewById(R.id.);
//					game.firstGamerAngriff(Integer.parseInt(tv.getText().toString()));
//					String ausgabe = "Nicht getroffen";
//					if(game.getroffen) ausgabe = "Getroffen";
//					
//					Toast t = Toast.makeText(getApplicationContext(), ausgabe, Toast.LENGTH_LONG);
//					t.show();
//				}
//				catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.test_ausgabe, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//}
