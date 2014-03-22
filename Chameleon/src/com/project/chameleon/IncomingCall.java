package com.project.chameleon;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IncomingCall extends BroadcastReceiver {
    
    private static final String LOG_TAG = "CHECKING";
	protected Context context;
    public  String  phone="";
	private TimeInterval routine;
	private DatabaseConnector connect;
	private Bundle bundle;
	private boolean wasRinging=false;
	private boolean attended=false;

   

	public void onReceive(Context context, Intent intent) {
		 this.context = context;
		 bundle = intent.getExtras();
         
         if(null == bundle)
                 return;
//    	 extras = intent.getExtras();
//    	 if (null == extras) {
//    	        return;
//    	    }
    	 routine = new TimeInterval(context);
    	 
    	 Log.i("IncomingCallReceiver",bundle.toString());
         
         String state = bundle.getString(TelephonyManager.EXTRA_STATE);
//         Log.i(LOG_TAG, state);
//         int status =Integer.parseInt(state);
//         switch(status){
//         case TelephonyManager.CALL_STATE_RINGING:
//              Log.i(LOG_TAG, "RINGING");
//              wasRinging = true;
//              break;
//         case TelephonyManager.CALL_STATE_OFFHOOK:
//              Log.i(LOG_TAG, "OFFHOOK");
//              attended=true;
//              if (!wasRinging) {
//                  // Start your new activity
//              } else {
//                  // Cancel your old activity
//              }
//
//              // this should be the last piece of code before the break
//              wasRinging = true;
//              break;
//         case TelephonyManager.CALL_STATE_IDLE:
//              Log.i(LOG_TAG, "IDLE");
//              // this should be the last piece of code before the break
//              
//              if(wasRinging && attended){
//            	  
//            	  Toast.makeText(context, "Picked", Toast.LENGTH_LONG).show();
//            	  Log.i(LOG_TAG, "Picked"); 
//              }
//              else if(wasRinging && !attended){
//            	  Toast.makeText(context, "Not Picked", Toast.LENGTH_LONG).show();
//            	  Log.i(LOG_TAG, "Not Picked"); 
//              }
//              wasRinging = true;
//              break;
//     }
                         
         Log.i("IncomingCallReceiver","State: "+ state);
         
         if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
         {
        	 
        	 
        	 savePreferences(context,"00",true); 
        	 Log.i("here",getFromPreferences(context).getBoolean("state")+"");
         }
         if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
         {
                 String phonenumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                 
                 savePreferences(context,phonenumber,false);
                 
                 connect = new DatabaseConnector(context);
 	     		
 		    	
 		    	String sub =  phonenumber;
 		    	long time = routine.getLogTime();
 				//boolean state = routine.getState();
 				
 		    	   
 	    		// if the contact is in cameleon prevent it from going to other sms applications
 	            if(connect.checkforContact(sub)>0 && time==1000){
 	            	
 	            	Log.i("number","iko");
 	            	
 	            	 Intent service = new Intent(context,
 	     					smsService.class);
 	                // service.putExtra("time",currentMessage.getTimestampMillis()+"");
 	                 service.putExtra("number",sub);
 	     			
 	     			context.startService(service);
 	     			getFellow(sub);
 	            	
 	            }else{
 	            	Log.i("number","haiko");
 	            }
                         
                 Log.i("IncomingCallReceiver","Incomng Number: " + phonenumber);
                 
                 String info = "Detect Calls sample application\nIncoming number: " + phonenumber;
                 
                // Toast.makeText(context, info, Toast.LENGTH_LONG).show();
         }
			
//			else{
//				return;
//			}

    	
    
//method to deletecall imediately

	  
//	  String status = extras.getString(TelephonyManager.EXTRA_STATE);
//
//	    if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(status)) {
//	    	 phone = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//	    	 
//		    	savePreferences(context,phone,false);
////	    	phone = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
////	    	savePreferences(context,phone,false);
////	    	
////	    	 Log.e("ringingh", getFromPreferences(context).getString("phone"));
//	    	
//	    	Log.e("state", "ringing");
//	   // return;
//	    }
//
//
//	     
//	    if(TelephonyManager.EXTRA_STATE_IDLE.equalsIgnoreCase(status)) {
//	    	 Log.e("idle", getFromPreferences(context).getString("phone"));
//	    	long time = routine.getLogTime();
//			boolean state = routine.getState();
//			Log.e("state", state+"");
//			if(time==1000){
//    	 //deleteCall();
//			}
//	    	
//	    	 Log.e("state", "iddle");
	    //return;
	    //}
//	     if(TelephonyManager.EXTRA_STATE_OFFHOOK.equalsIgnoreCase(status)){
////	    	 Log.e("off2", getFromPreferences(context).getString("phone"));
////	    	 savePreferences(context,getFromPreferences(context).getString("phone"),true);
////	    	 Log.e("off3", getFromPreferences(context).getString("phone"));
//	    	 Log.e("state", "offhook");
//	     //return;
//	     
//	     }
	     }
	     public void deleteCall(){
	    	 connect = new DatabaseConnector(context);
	     		connect.open();
		    	
		    	String sub =  getFromPreferences(context).getString("phone");
		    	   
	    		// if the contact is in cameleon prevent it from going to other sms applications
	            if(connect.checkforContact(sub)>0){
	            	
	            	 Intent service = new Intent(context,
	     					smsService.class);
	                // service.putExtra("time",currentMessage.getTimestampMillis()+"");
	                 service.putExtra("number",phone);
	     			
	     			context.startService(service);
	     			new LoadContacts().execute();
	            }
		    	
	    	 
	     }
	   
	     
	     public void getFellow(String num){
	    	 
	    	 DatabaseConnector dbConnector = new DatabaseConnector(context);
	    	 dbConnector.open();
		     	
	         Cursor result =dbConnector.getContactAlias(num);
	         
	         String queryString= "NUMBER='" + num+ "'";
		       Log.e("querry", queryString);
		         if(result.getCount()>0){
		        	context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null); 
		        	
		        	Log.e("service start", "started");
		        	Intent serviceIntent = new Intent(context, MyService.class);
		            context.startService(serviceIntent);
		            
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
  
  //loads contact information
	   private class LoadContacts extends AsyncTask<Long, Object, Cursor> 
	   {
	      DatabaseConnector dbConnector = new DatabaseConnector(context);
		private int i; 
	      @Override
	      protected Cursor doInBackground(Long... params)
	      {

	         dbConnector.open();
	     	
	         return dbConnector.getContactAlias(getFromPreferences(context).getString("phone"));
	      } 

	      @Override
	      protected void onPostExecute(Cursor result)
	      {
	         super.onPostExecute(result);
	         String queryString= "NUMBER='" + getFromPreferences(context).getString("phone") + "'";
	       Log.e("querry", queryString);
	         if(result.getCount()>0){
	        	context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null); 
	        	
	        	Log.e("service start", "started");
	        	Intent serviceIntent = new Intent(context, MyService.class);
	            context.startService(serviceIntent);
	            
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
		   boolean received = getFromPreferences(context).getBoolean("state");
		   int calltype;
		  
		   if(received){
			   calltype =CallLog.Calls.INCOMING_TYPE;
		   }
		   else {
			   calltype =CallLog.Calls.MISSED_TYPE;
		   }
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
	   public void savePreferences(Context aContext,String phonenum,boolean state){
	        SharedPreferences prefrences=aContext.getSharedPreferences("calldata", Context.MODE_PRIVATE);
	        Editor editor=prefrences.edit();
	        editor.putString("phone",phonenum);
	        Log.d("phone",phonenum );
	        editor.putBoolean("state",state);
	        editor.commit();
	    }
	   public static Bundle getFromPreferences(Context aContext){
	        SharedPreferences prefrences=aContext.getSharedPreferences("calldata", Context.MODE_PRIVATE);
	        Bundle data=new Bundle();
	        data.putString("phone",prefrences.getString("phone", "000"));
	        data.putBoolean("state",prefrences.getBoolean("state", false));
	        //String phn=  prefrences.getString("phone", "000");
	        
	        return data;
	    }
	   
}