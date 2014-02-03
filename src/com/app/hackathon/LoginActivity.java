package com.app.hackathon;

import static com.app.hackathon.CommonUtilities.SENDER_ID;
import static com.app.hackathon.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.androidhive.pushnotifications.AlertDialogManager;
import com.androidhive.pushnotifications.ConnectionDetector;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	boolean result=false;
	private final String SAMPLE_DB_NAME = "hackathonDb.db";
	private final String SAMPLE_TABLE_NAME = "items";
	AlertDialogManager alert = new AlertDialogManager();
	Editable text1;
	Editable text2;
    
    // Internet detector
    ConnectionDetector cd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		cd = new ConnectionDetector(getApplicationContext());
		 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(LoginActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
 
        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(LoginActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
             return;
        }
        
        
		Button myButton = (Button) findViewById(R.id.button1);

		myButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("in register click before try ");
				try {
					EditText e0 = (EditText) findViewById(R.id.editText1);
					text1=e0.getText();
					EditText e1 = (EditText) findViewById(R.id.editText2);
					text2=e1.getText();
					String str = "http://androidthebest.com/expirynotify/test.php?cnum="+text2+"&mnum="+text1;
					Log.i("Hackathon",str);

					new DownloadTextTask().execute(new URL(str));
					System.out.println("in register click");
				} catch (IOException e) {
					Log.e("Hackathon", e.getMessage());
				}
				Log.i("project3", result+"");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	class DownloadTextTask extends AsyncTask<URL, Void, Boolean> {
		protected Boolean doInBackground(URL... urls) {
			result = false;
			try {				                   
				Scanner in = new Scanner(urls[0].openStream());
				if (in.hasNextLine()) { result=Boolean.valueOf(in.nextLine());
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			Log.i("project", result+"");
			return result;

		}

		protected void onPostExecute(Boolean result) {						
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(LoginActivity.this);
			Editor editor = sharedPreferences.edit();
			if (sharedPreferences.contains("login")){
				editor.remove("login");
			}
			Log.i("project2", result+"");
			editor.putBoolean("login", result);

			editor.commit();
			if (result==true)
			{
				Log.i("project4", "if-going");
				SQLiteDatabase sampleDB = null;

				try {
					sampleDB =  LoginActivity.this.openOrCreateDatabase(SAMPLE_DB_NAME, MODE_PRIVATE, null);

					sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " +
							SAMPLE_TABLE_NAME +
							" (ProdId VARCHAR, ProdName VARCHAR," +
							" ProdPrice VARCHAR, ExpDate CHAR(10), flag CHAR(1));");

				} catch (SQLiteException se ) {
					Log.e(getClass().getSimpleName(), "Could not create or Open the database");
				} finally {
					if (sampleDB != null) 
						sampleDB.execSQL("DELETE FROM " + SAMPLE_TABLE_NAME);
					sampleDB.close();
				}
				System.out.println("before clling response activity");
				Intent intent = new Intent(LoginActivity.this, ResponseActivity.class);
				intent.putExtra("cnum", text2.toString());
				intent.putExtra("mnum", text1.toString());
				System.out.println("after clling response activity");
				startActivity(intent);
			}
			else
			{
				Log.i("project5", "else - going");
				TextView t0 = (TextView) findViewById(R.id.message);
				t0.setText("Invalid Login ID");
			}

		}
	}	
}


