package com.project.chameleon;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Contacts extends ListActivity {

	/**
	 * Shows all the contacts stored in the database. These are the numbers
	 * whose logs and inbox entries are to be deleted. This is the main page
	 * displayed after sign in.
	 */
	
	public static final String ROW_ID = "row_id";
	private ListView conListView;
	private CursorAdapter conAdapter;
	private SlidingMenu slidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycontacts);
		setTitleFromActivityLabel(R.id.title_text);
		
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		conListView = (ListView) findViewById(android.R.id.list);
		conListView.setOnItemClickListener(viewConListener);
		
		 String fontPath = "fonts/MAIAN.TTF";
	        // text view label
	        TextView title = (TextView) findViewById(R.id.title_text);
	        // Loading Font Face
	        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
	        // Applying font
	        title.setTypeface(tf);

		// map each name to a TextView
		String[] from = new String[] { DatabaseOpenHelper.KEY_NAME,
				DatabaseOpenHelper.KEY_NUMBER, DatabaseOpenHelper.KEY_ALTNAME,
				DatabaseOpenHelper.KEY_ALTNUMBER }; // name displayed
		int[] to = new int[] { R.id.actualName, R.id.actualNumber,
				R.id.aliasName, R.id.aliasNumber };
		
		conAdapter = new SimpleCursorAdapter(Contacts.this, R.layout.list_row,
				null, from, to);
		setListAdapter(conAdapter); // set adapter
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetContacts().execute((Object[]) null);
	}

	@Override
	protected void onStop() {
		Cursor cursor = conAdapter.getCursor();

		if (cursor != null)
			cursor.deactivate();

		conAdapter.changeCursor(null);
		super.onStop();
	}

	// retrieves contacts from database
	private class GetContacts extends AsyncTask<Object, Object, Cursor> {
		DatabaseConnector dbConnector = new DatabaseConnector(Contacts.this);

		@Override
		protected Cursor doInBackground(Object... params) {
			dbConnector.open();
			return dbConnector.getAllContacts();
		}

		@Override
		protected void onPostExecute(Cursor result) {
			conAdapter.changeCursor(result); // set the adapter's Cursor
			dbConnector.close();
		}
	}

	// clicking on a contact allows you to view its details
	// with the option of editing it or deleting it
	OnItemClickListener viewConListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent viewCon = new Intent(Contacts.this, ViewContact.class); 
			viewCon.putExtra(ROW_ID, arg3);
			startActivity(viewCon);
		}
	};

	public void onClickAdd(View v) {
		Intent about = new Intent(Contacts.this, AddEditContact.class);
		startActivity(about);
	}
	
	// sets the title of the page on the title bar
		private void setTitleFromActivityLabel(int textViewId) {
			TextView tv = (TextView) findViewById(textViewId);
			if (tv != null)
				tv.setText("My Contacts");
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
