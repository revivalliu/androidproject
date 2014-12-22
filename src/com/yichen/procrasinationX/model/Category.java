package com.yichen.procrasinationX.model;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yichen.procrasinationX.SmartPad;
import com.yichen.procrasinationX.common.Util;

public class Category extends BaseModel {
	
	public static final String TABLE_NAME = "category";
	public static final String COL_ID = BaseModel.COL_ID;
	public static final String COL_CREATEDTIME = BaseModel.COL_CREATEDTIME;
	public static final String COL_MODIFIEDTIME = BaseModel.COL_MODIFIEDTIME;
	public static final String COL_LOCKED = BaseModel.COL_LOCKED;
	public static final String COL_NAME = "name";
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				BaseModel.getSql(),  
				COL_NAME, " TEXT", 
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.save(cv);
		cv.put(COL_NAME, name==null ? "" : name);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.update(cv);
		if (name != null)
			cv.put(COL_NAME, name);
		
		return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)}) 
				== 1 ? true : false;
	}	
	
	public boolean load(SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				reset();
				super.load(cursor);
				name = cursor.getString(cursor.getColumnIndex(COL_NAME));
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	public static Cursor list(SQLiteDatabase db) {
		String[] columns = {COL_ID, COL_LOCKED, COL_NAME};
		String selection = !SmartPad.showLocked() ? COL_LOCKED+" <> 1" : null;
		
		return db.query(TABLE_NAME, columns, selection, null, null, null, COL_CREATEDTIME+" ASC");
	}
	
	public boolean delete(SQLiteDatabase db) {
		boolean status = false;
		String[] whereArgs = new String[]{String.valueOf(id)};
		String whereClause = Util.concat(
				Attachment.COL_NOTEID, " IN (SELECT ", Note.COL_ID, " FROM ", Note.TABLE_NAME, " WHERE ", Note.COL_CATEGORYID, " = ?)");

		db.beginTransaction();
        try {
			db.delete(Attachment.TABLE_NAME, whereClause, whereArgs);
			db.delete(CheckItem.TABLE_NAME, whereClause, whereArgs);
			db.delete(Note.TABLE_NAME, Note.COL_CATEGORYID+" = ?", whereArgs);
			status = db.delete(TABLE_NAME, COL_ID+" = ?", whereArgs)
					== 1 ? true : false;
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	    } finally {
	    	db.endTransaction();
	    }
		return status;		
	}	
	
	//--------------------------------------------------------------------------

	private String name;
	private List<Note> notes;
	
	public void reset() {
		super.reset();
		name = null;
		notes = null;
	}	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public List<Note> getNotes() {
		return notes;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public Category() {}
	
	public Category(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((Category)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	
	
}
