package com.project.chameleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class TimeInterval {
	Context context;
	private MyService service;
	private long smstime;
	private long logtime;
	private boolean state;
	
	TimeInterval(Context cx){
		this.context =cx;
	
	// retrieve values from preferences
	SharedPreferences getData = PreferenceManager
			.getDefaultSharedPreferences(context);
	String values = getData.getString("log", "2"); // gets data from the log
	// array
	
	// options for log
	if (values.equals("1")) {
		setLogTime(1 * 1000);
	} else if (values.equals("2")) {
		setLogTime(300 * 1000);
	} else if (values.equals("3")) {
		setLogTime(1800 * 1000);
	} else if (values.equals("4")) {
		setLogTime(3600 * 1000);
	} else if (values.equals("5")) {
		setLogTime(86400 * 1000);
	} else if (values.equals("6")) {
		setLogTime(604800 * 000);
	} else {
		setLogTime(2419200 * 1000);
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
	state = getData.getBoolean("state", false);
//	if (deactivate == true) {
//		service = new MyService();
//		service.cancelRepeatingTimer();
//	} 
	
}


// sets time
public void setTime(long _time) {
	this.smstime = _time;
}
// sets time
public void setLogTime(long _time) {
	this.logtime=_time;
}

// gets time
public long getTime() {
	return this.smstime;
}
// gets time
public long getLogTime() {
	return this.logtime;
}

public boolean setState(boolean state) {
	return this.state;
}
public boolean getState() {
	return this.state;
}

// set deactivate checkbox to cancel alarm if checked and restart the
// service if unchecked again.

}
