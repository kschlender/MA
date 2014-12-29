package com.ma.schiffeversenken.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ma.schiffeversenken.MyGdxGameField;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		//setup um GL20 anstelle GL10 zu nutzen.
		cfg.useGLSurfaceView20API18=true;
		//Sichert eine menge Batteriekapazität, wird deaktiviert solange nicht genutzt.
		cfg.useAccelerometer = false;
		cfg.useCompass=false;
		cfg.useImmersiveMode=false;
		cfg.useWakelock=false;
		initialize(new MyGdxGameField(), cfg);
	}
}
