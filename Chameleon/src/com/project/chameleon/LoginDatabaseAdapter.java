package com.project.chameleon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoginDatabaseAdapter 
{
	/**
	 * Create and manage the password database(login credentials)
	 */
	static final String DATABASE_NAME = "login.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 1;

	// TODO: Create public field for each column in your table.
	// SQL Statement to create a new database.
	static final String DATABASE_CREATE = "create table "+"LOGIN"+
			"( " +"ID"+" integer primary key autoincrement,"+ "PASSWORD text); ";

	// Variable to hold the database instance
	public  SQLiteDatabase db;

	// Context of the application using the database.
	private final Context context;

	// Database open/upgrade helper
	private DataBaseHelper dbHelper;

	//set database context
	public  LoginDatabaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public  LoginDatabaseAdapter open() throws SQLException 
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

	public void insertEntry(String password)
	{
		ContentValues newValues = new ContentValues();
		// Assign values for each row.

		newValues.put("PASSWORD",password);

		// Insert the row into your table
		db.insert("LOGIN", null, newValues);

	}

	public int deleteEntry(String PassWord)
	{

		String where="PASSWORD=?";
		int numberOFEntriesDeleted= db.delete("LOGIN", where, new String[]{PassWord}) ;

		return numberOFEntriesDeleted;
	}	

	public String getSingleEntry(String PassWord)
	{
		Cursor cursor=db.query("LOGIN", null, " PASSWORD=?", new String[]{PassWord}, null, null, null);
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

	public void updateEntry(String password)
	{
		// Define the updated row content.
		ContentValues updatedValues = new ContentValues();
		// Assign values for each row.
		updatedValues.put("PASSWORD",password);

		String where="PASSWORD = ?";
		db.update("LOGIN",updatedValues, where, new String[]{password});			   
	}	
	
	public int getCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + "LOGIN";

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


