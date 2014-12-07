package com.yichen.procrasinationX.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.yichen.procrasinationX.common.Util;

abstract class BaseModel extends AbstractModel {
	
	static final String COL_ID = AbstractModel.COL_ID;
	static final String COL_CREATEDTIME = "created_time";
	static final String COL_MODIFIEDTIME = "modified_time";
	static final String COL_LOCKED = "locked";
	
	static String getSql() {
		return Util.concat(COL_ID, " INTEGER PRIMARY KEY AUTOINCREMENT, ", 
				COL_CREATEDTIME, " INTEGER, ", 
				COL_MODIFIEDTIME, " INTEGER, ", 
				COL_LOCKED, " INTEGER, ");
	}
	
	void save(ContentValues cv) {
		long now = System.currentTimeMillis();
		cv.put(COL_CREATEDTIME, now);
		cv.put(COL_MODIFIEDTIME, now);
		cv.put(COL_LOCKED, (isLocked!=null && isLocked) ? 1 : 0);
	}
	
	void update(ContentValues cv) {
		cv.put(COL_ID, id);
		cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
		if (isLocked != null)
			cv.put(COL_LOCKED, isLocked ? 1 : 0);		
	}	
	
	void load(Cursor cursor) {
		id = cursor.getLong(cursor.getColumnIndex(COL_ID));
		createdTime = cursor.getLong(cursor.getColumnIndex(COL_CREATEDTIME));
		modifiedTime = cursor.getLong(cursor.getColumnIndex(COL_MODIFIEDTIME));
		isLocked = cursor.getInt(cursor.getColumnIndex(COL_LOCKED)) == 1 ? true : false;
	}
	
	//--------------------------------------------------------------------------
	
	protected long createdTime;
	protected long modifiedTime;
	protected Boolean isLocked;
	
	protected void reset() {
		id = 0;
		createdTime = 0;
		modifiedTime = 0;
		isLocked = null;
	}

	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public boolean isLocked() {
		return isLocked!=null ? isLocked : false;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}	
	
}
