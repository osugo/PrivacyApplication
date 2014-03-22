package com.project.chameleon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

//class needs modifications
public class SMSUtility {

	/**
	 * Deletes inbox
	 */

	String strNumber, Message_Body;

	public void Delete(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		SmsMessage[] messageString = null;

		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			messageString = new SmsMessage[pdus.length];

			for (int i = 0; i < messageString.length; i++) {
				messageString[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}

			strNumber = messageString[0].getOriginatingAddress(); // gets sender
																	// number
			Message_Body = messageString[0].getMessageBody(); // gets message
																// content

			Uri uri = Uri.parse("content://sms/inbox"); // access inbox
			Cursor cursor = context.getContentResolver().query(uri, null, null,
					null, null);
			cursor.moveToFirst();

			if (cursor.getCount() > 0) {
				int threadId = cursor.getInt(1);
				Log.d("Thread Id", threadId + " id - " + cursor.getInt(0));
				Log.d("contact number", cursor.getString(2));
				Log.d("coloumn name", cursor.getColumnName(2));

				context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadId),
						"address=?", new String[] { strNumber });
				Log.d("Message Thread Deleted", strNumber);
			}
			cursor.close();
		}

	}

}
