package com.project.chameleon;
 
 
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class VaultActivity extends Activity {
 
 private VaultsDatabaseAdapter dbHelper;
 private SimpleCursorAdapter dataAdapter;
 private SlidingMenu slidingMenu;
 
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.conf_texts);
  
 // setTitleFromActivityLabel(R.id.title_text);

	slidingMenu = new SlidingMenu(this);
	slidingMenu.setMode(SlidingMenu.LEFT);
	slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
	slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
	slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	slidingMenu.setFadeDegree(0.35f);
	slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	slidingMenu.setMenu(R.layout.slidingmenu);
	
	String fontPath = "fonts/MAIAN.TTF";
	// text view label
	TextView title = (TextView) findViewById(R.id.title_text);
	// Loading Font Face
	Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
	// Applying font
	title.setTypeface(tf);
 
  dbHelper = new VaultsDatabaseAdapter(this);
  dbHelper.open();
 
  //Clean all data
  //dbHelper.deleteAllCountries();
  //Add some data
  //dbHelper.getAllMessages();
 
  //Generate ListView from SQLite Database
  displayListView();
 
 }
 
 private void displayListView() {
 
 
  Cursor cursor = dbHelper.getAllMessages();
  // The desired columns to be bound
  String[] columns = new String[] {"NAME","MESSAGE"
  };
 
  // the XML defined views which the data will be bound to
  int[] to = new int[] { 
    R.id.smsNum,R.id.smsText
  };
 
  // create the adapter using the cursor pointing to the desired data 
  //as well as the layout information
  dataAdapter = new SimpleCursorAdapter(
    this, R.layout.msg_row, 
    cursor, 
    columns, 
    to);
 
  ListView listView = (ListView) findViewById(android.R.id.list);
  // Assign adapter to ListView
  
	  listView.setAdapter(dataAdapter);
  dbHelper.close();
  listView.setOnItemClickListener(new OnItemClickListener() {
   @Override
   public void onItemClick(AdapterView<?> listView, View view, 
     int position, long id) {
   // Get the cursor, positioned to the corresponding row in the result set
   //Cursor cursor = (Cursor) listView.getItemAtPosition(position);
 
   // Get the state's capital from this row in the database.
   }
  });
 

 }
//sets the title of the page on the title bar
	private void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText("Vault");
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//open feedback form from the sliding menu
		public void sendResponse(View v){
			startActivity(new Intent(this, Feedback.class));
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