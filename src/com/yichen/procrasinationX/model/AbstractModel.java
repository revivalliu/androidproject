package com.yichen.procrasinationX.model;

import android.database.sqlite.SQLiteDatabase;


abstract class AbstractModel {
	
	static final String COL_ID = "_id";
	
	protected long id;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	abstract long save(SQLiteDatabase db);
	abstract boolean update(SQLiteDatabase db);
	
	public long persist(SQLiteDatabase db) {
		if (id > 0)
			return update(db) ? id : 0;
		else
			return save(db);
	}

}
