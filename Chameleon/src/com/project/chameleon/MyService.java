package com.project.chameleon;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	
	/**
	 * Service to handle the alarm in the background
	 */

	AlarmReceiver smsalarm;
	AlaramReceiver callalarm;
	SMSUtility sms;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		smsalarm = new AlarmReceiver();
		callalarm = new AlaramReceiver();
//		sms = new SMSUtility();
//		sms.restoreSms(getApplicationContext(), "inbox");
		//start the alarm
		startRepeatingTimer();
		
	}

	public void startRepeatingTimer() {
		Context context = this.getApplicationContext();
		
		if (smsalarm != null) {
			smsalarm.startSMSchedule(context);
			callalarm.startCallschedule(context);
			
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cancelRepeatingTimer();
	}

	//cancel alarm
	public void cancelRepeatingTimer() {
		Context context = this.getApplicationContext();
		if (smsalarm != null) {
			smsalarm.CancelAlarm(context);
			callalarm.CancelAlarm(context);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}
}
