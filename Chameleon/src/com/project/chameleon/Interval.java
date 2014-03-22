package com.project.chameleon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Interval extends PreferenceActivity {
	/**
	 * Time for which the service runs is user defined. The options for the time
	 * are stored in shared preferences for both the inbox and log activities.
	 * Values from the radio buttons are taken and set as the new times.
	 * The deactivate checkbox will stop the service until the user restarts it again
	 */
	long time;
	MyService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		// retrieve values from preferences
		SharedPreferences getData = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String values = getData.getString("log", "2"); // gets data from the log
		// array

		// options for log
		if (values.equals("1")) {
			setTime(1 * 1000);
		} else if (values.equals("2")) {
			setTime(300 * 1000);
		} else if (values.equals("3")) {
			setTime(1800 * 1000);
		} else if (values.equals("4")) {
			setTime(3600 * 1000);
		} else if (values.equals("5")) {
			setTime(86400 * 1000);
		} else if (values.equals("6")) {
			setTime(604800 * 000);
		} else {
			setTime(2419200 * 1000);
		}

		// options for inbox
		String ivalues = getData.getString("inbox", "1");
		if (ivalues.equals("1")) {
			setTime(60 * 1000);
		} else if (ivalues.equals("2")) {
			setTime(300 * 1000);
		} else if (ivalues.equals("3")) {
			setTime(1800 * 1000);
		} else if (ivalues.equals("4")) {
			setTime(3600 * 1000);
		} else if (ivalues.equals("5")) {
			setTime(86400 * 1000);
		} else if (ivalues.equals("6")) {
			setTime(604800 * 1000);
		} else {
			setTime(2419200 * 1000);
		}

		//when the checkbox is checked, the service stops thereby cancelling the alarm
		boolean deactivate = getData.getBoolean("state", false);
		if (deactivate == true) {
			service = new MyService();
			service.cancelRepeatingTimer();
		} 
	}


	// sets time
	public void setTime(long _time) {
		this.time = _time;
	}

	// gets time
	public long getTime() {
		return this.time;
	}

	// set deactivate checkbox to cancel alarm if checked and restart the
	// service if unchecked again.
}
