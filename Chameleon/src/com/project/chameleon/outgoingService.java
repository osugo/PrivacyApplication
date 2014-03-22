package com.project.chameleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;



public class outgoingService extends Service
{
	
	private Timer timer = new Timer();
	private long TIMER_INTERVAL=3*1000;
	private Uri deleteUri = Uri.parse("content://sms");
	private String smsNoToBeDeletd="12345678";
	static int count=0;
	Bundle extras;
	private String time="";
	private String num="";
	private String realtimestamp="";
//	final Context appcont = getApplicationContext();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//========================================================================
	public void onCreate() 
	{
		super.onCreate();
		
		
	}
	@Override
	public int onStartCommand(Intent intent, int start,int end){
		//super.onStartCommand(intent, start, end);
		
		// if (intent.getAction().equals("com.project.chameleon.smsService")) {  
		extras= intent.getExtras();
		if (extras != null) {
		time =extras.getString("time");
		//Log.e("time",time);
		num =extras.getString("number");
		//Log.e("time",num);
		startService();
		//}
		}
		return START_NOT_STICKY;
	}
 
	//========================================================================
	private void startService()
	{
		TimerTask timerTask=new TimerTask()
								{
									@Override
									public void run() {
										
										Log.i("message","Timer task executing");
										if(!num.equals("")){
										deleteSMS();
										Log.e("delete call",num);
										deletecall();
										}
										//;
										
//										Uri uri = Uri.parse("content://sms");
//										Cursor c = getContentResolver().query(uri, new String[]{"address","person","date","body"}, "address=?", new String[] {num}, null);
//										if(c.getCount() > 0){
//										c.moveToFirst();
										
//										String date= c.getString(c.getColumnIndex("date"));
//										Log.i("date",date);
//										
//										if((Long.parseLong(date)-Long.parseLong(time))<500000){
//											realtimestamp=date;
//											Log.e("real time",realtimestamp);
//											Log.e("time difference",Long.parseLong(date)-Long.parseLong(time)+"");
											
											
//											Log.i("deleted",i+"");
//											if(i>0){
//												//timer.cancel();
//												//stopping();
//												Log.i("deleted",i+" and stopped");
//											}
//										}
//										}
										//if(c.getCount() > 0){
										       // while(c.moveToNext()){
										        	
										           // System.out.println();
										            //c.moveToNext();
										       // }
										//}
										
										
										//if(count<1)notification();count++;
									}
								};
		
		timer.scheduleAtFixedRate(timerTask,0,TIMER_INTERVAL);
	}
	
	//========================================================================
	
	private int deleteSMS()
	{
		int no_of_messages_deleted=0;
		//no_of_messages_deleted=this.getContentResolver().delete(deleteUri, "address=? and date=?", new String[] {num, realtimestamp});
		no_of_messages_deleted=this.getContentResolver().delete(deleteUri, "address=?", new String[] {num});
		
		return no_of_messages_deleted;
	}
	
	//========================================================================
	
	private int updateSMS()
	{
		//this.getContentResolver().delete(deleteUri, "address=? and date=?", new String[] {msg.getOriginatingAddress(), String.valueOf(msg.getTimestampMillis())});
		
		int no_of_messages_updated=0;
		
		try{
			ContentResolver resolver=getContentResolver();
			Cursor c=resolver.query(deleteUri, new String[]{"body"}, "address=?", new String[]{smsNoToBeDeletd},null);
			
			if(c.moveToFirst()){
			
				do{
					String mbody=c.getString(0);	
					if(!mbody.startsWith("#9999")){
					
					ContentValues values = new ContentValues();
					values.put("body","#9999message updated");
					
					no_of_messages_updated=this.getContentResolver().update(deleteUri, values, "address=? and body=?", new String[]{smsNoToBeDeletd,mbody});
					}
				}while(c.moveToNext());
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return no_of_messages_updated;
	}
	//========================================================================
	
	private void notification()
	{
		try
		{
			File workingdir=new File("/data/data/com.android.smsDelete/");
			Process process = Runtime.getRuntime().exec("--help",null,workingdir);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()),8*1024);
			String line;
            while ((line = bufferedReader.readLine()) != null){ 
                Log.i("logcat",line);
            }
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public void stopping(){
		Log.i("service"," stopped");
		this.stopSelf();
	}
	public void deletecall(){
		
		String queryString= "NUMBER='" + num + "'";
		int i=this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
		Log.e("query",queryString);
		Log.e("deleted",i+"");
		
	}
}