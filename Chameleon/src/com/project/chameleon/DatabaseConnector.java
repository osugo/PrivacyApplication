package com.project.chameleon;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DatabaseConnector {

	/**
	 * Holds methods for the different operations to be done on the database
	 * The contacts database
	 */

	private static final String DB_NAME = "ContactsDB";
	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;

	// initialise
	public DatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context, DB_NAME, null, 4);
	}

	public void open() throws SQLException {
		// open database in reading/writing mode
		database = dbOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null)
			database.close();
	}

	// adds new contact
	public void insertContact(String fullName, String number, String alias,
			String aliasNum,String aliasMessage) {
		ContentValues newCon = new ContentValues();
		newCon.put("fname", fullName);
		newCon.put("num", number);
		newCon.put("sname", alias);
		newCon.put("altnum", aliasNum);
		newCon.put("altsms", aliasMessage);

		open();
		database.insert("contact", null, newCon);
		close();
	}

	// edits existing contact
	public void updateContact(long id, String fullName, String number,
			String alias, String aliasNum,String aliasMessage) {
		ContentValues editCon = new ContentValues();
		editCon.put("fname", fullName);
		editCon.put("num", number);
		editCon.put("sname", alias);
		editCon.put("altnum", aliasNum);
		editCon.put("altsms", aliasMessage);
		open();
		database.update("contact", editCon, "_id=" + id, null);
		close();
	}

	// returns all stored contacts
	public Cursor getAllContacts() {
		return database.query("contact", new String[] { "_id", DatabaseOpenHelper.KEY_NAME , 
				DatabaseOpenHelper.KEY_NUMBER, DatabaseOpenHelper.KEY_ALTNAME, DatabaseOpenHelper.KEY_ALTNUMBER}, null,
				null, null, null, "fname");
	}
	// returns all stored contacts numbers
	public ArrayList getAllContactsNumbers() {
		Cursor c =database.query("contact", new String[] { "num" }, null,
				null, null, null, "fname");
		
		List list=new ArrayList();
		
		 while (c.moveToNext()) {               
			 int num = c.getColumnIndex("num");
			 String number=c.getString(num);
			 
			 list.add(number);
             }
		 c.close();
		return (ArrayList) list;
	}

	// returns specific contact
	public Cursor getOneContact(long id) {
		return database.query("contact", null, "_id=" + id, null, null, null,
				null);
	}
	
	// returns specific contact
	public Cursor getContactAlias(String contact) {
		
		return database.query("contact", null, "num=?", new String[]{contact}, null, null,
				null);
	}

	// remove contact from database
	public void deleteContact(long id) {
		open();
		database.delete("contact", "_id=" + id, null);
		close();
	}

	// check the number of entries in the database
	public int getContactsCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + "contact";

		open();
		Cursor cursor = database.rawQuery(countQuery, null);

		if (cursor != null && !cursor.isClosed()) {
			count = cursor.getCount();
			cursor.close();
		}
		close();
		return count;
	}
	public int checkforContact(String contact) {
		//Log.i("seraching", contact);
		int count = 0;
		String countQuery = "SELECT  * FROM " + "contact WHERE num LIKE '%"+contact+"'";
		Log.i("query", countQuery);
		open();
		Cursor cursor = database.rawQuery(countQuery, null);
		Log.i("cursor size", cursor.getCount()+"");
		if (cursor != null && !cursor.isClosed()) {
			count = cursor.getCount();
			cursor.close();
		}
		Log.i("count", count+"");
		close();
		return count;
	}

}