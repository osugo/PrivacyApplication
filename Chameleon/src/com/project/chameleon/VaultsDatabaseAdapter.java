package com.project.chameleon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class VaultsDatabaseAdapter 
{
	/**
	 * Create and manage the password database(login credentials)
	 */
	static final String DATABASE_NAME = "vault.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 1;

	// TODO: Create public field for each column in your table.
	// SQL Statement to create a new database.
	static final String DATABASE_CREATE = "create table "+"VAULT"+
			"( " +"ID"+" integer primary key autoincrement,"+ "NAME text,"+ "MESSAGE text); ";

	// Variable to hold the database instance
	public  SQLiteDatabase db;

	// Context of the application using the database.
	private final Context context;

	// Database open/upgrade helper
	private DatabaseMessagesHelper dbHelper;

	//set database context
	public  VaultsDatabaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new DatabaseMessagesHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public  VaultsDatabaseAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() 
	{
		db.close();
	}

	//SQLite instance
	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}

	public void insertEntry(String name,String message)
	{
		ContentValues newValues = new ContentValues();
		// Assign values for each row.

		newValues.put("NAME",name);
		newValues.put("MESSAGE",message);

		// Insert the row into your table
		db.insert("VAULT", null, newValues);

	}

	public int deleteEntry(String id)
	{

		String where="ID=?";
		int numberOFEntriesDeleted= db.delete("VAULT", where, new String[]{id}) ;

		return numberOFEntriesDeleted;
	}	

	public String getSingleEntry(String id)
	{
		Cursor cursor=db.query("VAULT", null, " ID=?", new String[]{id}, null, null, null);
		if(cursor.getCount()<1) // PassWord Not Exist
		{
			cursor.close();
			return "NOT EXIST";
		}
		cursor.moveToFirst();
		String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
		cursor.close();
		return password;				
	}
	// returns all stored messages
	public Cursor getAllMessages() {
		return db.query("VAULT", new String[] {"ID as _id","NAME", "MESSAGE" }, null,
				null, null, null, "ID");
	}
	public int getCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + "VAULT";

		open();
		Cursor cursor = db.rawQuery(countQuery, null);

		if (cursor != null && !cursor.isClosed()) {
			count = cursor.getCount();
			cursor.close();
		}
		close();
		return count;
	}
}


