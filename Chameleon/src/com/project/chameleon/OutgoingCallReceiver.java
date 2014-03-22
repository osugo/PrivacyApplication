package com.project.chameleon;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {

	private DatabaseConnector connect;
	private TimeInterval routine;
	 protected Context context;
	private String phone;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		// TODO Auto-generated method stub
		
		phone= intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Log.d("phone",phone);
		
		 routine = new TimeInterval(context);
		long time = routine.getLogTime();
		boolean state = routine.getState();
		Log.e("state", state+"");
		if(time==1000){
			deleteCall();
		}
		
	}
	 public void deleteCall(){
    	 connect = new DatabaseConnector(context);
     		connect.open();
	    	
	    	String sub =  phone;
	    	   
    		// if the contact is in cameleon prevent it from going to other sms applications
            if(connect.checkforContact(sub)>0){
            	
            	 Intent service = new Intent(context,
            			 outgoingService.class);
                // service.putExtra("time",currentMessage.getTimestampMillis()+"");
                 service.putExtra("number",sub);
     			
     			context.startService(service);
     			new LoadContacts().execute();
            }
	    	
    	 
     }
	 //loads contact information
	   private class LoadContacts extends AsyncTask<Long, Object, Cursor> 
	   {
	      DatabaseConnector dbConnector = new DatabaseConnector(context);
		private int i; 
	      @Override
	      protected Cursor doInBackground(Long... params)
	      {

	         dbConnector.open();
	     	
	         return dbConnector.getContactAlias(phone);
	      } 

	      @Override
	      protected void onPostExecute(Cursor result)
	      {
	         super.onPostExecute(result);
	         String queryString= "NUMBER='" + phone + "'";
	       Log.e("querry", queryString);
	         if(result.getCount()>0){
	        	context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null); 
	          
	         result.moveToFirst();
	         // get the column index for each data item
	         int capIndex = result.getColumnIndex("sname");
	         int aliasIndex = result.getColumnIndex("altnum");
	         //Log.e("Debug", result.getString(aliasIndex));
	         String aname=result.getString(capIndex);
	         
	         String anum=result.getString(aliasIndex);
	         insertAliasCall(anum,aname,"900");
	         result.close();
	         dbConnector.close();
	         }
	      }
	   }
	   //insert the alias call
	   public void insertAliasCall(String number,String name,String duration){
		   int calltype;
		  
		  
			   calltype =CallLog.Calls.OUTGOING_TYPE;
		  
	    	ContentValues values = new ContentValues();
	    	values.put(CallLog.Calls.NUMBER, number);
	        values.put(CallLog.Calls.CACHED_NAME,name);
	        values.put(CallLog.Calls.CACHED_NUMBER_LABEL,"Zain");
	        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
	        values.put(CallLog.Calls.DURATION, duration);
	        values.put(CallLog.Calls.TYPE, calltype);
	        values.put(CallLog.Calls.NEW, 1);
	        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);


	        //Log.d("DEBUG", "Inserting call log placeholder for " + number + " at " + addTime);
	        context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
	        
	    }

}
