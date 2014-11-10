package com.ma.schiffeversenken.view;

import com.ma.schiffeversenken.R;
import com.ma.schiffeversenken.R.id;
import com.ma.schiffeversenken.R.layout;
import com.ma.schiffeversenken.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Schwierigkeitsstufe extends Activity {
//Test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schwierigkeitsstufe);
        Button Normal_Button = null;
        createButtons(Normal_Button, R.id.Normal_Button, "Normal", SpielFeld.class);
    }

    private void createButtons(Button button, int id, String text, final Class c) {
        /*
         * Buttons erstellen
         */
        Button startSpielButton = (Button) findViewById(id);
        startSpielButton.setText(text);
        startSpielButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Schwierigkeitsstufe.this, c);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schwierigkeitsstufe, menu);
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
