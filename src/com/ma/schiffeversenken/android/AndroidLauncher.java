package com.ma.schiffeversenken.android;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ma.schiffeversenken.MyGdxGameField;
import com.ma.schiffeversenken.android.controller.Bluetooth;
import com.ma.schiffeversenken.android.controller.BluetoothConnectedThread;
import com.ma.schiffeversenken.android.view.BackActivity;

/**
 * Der AndroidLauncher initialisiert den OpenGL Context in LibGdx.
 * 
 * @author Maik Steinborn
 * @author Klaus Schlender
 */
public class AndroidLauncher extends AndroidApplication {

	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		//setup um GL20 anstelle GL10 zu nutzen.
		cfg.useGLSurfaceView20API18=true;
		//Sichert eine menge Batteriekapazitaet, wird deaktiviert solange nicht genutzt.
		cfg.useAccelerometer = false;
		cfg.useCompass=false;
		cfg.useImmersiveMode=false;
		cfg.useWakelock=false;
		

		
		boolean primaryBTGame = Boolean.parseBoolean(getIntent().getExtras().get(Bluetooth.PRIMARY_BT_GAME).toString());
		boolean secondaryBTGame = Boolean.parseBoolean(getIntent().getExtras().get(Bluetooth.SECONDARY_BT_GAME).toString());
		
		
		
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		BroadcastReceiver mReceiver;
		
		//Broadcastreceiver fuer einen reconnect
		mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();

	            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	            	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AndroidLauncher.this);
	            		dialogBuilder.setMessage(getString(R.string.bluetooth_reconnect_question))
	            		.setPositiveButton(getString(R.string.yes), dialogClickListener)
	            		.setNegativeButton(getString(R.string.no), dialogClickListener)
	            		.show();
	               
	            }    
	        }
	    };
	    
		this.registerReceiver(mReceiver, filter);
		
		
		initialize(new MyGdxGameField(primaryBTGame, secondaryBTGame,this), cfg);
	}

	/**
	 * Baut die Bluetooth Verbindung neu auf,
	 * wenn der Benutzer den Dialog positiv bestätigt hat
	 */
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	    	if(which == DialogInterface.BUTTON_POSITIVE){
	    		BluetoothConnectedThread btcThread = BluetoothConnectedThread.getInstance();
                btcThread.reconnect();
	    	}
	    	else if(which == DialogInterface.BUTTON_NEGATIVE){
	    		finish();
	    	}
	    }
	};
	
	
	/**
	 * Diese Methode dient der Meldung an den Nutzer ausserhalb der Anwendung.
	 * 
	 * @param note Text
	 * @param context ist ein Context der vom Builder genutzt wird um die RemoteViews zu erstellen. 
	 * @param sourceActivityClassParent Elternteil Activity-Klassennamen JavaClass.class.
	 * @param sourceActivityClassResult Resultierender Activity-Klassennamen JavaClass.class.
	 * @param mNM NotificationManager.
	 * @param mId id  Eine id fuer die Meldung, welche unique in der Anwendung ist.
	 */
	public static void notificationToUser(String note,Context context,Class<?> sourceActivityClassParent,Class<?> sourceActivityClassResult,NotificationManager mNM,int mId){
		Notification.Builder mBuilder =
		        new Notification.Builder(context)
		        .setSmallIcon(R.drawable.schiffeversenken_logo)
		        .setContentTitle("Schiffeversenken")
		        .setContentText(note);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, sourceActivityClassResult);
	
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(sourceActivityClassParent);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		
		// Creates the PendingIntent
		PendingIntent resultPendingIntent =
		        PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
	
		mBuilder.setContentIntent(resultPendingIntent);
	//	NotificationManager mNotificationManager =
	//	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationManager mNotificationManager = mNM;
	//	int mId=1234567;
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		//zum beenden der Notification
	//	mNotificationManager.cancel(mId);
	}
}
