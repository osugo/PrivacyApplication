package com.project.chameleon;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	/**
	 * Receives the timed broadcast from the alarm manager and deleted the call
	 * log and inbox
	 */
	TimeInterval routine;
	public static String ACTION_ALARM = "com.alarammanager.alaram";
private Context conext;
private ArrayList list;
private Calendar cal;
	@Override
	public void onReceive(Context context, Intent intent) { 
		this.conext =context;
		//code to delete the call log and inbox of the specific contact(s)
//		 VaultsDatabaseAdapter dbC = new VaultsDatabaseAdapter(context);
//	        dbC.open();
//	       dbC.insertEntry("8989", "sms");
////	       Log.e("count", dbC.getCount()+"");
//	         
//	         dbC.close();
		DatabaseConnector dbConnector = new DatabaseConnector(context);
		dbConnector.open();
		Uri uri = Uri.parse("content://sms/sent");
          ContentResolver contentResolver = context.getContentResolver();
          Cursor cursor = contentResolver.query(uri, new String[] { "_id", "address"}, null, null,null);
          list=dbConnector.getAllContactsNumbers();
          if(cursor.getCount()>0){
          while (cursor.moveToNext()) {               
          String num = cursor.getString(1);
		if(list.contains(num))
		{
			Uri thread = Uri.parse("content://sms");
			String where = "address="+num;
			context.getContentResolver().delete(thread, where, null);
			
			
		}else{
			//Log.i("Debug","haikoko");
		}
          }
          }else{
        	  Log.i("smsmnum","Empty");
          }
          cursor.close();
         dbConnector.close();
		Bundle bundle = intent.getExtras();
		String action = bundle.getString(ACTION_ALARM);
		//if (action.equals(ACTION_ALARM)) {
			//Log.i("Alarm Receiver", "If loop");
//			Intent inService = new Intent(context,TaskService.class);
//			context.startService(inService);
			deleteReadChameleonSms();
//		}
//		else
//		{
//			Log.i("Alarm Receiver", "Else loop");
//			Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
//		}
	}
		
		
	

	//set the alarm
	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10);
		
		//create a new Interval object to provide the time(interval)
		routine = new TimeInterval(context);
		long time = routine.getTime();
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), time,
				pi);
	}

	//stop the sms alarm
	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
	public void startSMSchedule(Context context) {

		try {
			AlarmManager alarms = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			Intent intent = new Intent(context,
					AlarmReceiver.class);
			intent.putExtra(AlarmReceiver.ACTION_ALARM,
					AlarmReceiver.ACTION_ALARM);
			final PendingIntent pIntent = PendingIntent.getBroadcast(context,
					1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
			
			//create a new Interval object to provide the time(interval)
			routine = new TimeInterval(context);
			long time = routine.getTime();
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					cal.getTimeInMillis(), time, pIntent);
			
			//toast(context,"Started...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void toast(Context context,String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT)
				.show();
	}
	//delete chameleon read sms from inbox
	public void deleteReadChameleonSms(){
//    Thread closeActivity = new Thread(new Runnable() {
//	   		  @Override
//	   		  public void run() {
	   		    try {
	   		      //Thread.sleep(3000);
	   		   Uri uri = Uri.parse("content://sms");
	             ContentResolver contentResolver = conext.getContentResolver();
	             String where = "read="+1;
	             Cursor cursor = contentResolver.query(uri, new String[] { "_id", "address"}, where, null,null);
	             if(cursor.getCount()>0){
	                 while (cursor.moveToNext()) {               
	                 String num = cursor.getString(1);
	                 //Log.i("smsmnum",num);
	       		if(list.contains(num))
	       		{
	       			//Log.i("Debug","iko");
	       			Uri thread = Uri.parse("content://sms");
	       			 where = "address="+num+" and read="+1;
	       			conext.getContentResolver().delete(thread, where, null);
	       			
	       		}else{
	       			//Log.i("Debug","haikoko");
	       		}
	                 }
	                 }else{
	               	  Log.i("smsmnum","Empty");
	                 }
	                 cursor.close();
			    } catch (Exception e) {
			      e.getLocalizedMessage();
			    }
//			  }
//			});
//		closeActivity.start();
	}
	
	//method for inserting a dumy message
 	    public boolean insertDumySms(String folderName,String phone,String name) {
 	        boolean ret = false;
 	        try {
 	            ContentValues values = new ContentValues();
 	            values.put("address", phone);
 	            values.put("body", name);
 	            values.put("read", 0);
 	            values.put("date", System.currentTimeMillis());
 	            conext.getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
 	            ret = true;
 	        } catch (Exception ex) {
 	        	//Log.e("DEBUG", ex+"");
 	        }
 	        return ret;
 	    }
	}

