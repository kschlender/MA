package com.ma.schiffeversenken.android.view;


import com.ma.schiffeversenken.android.R;
import com.ma.schiffeversenken.android.R.id;
import com.ma.schiffeversenken.android.R.layout;
import com.ma.schiffeversenken.android.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
/**
 * Die BackActivity soll den Nutzer auf das vorhergehende Activity befoerdern.
 * Diese ist Hilfreich um den Spieler wieder in die OpenGL Scene zu befoerdern
 * und wird im zusammenhang mit Notifikationen verwendet.
 * 
 * @author Klaus Schlender
 */
public class BackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back);
		//Sofortiges Weiterleiten auf die vorhergehende Ansicht
		finish();

		Button backButton = (Button) findViewById(R.id.Back_Button);
		backButton.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                try {
	                	finish();
	                    
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.back, menu);
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
