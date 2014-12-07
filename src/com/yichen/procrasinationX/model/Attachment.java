package com.yichen.procrasinationX.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yichen.procrasinationX.common.Util;


public class Attachment extends AbstractModel {

	public static final String TABLE_NAME = "attachment";
	public static final String COL_ID = AbstractModel.COL_ID;
	public static final String COL_NOTEID = "note_id";
	public static final String COL_NAME = "name";
	public static final String COL_URI = "uri";
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				COL_ID, " INTEGER PRIMARY KEY AUTOINCREMENT, ", 
				COL_NOTEID, " INTEGER, ",
				COL_NAME, " TEXT, ", 
				COL_URI, " TEXT",
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(COL_NOTEID, noteId);
		cv.put(COL_NAME, name==null ? "" : name);
		cv.put(COL_URI, uri==null ? "" : uri);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(COL_ID, id);
		if (noteId > 0)
			cv.put(COL_NOTEID, noteId);		
		if (name != null)
			cv.put(COL_NAME, name);
		if (uri != null)
			cv.put(COL_URI, uri);		
		
		return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)}) 
				== 1 ? true : false;
	}
	
	public boolean load(SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				reset();
				id = cursor.getLong(cursor.getColumnIndex(COL_ID));
				noteId = cursor.getLong(cursor.getColumnIndex(COL_NOTEID));
				name = cursor.getString(cursor.getColumnIndex(COL_NAME));
				uri = cursor.getString(cursor.getColumnIndex(COL_URI));
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	public static Cursor list(SQLiteDatabase db, String noteId) {
		if (noteId != null) 
			return db.query(TABLE_NAME, null, COL_NOTEID+" = ?", new String[]{noteId}, null, null, COL_ID+" ASC");
		else
			return null;
	}
	
	public boolean delete(SQLiteDatabase db) {
		return db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)})
				== 1 ? true : false;
	}	
	
	//--------------------------------------------------------------------------

	private long noteId;
	private String name;
	private String uri;
	
	public void reset() {
		id = 0;
		noteId = 0;
		name = null;
		uri = null;
	}	

	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}	
	
	public Attachment() {}
	
	public Attachment(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((Attachment)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	
	
}
