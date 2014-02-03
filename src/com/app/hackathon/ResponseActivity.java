package com.app.hackathon;

import static com.app.hackathon.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.app.hackathon.CommonUtilities.EXTRA_MESSAGE;
import static com.app.hackathon.CommonUtilities.SENDER_ID;

import org.json.JSONObject;

import com.androidhive.pushnotifications.AlertDialogManager;
import com.androidhive.pushnotifications.ConnectionDetector;
import com.androidhive.pushnotifications.WakeLocker;
import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class ResponseActivity extends Activity {
	// Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
     
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
     
    // Connection detector
    ConnectionDetector cd;
     
    public static String cnum;
    public static String mnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);
		
		cd = new ConnectionDetector(getApplicationContext());
		 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(ResponseActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
         Log.i("hello in response ", "inresponseactivity ");
        // Getting name, email from intent
        Intent i = getIntent();
         
        cnum = i.getStringExtra("cnum");
        mnum = i.getStringExtra("mnum");     
         
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
 
      //  lblMessage = (TextView) findViewById(R.id.lblMessage);
         
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
         
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        Log.i("Before calling register on GCM  ", "Before calling register on GCM  "+regId+"   after regId");
        if (regId.equals("")) {
        	Log.i("calling register on GCM  ", "calling register on GCM ");
            // Registration is not present, register now with GCM          
            GCMRegistrar.register(this, SENDER_ID);
        	 //Intent intnt = new Intent(getApplicationContext(), ResponseActivity.class);
             
             // Registering user on our server                  
             // Sending registraiton details to MainActivity
             //i.putExtra("name", name);
             //i.putExtra("email", email);
            // startActivity(intnt);
        } else {
            // Device is already registered on GCM
        	Log.i("checking if registerd already GCM  ", " GCM ");
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.             
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
 
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, cnum, mnum, regId);
                        return null;
                    }
 
                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
 
                };
                mRegisterTask.execute(null, null, null);
            }
        }
	}
	
	/**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            /*try {
             = new JSONObject(newMessage); 
            }
            catch(Exception e)
            {
            	
            }*/
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
             
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
             
            // Showing received message
          //  lblMessage.append(newMessage + "\n");  
            
            
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
             
            // Releasing wake lock
            WakeLocker.release();
        }
    };
     
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.response, menu);
		return true;
	}

}
