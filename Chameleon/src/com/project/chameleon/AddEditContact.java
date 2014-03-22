package com.project.chameleon;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditContact extends Activity {

	/**
	 * View selected contact and edit it.
	 */

	private long rowID;
	private EditText fnameEt;
	private EditText snameEt;
	private EditText numEt;
	private EditText aliasEt;
	private EditText aliamessagesEt;
	private Button phonebook;
	private Button phonebook_contact;
	private SlidingMenu slidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addedit);
		setTitleFromActivityLabel (R.id.title_text);
		
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		fnameEt = (EditText) findViewById(R.id.fnameEdit);
		snameEt = (EditText) findViewById(R.id.snameEdit);
		numEt = (EditText) findViewById(R.id.numEdit);
		aliasEt = (EditText) findViewById(R.id.aliasNum);
		aliamessagesEt = (EditText) findViewById(R.id.aliasMessagefield);
		phonebook_contact = (Button) findViewById(R.id.from_phonebook);
		phonebook = (Button) findViewById(R.id.pbook);
		
		setFonts();

		// get data
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			rowID = extras.getLong("row_id");
			fnameEt.setText(extras.getString("fname"));
			snameEt.setText(extras.getString("sname"));
			numEt.setText(extras.getString("num"));
			aliasEt.setText(extras.getString("altnum"));
			aliamessagesEt.setText(extras.getString("altsms"));
		}

		Button saveButton = (Button) findViewById(R.id.saveBtn);
		saveButton.setOnClickListener(new OnClickListener() {

			// save new contact
			public void onClick(View v) {
				if (fnameEt.getText().length() != 0
						&& numEt.getText().toString().length() != 0
						&& snameEt.getText().toString().length() != 0
						&& aliasEt.getText().toString().length() != 0) {
					AsyncTask<Object, Object, Object> saveContactTask = new AsyncTask<Object, Object, Object>() {
						@Override
						protected Object doInBackground(Object... params) {
							saveContact();
							createContact(snameEt.getText().toString(), aliasEt
									.getText().toString());
							createContact(fnameEt.getText().toString(), numEt
									.getText().toString());
							return null;
						}

						@Override
						protected void onPostExecute(Object result) {
							Toast.makeText(AddEditContact.this,
									"Chameleon contact saved",
									Toast.LENGTH_SHORT).show();
							finish();
						}
					};

					saveContactTask.execute((Object[]) null);
				}

				// shown when one of the required fields is missing
				else {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							AddEditContact.this);
					alert.setTitle(R.string.errorTitle);
					alert.setMessage(R.string.errorMessage);
					alert.setPositiveButton(R.string.errorButton, null);
					alert.show();
				}
			}
		});

		// option to add alias contact details from the phonebook
		phonebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 100);
			}
		});
		// option to add alias contact details from the phonebook
		phonebook_contact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 101);
			}
		});
	}
	
	private void setFonts() {
		// TODO Auto-generated method stub
		// Font path
		String fontPath = "fonts/MAIAN.TTF";
		// text view label
		TextView title = (TextView) findViewById(R.id.title_text);
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		// Applying font
		title.setTypeface(tf);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
		
		Uri contact = data.getData();
		ContentResolver cr = getContentResolver();

		Cursor c = managedQuery(contact, null, null, null, null);
		// c.moveToFirst();

		while (c.moveToNext()) {
			String id = c.getString(c
					.getColumnIndex(ContactsContract.Contacts._ID));

			String name = c.getString(c
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if (Integer
					.parseInt(c.getString(c
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				Cursor pCur = cr.query(Phone.CONTENT_URI, null,
						Phone.CONTACT_ID + " = ?", new String[] { id }, null);
				if(requestCode==100){
				while (pCur.moveToNext()) {
					String phone = pCur.getString(pCur
							.getColumnIndex(Phone.NUMBER));
					snameEt.setText(name);
					aliasEt.setText(phone.replace(" ", "").replace("-", ""));
				}
				}else {
					while (pCur.moveToNext()) {
						String phone = pCur.getString(pCur
								.getColumnIndex(Phone.NUMBER));
						fnameEt.setText(name);
						numEt.setText(phone.replace(" ", "").replace("-", ""));
				}
			}
			}
		}
		}
	}

	// stores the new or updated contact to the database
	private void saveContact() {
		DatabaseConnector dbConnector = new DatabaseConnector(this);

		if (getIntent().getExtras() == null) {
			dbConnector.insertContact(fnameEt.getText().toString(), numEt
					.getText().toString(), snameEt.getText().toString(),
					aliasEt.getText().toString(),aliamessagesEt.getText().toString());

		} else {
			dbConnector.updateContact(rowID, fnameEt.getText().toString(),
					numEt.getText().toString(), snameEt.getText().toString(),
					aliasEt.getText().toString(),aliamessagesEt.getText().toString());

		}
	}

	// checks if the contacts exist in the user's phonebook and adds them if
	// they dont
	private void createContact(String name, String phone) {
		ContentResolver cr = getContentResolver();

		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String existName = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (existName.contains(name)) {
					// the number will not be added if it already exists in the
					// phone book
					return;
				}
			}
		}

		// add the contact to phone book if it doesnt exist
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,
						"accountname@gmail.com")
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME,
						"com.google").build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
						name).build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
				.build());

		try {
			cr.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void setTitleFromActivityLabel (int textViewId)
	{
	    TextView tv = (TextView) findViewById (textViewId);
	    if (tv != null) 
	    	tv.setText ("Contacts");
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
}
