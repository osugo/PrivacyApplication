package com.project.chameleon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	/**
	 * DatabaseOpenHelper for the contacts database
	 * @param context the database context
	 * @param name database name
	 * @param factory
	 * @param version database version
	 */
	
	public static final String KEY_NAME = "fname";
	public static final String KEY_NUMBER = "num";
	public static final String KEY_ALTNAME = "sname";
	public static final String KEY_ALTNUMBER = "altnum";
	public static final String KEY_ALTSMS = "altsms";

	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	//create the table in the database
	@Override
	public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE contact " + " (_id integer primary key autoincrement, " + KEY_NAME 
        		+ " TEXT NOT NULL, " + KEY_NUMBER + " TEXT NOT NULL, " + KEY_ALTNAME + " TEXT NOT NULL, " 
        		+ KEY_ALTNUMBER + " TEXT NOT NULL, "+ KEY_ALTSMS + " TEXT NULL );" ;                 
        db.execSQL(createQuery);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	if(newVersion == 2){
			db.execSQL("ALTER TABLE contact ADD COLUMN altsms text");
		}else{
			db.execSQL("DROP TABLE contact");
			onCreate(db);
		}
	}
}

