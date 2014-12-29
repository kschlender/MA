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
 * Android Activity zur Auswahl ob ein Mehrspieler Spiel erstellt
 * oder an einem Mehrspieler Spiel teilgenommen werden soll
 * @author Maik Steinborn
 */
public class Multiplayer extends Activity {
	/**
	 * Erstellt eine Multiplayer Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer);
		
		Button CreateGameButton = null;
		Button VisitGameButton = null;
        createButtons(CreateGameButton, R.id.Create_Game_Button, "Spiel erstellen", CreateMultiplayerGame.class);
        createButtons(VisitGameButton, R.id.Visit_Game_Button, "An Spiel teilnehmen", VisitMultiplayerGame.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.multiplayer, menu);
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
	
	private <E> void createButtons(Button button, int id, String text, final Class<E> c) {
        /*
         * Buttons erstellen
         */
        Button startSpielButton = (Button) findViewById(id);
        startSpielButton.setText(text);
        startSpielButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Multiplayer.this, c);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
