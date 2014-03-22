package com.project.chameleon;
import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ViewContact extends Activity {

	/**
	 * Page containing the contact details, the name, alias name if any and
	 * number. Menu offers options for editing or deleting the contact
	 */

	private long rowID;
	private EditText fnameEt;
	private EditText snameEt;
	private EditText numEt;
	private EditText aliasEt;
	private Button alias;
	private Button phonebook;
	private Button submission;
	DatabaseConnector connect;
	private final int PICK = 10;
	private final int ADD = 100;
	private EditText aliasmessageTV;
	private SlidingMenu slidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addedit);
		setTitleFromActivityLabel(R.id.title_text);
		
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		setViews();
		setFonts();

		// create a database object and open it
		connect = new DatabaseConnector(this);
		connect.open();

		// gets stored data
		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(Contacts.ROW_ID);

		phonebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, ADD);
			}
		});

		alias.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK);
			}
		});

		submission.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
							Toast.makeText(ViewContact.this,
									"Chameleon contact saved",
									Toast.LENGTH_SHORT).show();
							finish();
						}
					};

					saveContactTask.execute((Object[]) null);
				}

				// shown when one of the required fields is missing
				else {
					final Dialog log = new Dialog(ViewContact.this);
					log.setContentView(R.layout.empty);
					log.setTitle("Error");

					TextView errorMsg = (TextView) log.findViewById(R.id.body);
					Button ok = (Button) log.findViewById(R.id.okay);

					errorMsg.setBackgroundColor(Color.WHITE);
					errorMsg.setText("Some fields are missing. Make sure all fields are filled to proceed.");

					ok.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							log.dismiss();
						}
					});
				}
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

	private void setViews() {
		// TODO Auto-generated method stub
		// set up view and buttons
		fnameEt = (EditText) findViewById(R.id.fnameEdit);
		numEt = (EditText) findViewById(R.id.numEdit);
		snameEt = (EditText) findViewById(R.id.snameEdit);
		aliasEt = (EditText) findViewById(R.id.aliasNum);
		aliasmessageTV = (EditText) findViewById(R.id.aliasMessagefield);
		phonebook = (Button) findViewById(R.id.from_phonebook);
		alias = (Button) findViewById(R.id.pbook);
		submission = (Button) findViewById(R.id.saveBtn);
	}

	// stores the new or updated contact to the database
	private void saveContact() {
		DatabaseConnector dbConnector = new DatabaseConnector(this);

		if (getIntent().getExtras() == null) {
			dbConnector.insertContact(fnameEt.getText().toString(), numEt
					.getText().toString(), snameEt.getText().toString(),
					aliasEt.getText().toString(),aliasmessageTV.getText().toString());

		} else {
			dbConnector.updateContact(rowID, fnameEt.getText().toString(),
					numEt.getText().toString(), snameEt.getText().toString(),
					aliasEt.getText().toString(),aliasmessageTV.getText().toString());

		}
	}
	
	// checks if the contact exists in the user's phonebook and adds it if it
		// doesnt
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflate = getMenuInflater();
		menuInflate.inflate(R.menu.delete_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.remove:
			/*
			 * Intent i = new Intent(ViewContact.this, DeleteDialog.class);
			 * startActivityForResult(i, DELETE_REQUEST);
			 */
			deleteContact();
			break;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadContacts().execute(rowID); // gets contact details
	}

	// loads contact information
	private class LoadContacts extends AsyncTask<Long, Object, Cursor> {
		DatabaseConnector dbConnector = new DatabaseConnector(ViewContact.this);

		@Override
		protected Cursor doInBackground(Long... params) {
			dbConnector.open();
			return dbConnector.getOneContact(params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);

			result.moveToFirst();
			// get the column index for each data item
			int nameIndex = result.getColumnIndex("fname");
			int capIndex = result.getColumnIndex("sname");
			int codeIndex = result.getColumnIndex("num");
			int aliasIndex = result.getColumnIndex("altnum");
			int aliassmsIndex = result.getColumnIndex("altsms");

			fnameEt.setText(result.getString(nameIndex));
			snameEt.setText(result.getString(capIndex));
			numEt.setText(result.getString(codeIndex));
			aliasEt.setText(result.getString(aliasIndex));
			aliasmessageTV.setText(result.getString(aliassmsIndex));

			result.close();
			dbConnector.close();
		}
	}

	// prompts user to confirm deletion choice or abort
	private void deleteContact() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_box);
		dialog.setTitle("Confirm Action");

		TextView message = (TextView) dialog.findViewById(R.id.body);
		message.setText("This will permanently remove the entry!");
		message.setBackgroundColor(Color.WHITE);
		Button remover = (Button) dialog.findViewById(R.id.away);
		Button hide = (Button) dialog.findViewById(R.id.close);

		remover.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final DatabaseConnector dbConnector = new DatabaseConnector(
						ViewContact.this);

				AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
					@Override
					protected Object doInBackground(Long... params) {
						dbConnector.deleteContact(params[0]);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						Toast.makeText(ViewContact.this, "Contact deleted",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						finish();
					}
				};
				deleteTask.execute(new Long[] { rowID });
			}
		});

		hide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
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

				String cname = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(c.getString(c
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(Phone.CONTENT_URI, null,
							Phone.CONTACT_ID + " = ?", new String[] { id },
							null);

					while (pCur.moveToNext()) {
						String cnumber = pCur.getString(pCur
								.getColumnIndex(Phone.NUMBER));

						// differentiate between the two request so as to
						// populate the edit texts respectively
						switch (requestCode) {
						case ADD:
							fnameEt.setText(cname);
							numEt.setText(cnumber);
							break;

						case PICK:
							snameEt.setText(cname);
							aliasEt.setText(cnumber);
							break;
						}
					}
				}
			}
		}
	}

	public void onClickAdd(View v) {
		Intent about = new Intent(ViewContact.this, AddEditContact.class);
		startActivity(about);
	}

	
	// sets the title of the page on the title bar
	private void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText("Contacts");
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
		
		//open feedback form from the sliding menu
		public void sendResponse(View v){
			startActivity(new Intent(this, Feedback.class));
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

	/*	@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				this.slidingMenu.toggle();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		} */

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// close the database
		connect.close();
	}
}
