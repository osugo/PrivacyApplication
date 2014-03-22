package com.project.chameleon;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class About extends Activity{
	
	private TextView response;
	private SlidingMenu slidingMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitleFromActivityLabel(R.id.title_text);
		
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		
		// Font path
		String fontPath = "fonts/MAIAN.TTF";
		// text view label
		TextView title = (TextView) findViewById(R.id.title_text);
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		// Applying font
		title.setTypeface(tf);
	}

	// sets the title of the page on the title bar
	private void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText("About Chameleon");
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
	
	//open feedback form from the sliding menu
	public void sendResponse(View v){
		startActivity(new Intent(this, Feedback.class));
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
	