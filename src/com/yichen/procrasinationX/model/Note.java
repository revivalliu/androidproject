package com.yichen.procrasinationX.model;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yichen.procrasinationX.SmartPad;
import com.yichen.procrasinationX.common.Util;

/**
 * @author appsrox.com
 *
 */
public class Note extends BaseModel {
	
	public static final String TABLE_NAME = "note";
	public static final String COL_ID = BaseModel.COL_ID;
	public static final String COL_CREATEDTIME = BaseModel.COL_CREATEDTIME;
	public static final String COL_MODIFIEDTIME = BaseModel.COL_MODIFIEDTIME;
	public static final String COL_LOCKED = BaseModel.COL_LOCKED;
	public static final String COL_CATEGORYID = "category_id";
	public static final String COL_TITLE = "title";
	public static final String COL_CONTENT = "content";
	public static final String COL_TYPE = "type";
	
	public static final String BASIC = "basic";
	public static final String CHECKLIST = "checklist";
	public static final String SNAPSHOT = "snapshot";
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				BaseModel.getSql(),
				COL_CATEGORYID, " INTEGER, ",
				COL_TITLE, " TEXT, ", 
				COL_CONTENT, " TEXT, ",
				COL_TYPE, " TEXT",				
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.save(cv);
		cv.put(COL_CATEGORYID, categoryId);
		cv.put(COL_TITLE, title==null ? "" : title);
		cv.put(COL_CONTENT, content==null ? "" : content);
		cv.put(COL_TYPE, type==null ? BASIC : type);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.update(cv);
		if (categoryId > 0)
			cv.put(COL_CATEGORYID, categoryId);
		if (title != null)
			cv.put(COL_TITLE, title);
		if (content != null)
			cv.put(COL_CONTENT, content);
		if (type != null)
			cv.put(COL_TYPE, type);		
		
		return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)}) 
				== 1 ? true : false;
	}
	
	public boolean load(SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				reset();
				super.load(cursor);
				categoryId = cursor.getLong(cursor.getColumnIndex(COL_CATEGORYID));
				title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
				content = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
				type = cursor.getString(cursor.getColumnIndex(COL_TYPE));
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * @param db
	 * @param args {categoryId, orderBy}
	 * @return cursor
	 */
	public static Cursor list(SQLiteDatabase db, String... args) {
		String categoryId = args!=null ? args[0] : null;
		
		String[] columns = {COL_ID, COL_LOCKED, COL_TITLE, COL_TYPE, COL_MODIFIEDTIME, COL_CREATEDTIME};
		String selection = "1 = 1";
		selection += !SmartPad.showLocked() ? " AND "+COL_LOCKED+" <> 1" : "";
		selection += categoryId!=null ? " AND "+COL_CATEGORYID+" = "+categoryId : "";
		String orderBy = (args!=null && args.length>1) ? args[1] : 
				categoryId!=null ? COL_CREATEDTIME+" ASC" : COL_MODIFIEDTIME+" DESC";
		
		return db.query(TABLE_NAME, columns, selection, null, null, null, orderBy);
	}
	
	public boolean delete(SQLiteDatabase db) {
		boolean status = false;
		String[] whereArgs = new String[]{String.valueOf(id)};
		
		db.beginTransaction();
        try {
			db.delete(Attachment.TABLE_NAME, Attachment.COL_NOTEID+" = ?", whereArgs);
			db.delete(CheckItem.TABLE_NAME, CheckItem.COL_NOTEID+" = ?", whereArgs);
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

	private long categoryId;
	private String title;
	private String content;
	private String type;
	private List<Attachment> attachments;
	private List<CheckItem> checklist;	
	
	public void reset() {
		super.reset();
		categoryId = 0;
		title = null;
		content = null;
		type = null;
		attachments = null;
		checklist = null;
	}	
	
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	public List<CheckItem> getChecklist() {
		return checklist;
	}
	public void setChecklist(List<CheckItem> checklist) {
		this.checklist = checklist;
	}

	public Note() {}
	
	public Note(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((Note)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	

}
