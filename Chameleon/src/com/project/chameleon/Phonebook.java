package com.project.chameleon;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.EditText;
import android.widget.Toast;

public class Phonebook extends Activity{

	/**
	 * Adds contacts directly from phonebook to the database
	 */
	public static final int PICK_CONTACT = 1;
	DatabaseConnector dbConnector;
	EditText cname, cnumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get instance if the database
		dbConnector = new DatabaseConnector(this);
		//open the database
		dbConnector.open();

		//open phonebook
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK){
			switch(requestCode){
			case PICK_CONTACT:
				Uri contact = data.getData();
				ContentResolver cr = getContentResolver();

				//query the contacts uri
				Cursor c = managedQuery(contact, null, null, null, null);

				while(c.moveToNext()){
					//get row id
					String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

					//get contact name
					String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr.query(Phone.CONTENT_URI,null,Phone.CONTACT_ID +" = ?", new String[]{id}, null);

						while(pCur.moveToNext()){
							//get contact number
							String phone = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
							
							//remove any spaces or - in the number
							phone=phone.replace(" ", "");
							phone= phone.replace("-", "");
							cname = new EditText(this);
							//set name to an edittext to get even sim and google contacts
							cname.setText(name);

							cnumber = new EditText(this);
							//set number to edittext to get all including sim and google contacts
							cnumber.setText(phone);

							//add the contact details to the database
							dbConnector.insertContact(cname.getText().toString(), cnumber.getText().toString(), null, null,null);
						}
						Toast.makeText(Phonebook.this, "Chameleon contact saved", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			}
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbConnector.close();
	}
}