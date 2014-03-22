package com.project.chameleon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity {

	private RelativeLayout friends;
	private RelativeLayout messages;
	private RelativeLayout tweak;
	private RelativeLayout know;
	private TextView friendsTitle;
	private TextView friendsDesc;
	private TextView messagesTitle;
	private TextView messagesDesc;
	private TextView tweakTitle;
	private TextView tweakDesc;
	private TextView knowTitle;
	private TextView knowDesc;
	private TextView App;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);

		setViews();
		setFonts();
		setListeners();
	}

	private void setViews() {
		// TODO Auto-generated method stub
		friends = (RelativeLayout) findViewById(R.id.people);
		friendsTitle = (TextView) findViewById(R.id.ctstitle);
		friendsDesc = (TextView) findViewById(R.id.ctsexp);
		messages = (RelativeLayout) findViewById(R.id.texts);
		messagesTitle = (TextView) findViewById(R.id.txttitle);
		messagesDesc = (TextView) findViewById(R.id.txtexp);
		tweak = (RelativeLayout) findViewById(R.id.preferences);
		tweakTitle = (TextView) findViewById(R.id.setstitle);
		tweakDesc = (TextView) findViewById(R.id.setsexp);
		know = (RelativeLayout) findViewById(R.id.application);
		knowTitle = (TextView) findViewById(R.id.infotitle);
		knowDesc = (TextView) findViewById(R.id.infoexp);
		App = (TextView)findViewById(R.id.appname);
	}

	private void setFonts() {
		// TODO Auto-generated method stub
		// Font path
		String fontPath = "fonts/ARLRDBD.TTF";
		String font = "fonts/MAIAN.TTF";
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		Typeface tp = Typeface.createFromAsset(getAssets(), font);
		// Applying font
		friendsTitle.setTypeface(tf);
		friendsDesc.setTypeface(tf);
		messagesTitle.setTypeface(tf);
		messagesDesc.setTypeface(tf);
		tweakTitle.setTypeface(tf);
		tweakDesc.setTypeface(tf);
		knowTitle.setTypeface(tf);
		knowDesc.setTypeface(tf);
		App.setTypeface(tp);
	}

	private void setListeners() {
		// TODO Auto-generated method stub
		Press press = new Press();
		friends.setOnClickListener(press);
		tweak.setOnClickListener(press);
		know.setOnClickListener(press);
		messages.setOnClickListener(press);
	}

	private class Press implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.people:
				Intent contacts = new Intent(Home.this, Contacts.class);
				startActivity(contacts);
				break;

			case R.id.preferences:
				Intent configure = new Intent(Home.this, Settings.class);
				startActivity(configure);
				break;

			case R.id.texts:
				 VaultsDatabaseAdapter dbC = new VaultsDatabaseAdapter(Home.this);
		 	        dbC.open();
		 	      
		 	        if(dbC.getCount()>0){
		 	        	Intent messages = new Intent(Home.this, VaultActivity.class);
						startActivity(messages);	
		 	        }
		 	        else{
		 	        	int duration = Toast.LENGTH_LONG;
                  Toast toast = Toast.makeText(Home.this, 
                              "No private messages", duration);
                   toast.show();
		 	        }
			         
			         dbC.close();
			        
				
				break;

			case R.id.application:
				Intent app = new Intent(Home.this, About.class);
				startActivity(app);
				break;
			}
		}
	}
}