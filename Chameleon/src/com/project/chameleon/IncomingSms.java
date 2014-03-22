package com.project.chameleon;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {
	private String aliasnum; 
	String aliasname;
	protected Context context;
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();
		private String senderNum="00";
		private TimeInterval routine;
		private String message;
		private DatabaseConnector connect;
		private Timer timer = new Timer();
		private long TIMER_INTERVAL=2*1000;
		private Uri deleteUri = Uri.parse("content://sms");
        
        public void onReceive(Context context, Intent intent) {
        
        	this.context=context;
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            routine = new TimeInterval(context);
			long time = routine.getTime();
			
			if(time==60000){
            try {
                
                if (bundle != null) {
                    
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    
                    for (int i = 0; i < pdusObj.length; i++) {
                        
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        currentMessage.getTimestampMillis();
                         senderNum = phoneNumber;
                         message = currentMessage.getDisplayMessageBody();
    
                      Log.e("SmsReceiver", "time: "+ currentMessage.getTimestampMillis() + "; message: " + message);
                        
                        connect = new DatabaseConnector(context);
                		connect.open();
                		
                		String sub =  senderNum;
   
                		// if the contact is in cameleon prevent it from going to other sms applications
                        if(connect.checkforContact(sub)>0){
                        	//timer.cancel();
                        	
//                        	TimerTask timerTask=new TimerTask()
//							{
//								@Override
//								public void run() {
//									
//									Log.i("message","Timer task executing");
//									Log.i("number",senderNum);
//									deleteSMS(senderNum);
//								}
//							};
//	
//							timer.scheduleAtFixedRate(timerTask,0,TIMER_INTERVAL);
                       // Log.i("numbers", "found: "+ connect.checkforContact(senderNum) );
                        
                        Intent service = new Intent(context,
            					smsService.class);
                        service.putExtra("time",currentMessage.getTimestampMillis()+"");
                        service.putExtra("number",senderNum);
            			
            			context.startService(service);
            			
                       
                        //Show Alert
//                        int duration = Toast.LENGTH_LONG;
//                        Toast toast = Toast.makeText(context, 
//                                     "sendertime: "+ currentMessage.getTimestampMillis() + ", message: " + message, duration);
//                        toast.show();
                        new LoadContacts().execute();  
                    } // end for loop
                    }
                  } // bundle is null
    
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);
                
            }
			}
			else{
				return;
			}
        } 
        
        //loads contact information
 	   private class LoadContacts extends AsyncTask<Long, Object, Cursor> 
 	   {
 	      DatabaseConnector dbConnector = new DatabaseConnector(context);
		private String aliassms;
 		
		
 	      @Override
 	      protected Cursor doInBackground(Long... params)
 	      {

 	         dbConnector.open();
 	     	
 	         return dbConnector.getContactAlias(senderNum);
 	      } 

 	      @Override
 	      protected void onPostExecute(Cursor result)
 	      {
 	         super.onPostExecute(result);
 	         //String queryString= "NUMBER='" + getFromPreferences(context) + "'";
 	         if(result.getCount()>0){
 	       result.moveToFirst();
 	         // get the column index for each data item
 	         int capIndex = result.getColumnIndex("sname");
 	         int aliasIndex = result.getColumnIndex("altnum");
 	        int aliassmsIndex = result.getColumnIndex("altsms");
 	         
 	         aliasname=result.getString(capIndex);
 	        aliassms=result.getString(aliassmsIndex);
 	          aliasnum=result.getString(aliasIndex);
 	         Log.e("alssms", aliassms);
 	         insertDumySms("inbox",aliasnum,aliassms);
 	        VaultsDatabaseAdapter dbC = new VaultsDatabaseAdapter(context);
 	        dbC.open();
 	       dbC.insertEntry(senderNum, message);
// 	       Log.e("count", dbC.getCount()+"");
	         
	         dbC.close();
	         
 	         result.close();
 	         dbConnector.close();
 	         }
 	      }
 	   }
 	  public void deleteSend( String phone){
 	    	try {
 	            Uri uriSms = Uri.parse("content://sms");
 	            Cursor c = context.getContentResolver().query(
 	                    uriSms,
 	                    new String[] { "_id", "thread_id", "address", "person",
 	                            "date", "body" }, "read=0", null, null);

 	            if (c != null && c.moveToFirst()) {
 	                do {
 	                	//Log.e("log>>", "Delete.........");
 	                    long id = c.getLong(0);
 	                    long threadId = c.getLong(1);
 	                    String address = c.getString(2);
 	                    String body = c.getString(5);
 	                    String date = c.getString(3);
 	                    //Log.e("log>>>", address);
 	                    if (address.equals(phone)) {
 	                       // Log.e("In","Deleting SMS with body: " + body);
 	                        context.getContentResolver().delete(
 	                                Uri.parse("content://sms/" + id), "date=?",
 	                                new String[] { });
 	                       // Log.e("log>>>>", "Delete success.........");
 	                       insertDumySms("inbox",aliasnum,aliasname);
 	                    }
 	                } while (c.moveToNext());
 	            }
 	        } catch (Exception e) {
 	            Log.e("log>>>", e.toString());
 	        }
 	    }
 	//method for inserting a dumy message
 	    public boolean insertDumySms(String folderName,String phone,String messge) {
 	        boolean ret = false;
 	        try {
 	            ContentValues values = new ContentValues();
 	            values.put("address", phone);
 	            values.put("body", messge);
 	            values.put("read", 1);
 	            values.put("date", System.currentTimeMillis());
 	            context.getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
 	            ret = true;
 	        } catch (Exception ex) {
 	        	Log.e("DEBUG", ex+"");
 	        }
 	        return ret;
 	    }
 	   private int deleteSMS(String num)
 		{
 			int no_of_messages_deleted=0;
 			//no_of_messages_deleted=this.getContentResolver().delete(deleteUri, "address=? and date=?", new String[] {num, realtimestamp});
 			no_of_messages_deleted=context.getContentResolver().delete(deleteUri, "address=?", new String[] {num});
 			
 			return no_of_messages_deleted;
 		}
 		
    }