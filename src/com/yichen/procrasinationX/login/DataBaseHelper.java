package com.yichen.procrasinationX.login;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper
{
	public DataBaseHelper(Context context, String name,CursorFactory factory, int version) 
    {
	           super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase _db) 
	{
			_db.execSQL(LoginDataBaseAdapter.DATABASE_CREATE);
			
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) 
	{
			
			Log.w("TaskDBAdapter", "Upgrading from version " +_oldVersion + " to " +_newVersion + ", which will destroy all old data");
	
			_db.execSQL("DROP TABLE IF EXISTS " + "TEMPLATE");
			// Create a new one.
			onCreate(_db);
	}
	

}
