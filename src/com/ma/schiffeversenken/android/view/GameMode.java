package com.ma.schiffeversenken.android.view;

import com.ma.schiffeversenken.android.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Android Activity zur Auswahl des Spielermodus
 * @author Maik Steinborn
 */
public class GameMode extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spielermodus);

		createButtons(R.id.Einzelspieler_Button, StartGame.class);
		createButtons(R.id.Mehrspieler_Button, Multiplayer.class);
	}

	private <E> void createButtons(int id, final Class <E> c){
		/*
		 * Buttons erstellen
		 */
		Point p = new Point();
		getWindowManager().getDefaultDisplay().getSize(p);
		int buttonWidth = p.x / 2;
		
		Button button = (Button) findViewById(id);
		RelativeLayout.LayoutParams lParams = (android.widget.RelativeLayout.LayoutParams) button.getLayoutParams();
		lParams.width = buttonWidth;
		
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent(GameMode.this, c);
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
		getMenuInflater().inflate(R.menu.gamemode, menu);
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
