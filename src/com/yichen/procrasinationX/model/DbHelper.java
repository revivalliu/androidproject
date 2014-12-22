package com.yichen.procrasinationX.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yichen.procrasinationX.SmartPad;

public class DbHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "smartpad.db";
	public static final int DB_VERSION = 1;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Category.getSql());
		db.execSQL(Note.getSql());
		db.execSQL(Attachment.getSql());
		db.execSQL(CheckItem.getSql());
		
		populateData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Attachment.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CheckItem.TABLE_NAME);
		
		onCreate(db);		
	}
	
	private void populateData(SQLiteDatabase db) {
		
		// Public
		Category category = new Category();
		category.setName("Public");
		long categoryId = category.save(db);
		SmartPad.PUBLIC_CATEGORYID = categoryId;
		
		Note note = new Note();
		note.setCategoryId(categoryId);
		note.setTitle("About me");
		note.setType(Note.BASIC);
		note.setContent("This app allows you to easily manage task and clock reminder: specific features include quick notes, convenient check list, photo image, doodle manuscript, voice audio memo, voice recognition - speech to text, clock reminder, time count down, share function, location reminder, customized settings, sign-in functions and more. \n\nDon't forget to long press for more options. \n\nYichen ");
		note.save(db);
		
		// Personal
		category.reset();
		category.setName("Personal");
		category.setLocked(true);
		categoryId = category.save(db);
		
		note.reset();
		note.setCategoryId(categoryId);
		note.setTitle("To do");
		note.setType(Note.CHECKLIST); 
		long noteId = note.save(db);
		CheckItem ci = new CheckItem();
		ci.setNoteId(noteId);
		ci.setName("Set password in Settings page");
		ci.save(db);
		ci.reset();		
		ci.setNoteId(noteId);
		ci.setName("Rate this app.");
		ci.save(db);
		ci.reset();
		ci.setNoteId(noteId);
		ci.setName("Visit www.yichen.com");
		ci.save(db);
		
	}

}
