package com.app.hackathon;

import com.androidhive.pushnotifications.AlertDialogManager;
import com.androidhive.pushnotifications.ConnectionDetector;
import com.androidhive.pushnotifications.WakeLocker;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

import static com.app.hackathon.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.widget.Toast;

public class MainActivity extends Activity {
	static final String EXTRA_MESSAGE = "message";
	
	ConnectionDetector cd;
	// Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
     
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadSavedPreferences();
        
    }
    private void loadSavedPreferences() {
    	
    	cd = new ConnectionDetector(getApplicationContext());
    	 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
         
        // Getting name, email from intent
//        Intent i = getIntent();
//         
//        name = i.getStringExtra("name");
//        email = i.getStringExtra("email");     
         
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
 
       // lblMessage = (TextView) findViewById(R.id.lblMessage);
         
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
         
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        
        
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean login=false; 
		if (sharedPreferences.contains("login")){
		login= sharedPreferences.getBoolean("login", false);
		}
		
		if (!login || regId.equals("")) {
			//GCMRegistrar.register(this, SENDER_ID);
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
		} else {
			Intent intent = new Intent(MainActivity.this, ResponseActivity.class);
            startActivity(intent);
			
		}

		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	public void sendBasicNotification(View view) {

		Notification notification = new Notification.Builder(this)

		.setContentTitle("Basic Notification")

		.setContentText("Basic Notification, used earlier")

		.addAction(R.drawable.ic_launcher, "Yes", getPendingIntent())

		.addAction(R.drawable.ic_launcher, "No", getPendingIntent())

		.setSmallIcon(R.drawable.ic_launcher).build();


		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = getNotificationManager();

		notificationManager.notify(0, notification);
	}

	public PendingIntent getPendingIntent() {


		return PendingIntent.getActivity(this, 0, new Intent(this,
				HandleNotificationActivity.class), 0);

	}

	public NotificationManager getNotificationManager() {

		return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	}
	/**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
             
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
             
            // Showing received message
            //lblMessage.append(newMessage + "\n");          
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
             
            // Releasing wake lock
            WakeLocker.release();
        }
    };
    
}


