package com.project.chameleon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Sign in page
 * 
 * @author Root
 * 
 */

public class LoginPage extends Activity {

	private EditText passcode, confirm;
	private Button submit;
	private LoginDatabaseAdapter loginDatabaseAdapter;
	int count = 0;
	private DatabaseConnector connect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);

		// create a new database object and open it
		loginDatabaseAdapter = new LoginDatabaseAdapter(this);
		connect = new DatabaseConnector(this);
		connect.open();
		
		
		// initialise views
		setUpViews();
		setUpFonts();

		// check if an account has been created
		if (loginDatabaseAdapter.getCount() == 0) {
			submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// create new account
					signup();
				}
			});
		} else {
			// hide password confirm fields
			confirm.setVisibility(View.GONE);

			// change display text of button and textview
			passcode.setHint("Enter password:");
			submit.setText("Log in");

			submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					login();
				}
			});
		}
	}

	private void setUpViews() {
		// TODO Auto-generated method stub
		passcode = (EditText) findViewById(R.id.passcreate);
		confirm = (EditText) findViewById(R.id.passconfirm);
		submit = (Button) findViewById(R.id.submit);
		// head = (TextView)findViewById(R.id.header);
	}

	private void setUpFonts() {
		// Font path
		String fontPath = "fonts/MAIAN.TTF";
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		// Applying font
		/*
		 * passcode.setTypeface(tf); confirm.setTypeface(tf);
		 * submit.setTypeface(tf);
		 */
	}

	// to create new account
	protected void signup() {
		// TODO Auto-generated method stub

		String password = passcode.getText().toString();
		String confirmPassword = confirm.getText().toString();

		// check if any of the fields are vacant
		if (password.equals("") || confirmPassword.equals("")) {
			Toast.makeText(getApplicationContext(), "Field Vaccant",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check if both password matches
		if (!password.equals(confirmPassword)) {
			Toast.makeText(getApplicationContext(), "Password does not match",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			// Save the Data in Database
			loginDatabaseAdapter.open();
			loginDatabaseAdapter.insertEntry(password);
			Toast.makeText(getApplicationContext(),
					"Success! Use this password to log in.", Toast.LENGTH_SHORT)
					.show();

			// go to the home screen
			Intent start = new Intent(LoginPage.this, Contacts.class);
			startActivity(start);

		}
	}

	// sign in
	protected void login() {
		// TODO Auto-generated method stub
		// get entered password
		String password = passcode.getText().toString();

		// retrieves the password from database
		loginDatabaseAdapter.open();
		String storedPassword = loginDatabaseAdapter.getSingleEntry(password);

		// compares passwords and allows login if they match
		if (password.equals(storedPassword)) {
			Toast.makeText(LoginPage.this, "Login Successful",
					Toast.LENGTH_SHORT).show();

			if (connect.getContactsCount() == 0) {
				Intent intent = new Intent(LoginPage.this, Contacts.class);
				startActivity(intent);
			} else {
				Intent welcome = new Intent(LoginPage.this, VaultActivity.class);
				startActivity(welcome);
			}

		} else {
			Toast.makeText(LoginPage.this, "Incorrect password",
					Toast.LENGTH_LONG).show();
			count++;

			if (count == 3) {
				System.exit(0);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// close the database
		loginDatabaseAdapter.close();
		connect.close();
	}
}
