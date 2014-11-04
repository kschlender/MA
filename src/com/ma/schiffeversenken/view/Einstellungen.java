package com.ma.schiffeversenken.view;

import com.ma.schiffeversenken.R;
import com.ma.schiffeversenken.R.id;
import com.ma.schiffeversenken.R.layout;
import com.ma.schiffeversenken.R.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Einstellungen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_einstellungen);
		
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		setCheckBoxes();
	}
	
	private void setCheckBoxes(){
		
		final CheckBox lautlosCheckBox = (CheckBox)findViewById(R.id.Lautlos_Checkbox);
		final CheckBox vibrationausCheckBox = (CheckBox)findViewById(R.id.Vibration_aus_Checkbox);
		
		if(getValue("lautlos") == "true"){
			lautlosCheckBox.setChecked(true);
		}
		
		if(getValue("vibrationaus") == "true"){
			vibrationausCheckBox.setChecked(true);
		}
		
		lautlosCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			/*
			 * Wenn die Lautlos Checkbox betätigt wird,
			 * wird der entsprechende Eintrag in den Shared Preferences geändert
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					setSharedPreferences("lautlos", "true");
				}
				else{
					setSharedPreferences("lautlos", "false");
				}
			}
		});
		
		vibrationausCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			/*
			 * Wenn die Vibration aus Checkbox betätigt wird,
			 * wird der entsprechende Eintrag in den Shared Preferences geändert
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					setSharedPreferences("vibrationaus", "true");
				}
				else{
					setSharedPreferences("vibrationaus", "false");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.einstellungen, menu);
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
	
	public String getValue(String name){
		//Gibt den Wert des entsprechendes Eintrags zurück
		SharedPreferences sp = getSharedPreferences("Main_Preferences", MODE_MULTI_PROCESS);
		return sp.getString(name, "");
	}
	
	public void setSharedPreferences(String name, String value){
		//Setzt einen Wert in Shared Preferences
		SharedPreferences sp = getSharedPreferences("Main_Preferences", MODE_MULTI_PROCESS);
		Editor editor = sp.edit();
		editor.putString(name, value);
		editor.apply();
	}
}
