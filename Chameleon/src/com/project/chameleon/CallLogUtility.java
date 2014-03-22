package com.project.chameleon;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.CallLog;
import android.widget.Toast;

public class CallLogUtility {

	/**
	 * Handles addition of number to call log or deletion of number from call log
	 * Is to be called from the MyService class at the defined interval
	 * @param resolver
	 * @param strNum is the number from the database whose log is to be deleted
	 */

	//writes to call log
	/*public  void  AddNumToCallLog(ContentResolver resolver ,String strNum, int type, long timeInMiliSecond)
		{
			strNum = "";
	    	while(strNum.contains("-"))
			{
	    		strNum =strNum.substring(0,strNum.indexOf('-')) + strNum.substring(strNum.indexOf('-')+1,strNum.length());
			}
			ContentValues values = new ContentValues();
			values.put(CallLog.Calls.NUMBER, strNum);
			values.put(CallLog.Calls.DATE, timeInMiliSecond);
			values.put(CallLog.Calls.DURATION, 0);
			values.put(CallLog.Calls.TYPE, type);
			values.put(CallLog.Calls.NEW, 1);
			values.put(CallLog.Calls.CACHED_NAME, "");
			values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
			values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
			Log.d("AddToCallLog", "Inserting call log placeholder for " + strNum);

			if(null != resolver)
			{
				resolver.insert(CallLog.Calls.CONTENT_URI, values);
			}
			//getContentResolver().delete(url, where, selectionArgs)
		}*/

	//deletes from call log
	public void DeleteNumFromCallLog(ContentResolver resolver, String strNum)
	{
		try
		{
			String strUriCalls = "content://call_log/calls";
			Uri UriCalls = Uri.parse(strUriCalls);
			//Cursor c = res.query(UriCalls, null, null, null, null);
			if(null != resolver)
			{
				resolver.delete(UriCalls,CallLog.Calls.NUMBER +"=?",new String[]{ strNum});
				Toast.makeText(null, "Logs Deleted", Toast.LENGTH_SHORT).show();
			}
		}
		catch(Exception e) 
		{ 
			e.getMessage(); 
		} 
	}

	public void myDel(ArrayList<String> myStr){
		Iterator iterator = myStr.iterator();
		while( iterator.hasNext()){
			DeleteNumFromCallLog(null,(String) iterator.next() );
		}
	}
}

