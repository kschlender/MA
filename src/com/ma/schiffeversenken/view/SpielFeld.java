package com.ma.schiffeversenken.view;

import android.app.ActionBar;
import com.ma.schiffeversenken.R;
import com.ma.schiffeversenken.R.id;
import com.ma.schiffeversenken.R.layout;
import com.ma.schiffeversenken.R.menu;
import com.ma.schiffeversenken.controller.*;
import com.ma.schiffeversenken.model.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpielFeld extends Activity {

    //Basic GL surface View
    GLSurfaceView surfaceView;
    GLRenderer renderer = new GLRenderer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create our surfaceview
        surfaceView = new GLSurfaceView(this);
        //set our renderer
        surfaceView.setRenderer(renderer);

        //set contentview from R.layout.activity_main to this surfaceView
        setContentView(surfaceView);

        Button resetButton = new Button(this);
        TextView tv = new TextView(this);
        LinearLayout ll = new LinearLayout(this);

        resetButton.setText("Reset Scene");
        tv.setText("Reset: ");

        ll.addView(tv);
        ll.addView(resetButton);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

        //linearlayout and l
        addContentView(ll, lp);

        resetButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
				//what happened when i klick the Reset Button

            }
        });

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
				try{
					Intent intent = new Intent(SpielFeld.this, c);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
