package com.ma.schiffeversenken.android.view;

import com.ma.schiffeversenken.android.AndroidLauncher;
import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.controller.KI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Android Activity fuer den Einzelspielermodus
 * @author Klaus Schlender
 */
public class StartGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_game);
				
		try{
			createButtons(R.id.button_ki_einfach, KI.KI_SIMPLE, AndroidLauncher.class);
			createButtons(R.id.button_ki_normal, KI.KI_NORMAL, AndroidLauncher.class);
			createButtons(R.id.button_ki_schwer, KI.KI_DIFFICULT, AndroidLauncher.class);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_game, menu);
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
	
	private <E> void createButtons(int id, final String kiDifficulty, final Class <E> c){
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
					SharedPreferences sp = getSharedPreferences("Main_Preferences", MODE_MULTI_PROCESS);
					Editor editor = sp.edit();
					editor.putString("ki", kiDifficulty);
					editor.apply();
				
					Intent intent = new Intent(StartGame.this, c);
					intent.putExtra("primaryBTGame", "false");
					intent.putExtra("secondaryBTGame", "false");
					startActivity(intent);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
	}
}
