package com.example.testapp;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
public class DataBase extends SQLiteOpenHelper {
	
	Cursor c;
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "disasterAppUser";
 
    // Contacts table name
    private static final String TABLE_NAME = "user";
    private static final String TABLE_NAME2 = "message";
    
    private static final String KEY_DISASTER = "disaster_type";
    private static final String KEY_WORKER = "worker_type";
    
    private static final String KEY_MESSAGE = "msg_content";
    
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 //Creating table
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REGISTER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_DISASTER + " TEXT," + KEY_WORKER + " TEXT )";
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_NAME2 + "("
                + KEY_MESSAGE + " TEXT )";
        db.execSQL(CREATE_REGISTER_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
    }
    //upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        // Create tables again
        onCreate(db);
    }
    
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    
    public void addDetails(String dt,String wt) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_DISASTER, dt); //disaster type
        values.put(KEY_WORKER, wt);//worker type
        
 
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
	public String getUser()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
		c=db.rawQuery("SELECT * from user;", null);
		c.moveToFirst();
		db.close();
		return c.getString(c.getColumnIndex("worker_type"));
	}
	public String getDisasterType()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
		c=db.rawQuery("SELECT * from user;", null);
		c.moveToFirst();
		db.close();
		return c.getString(c.getColumnIndex("disaster_type"));
	}
	public boolean ifExists()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteStatement s = db.compileStatement( "select count(*) from user;" );
		long count = s.simpleQueryForLong();
		if(count>=1)
			return true;
		else
			return false;
	}
	public void deleteDetails()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		//db.delete(TABLE_NAME, KEY_WORKER+"="+w, null);
		db.execSQL("delete from "+TABLE_NAME);
	}
	public void addMsg(String msg)
	{
		 SQLiteDatabase db = this.getWritableDatabase();
		 
	     ContentValues values = new ContentValues();
	     values.put(KEY_MESSAGE, msg); //message
	     db.insert(TABLE_NAME2, null, values);
	     db.close(); // Closing database connection
	}
}
