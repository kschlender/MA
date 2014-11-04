package com.ma.schiffeversenken.view;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity {

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
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		//linearlayout and l
		addContentView(ll, lp);
		
		resetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//what happened when i klick the Reset Button
				
			}
		});
		
	}
}
