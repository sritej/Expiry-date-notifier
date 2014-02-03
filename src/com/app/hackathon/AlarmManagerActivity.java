package com.app.hackathon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AlarmManagerActivity extends Activity{

	private AlarmManagerFb alarmManagerFb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_manager);
		Log.i("project", "check");
		alarmManagerFb.setAlarm(getApplicationContext());
	}

}
