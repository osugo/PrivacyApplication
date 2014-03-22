package com.project.chameleon;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Settings extends PreferenceActivity {
	/**
	 * Time for which the service runs is user defined. The options for the time
	 * are stored in shared preferences for both the inbox and log activities.
	 * Values from the radio buttons are taken and set as the new times. The
	 * deactivate checkbox will stop the service until the user restarts it
	 * again
	 */
	long time;
	MyService service;
	private ListView list;
	private SlidingMenu slidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		setContentView(R.layout.pref_list);
		setTitleFromActivityLabel(R.id.title_text);

		//initialise sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		list = (ListView) findViewById(android.R.id.list);
		
		// Font path
        String fontPath = "fonts/MAIAN.TTF";
        // text view label
        TextView title = (TextView) findViewById(R.id.title_text);
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        // Applying font
        title.setTypeface(tf);

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

		// when the checkbox is checked, the service stops thereby cancelling
		// the alarm
		boolean deactivate = getData.getBoolean("state", false);
//		if (deactivate == true) {
//			service = new MyService();
//			service.cancelRepeatingTimer();
//		}
	}

	// sets time
	public void setTime(long _time) {
		this.time = _time;
	}

	// gets time
	public long getTime() {
		return this.time;
	}

	// sets the title of the page on the title bar
	private void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText("Preferences");
	}

	// start contacts activity from sliding menu
	public void seeContacts(View v) {
		startActivity(new Intent(this, Contacts.class));
	}

	// start vault activity from sliding menu
	public void privateMessages(View v) {
		startActivity(new Intent(this, VaultActivity.class));
	}

	// start settings activity from sliding menu
	public void Customise(View v) {
		startActivity(new Intent(this, Settings.class));
	}

	// start about activity from sliding menu
	public void appInfo(View v) {
		startActivity(new Intent(this, About.class));
	}

	// show/hide menu when home icon is pressed
	public void onClickHome(View v) {
		slidingMenu.toggle();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.toggle();
		} else {
			super.onBackPressed();
		}
	}
	//open feedback form from the sliding menu
		public void sendResponse(View v){
			startActivity(new Intent(this, Feedback.class));
		}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
