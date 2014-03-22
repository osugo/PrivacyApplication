package com.project.chameleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class LogActivity extends Activity{

	/**
	 * Prompt showing the log feature is unavailable for this version
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//set up dialog
		final AlertDialog log = new AlertDialog.Builder(LogActivity.this).create();
		//title of dialog
		log.setTitle(R.string.logTitle);
		//set text to be displayed
		log.setMessage("This feature is only available on the pro version.");
		
		//set upgrade button
		log.setButton("Upgrade", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//set cancel button
		log.setButton2("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		log.show();
	}
	
}

