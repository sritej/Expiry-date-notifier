package com.app.hackathon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerFb extends BroadcastReceiver{

	public final static String ONE_TIME = "One Time";

	@Override
	public void onReceive(Context context, Intent intent) {

		setAlarm(context);

	}

	public void setAlarm(Context context){

		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmManagerFb.class);

		intent.putExtra(ONE_TIME, Boolean.FALSE);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 , pi);


	}

}
