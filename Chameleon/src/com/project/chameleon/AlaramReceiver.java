package com.project.chameleon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

public class AlaramReceiver extends BroadcastReceiver {

	public static String ACTION_ALARM_CALL = "com.alex.callalaram";
	private Context context;
	private ArrayList list;
	private String phNumber;
	private String callType;
	private String callDate;
	private String id;
	private String Name;
	private Date callDayTime;
	private String callDuration;
	private Calendar cal;
	private TimeInterval routine;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		
		Bundle bundle = intent.getExtras();
		String action = bundle.getString(ACTION_ALARM_CALL);
		
		///if (action.equals(ACTION_ALARM_CALL)) {
			//Toast.makeText(context,"inside", Toast.LENGTH_SHORT).show();
			//Log.i("Alarm Receiver", "Call Called");
			getCallDetails();
//		}
//		else
//		{
//			Log.i("Alarm Receiver", "Else loop");
//			Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
//		}
	}
	 private void getCallDetails() {
	        // TODO Auto-generated method stub
		
		 	DatabaseConnector dbConnector = new DatabaseConnector(context);
			dbConnector.open();
			list=dbConnector.getAllContactsNumbers();
	        Cursor managedCursor = context.getContentResolver().query( CallLog.Calls.CONTENT_URI,null, null,null, null);
	        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
	        int callid = managedCursor.getColumnIndex( CallLog.Calls._ID);
	        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
	        int name = managedCursor.getColumnIndex( CallLog.Calls.CACHED_NAME );
	        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
	        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
	        while ( managedCursor.moveToNext() ) {
	        phNumber = managedCursor.getString( number );
	       callType = managedCursor.getString( type );
	        callDate =managedCursor.getString( date );;
	         id = managedCursor.getString( callid );
	         Name = managedCursor.getString( name );
	        callDayTime = new Date(Long.valueOf(callDate));
	         callDuration = managedCursor.getString( duration );
	         
	        	
	         if(list.contains(phNumber))
	 		{
	        	 //Toast.makeText(context,phNumber+"", Toast.LENGTH_SHORT).show();
	        	Cursor result= dbConnector.getContactAlias(phNumber);
	 		
	        	 if(result.getCount()>0){
	     	        //String queryString= "_id='" + id + "'";
	     	      
	     	         result.moveToFirst();
	     	         // get the column index for each data item
	     	         int capIndex = result.getColumnIndex("sname");
	     	         int aliasIndex = result.getColumnIndex("altnum");
	     	        // Log.e("Debug", result.getString(aliasIndex));
	     	         String aname=result.getString(capIndex);
	     	         String anum=result.getString(aliasIndex);
	     	         insertAliasCall(anum,aname,callDuration,callDayTime,callType);
	     	         
	     	        String queryString= "NUMBER='" + phNumber + "'";
	     		   context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);       
	     	         result.close();
	        	 }
	        
	 		}	
			  
	        }
	       
	         managedCursor.close();
	         dbConnector.close();
	        }
	
	 
	//insert the alias call
	   public void insertAliasCall(String number,String name,String duration,Date daydate,String caltype){
		   cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
	    	ContentValues values = new ContentValues();
	    	values.put(CallLog.Calls.NUMBER, number);
	        values.put(CallLog.Calls.CACHED_NAME,name);
	        values.put(CallLog.Calls.CACHED_NUMBER_LABEL,name);
	        values.put(CallLog.Calls.DATE, cal.getTimeInMillis());
	        values.put(CallLog.Calls.DURATION, duration);
	        values.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
	        values.put(CallLog.Calls.NEW, 0);
	        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);


	        //Log.d("DEBUG", "Inserting call log placeholder for " + number + " at " + addTime);
	        context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
	    }
	public void startCallschedule(Context context) {

		try {
			AlarmManager alarms = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			Intent intent = new Intent(context,
					AlaramReceiver.class);
			intent.putExtra(AlaramReceiver.ACTION_ALARM_CALL,
					AlaramReceiver.ACTION_ALARM_CALL);
			final PendingIntent pIntent = PendingIntent.getBroadcast(context,
					1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			
			//create a new Interval object to provide the time(interval)
			routine = new TimeInterval(context);
			long time = routine.getLogTime();
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					12345, time, pIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//stop the alarm
	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlaramReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
