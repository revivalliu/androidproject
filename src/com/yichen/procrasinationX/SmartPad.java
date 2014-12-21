package com.yichen.procrasinationX;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yichen.procrasinationX.model.DbHelper;
import com.yichen.procrasinationX.R;


public class SmartPad extends Application {
	
	public static DbHelper dbHelper;
	public static SQLiteDatabase db;
	public static SharedPreferences sp;
	
	public static final String HIDE_LOCKED = "hideLocked";
	public static final String PASSWORD = "password";
	public static final String LAST_AUTH = "lastAuth";
	public static final String DEFAULT_SORT = "defaultSort";
	public static final String DEFAULT_CATEGORY_OPT = "defaultCategoryOpt";
	public static final String TIME_OPTION = "timeOpt";
	
	public static long PUBLIC_CATEGORYID = 1;
	public static long LASTCREATED_CATEGORYID;
	public static long LASTSELECTED_CATEGORYID;
	
	
	//public static final String TIME_OPTION = "time_option";
	public static final String DATE_RANGE = "date_range";
	public static final String DATE_FORMAT = "date_format";
	public static final String TIME_FORMAT = "time_format";
	public static final String VIBRATE_PREF = "vibrate_pref";
	public static final String RINGTONE_PREF = "ringtone_pref";
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";
	//--+ remindme

	@Override
	public void onCreate() {
		super.onCreate();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		dbHelper = new DbHelper(this);
		db = dbHelper.getWritableDatabase();		
	}
	
	public static int getDefaultCategoryOpt() {
		return Integer.parseInt(sp.getString(DEFAULT_CATEGORY_OPT, "0"));
	}	
	
	public static boolean showLocked() {
		return !sp.getBoolean(HIDE_LOCKED, false);
	}
	
	public static boolean isAuth() {
		return TextUtils.isEmpty(sp.getString(PASSWORD, null)) || 
				(System.currentTimeMillis()-sp.getLong(LAST_AUTH, 0)) < 5*60*1000;
	}

	public static void clearAuth() {
		sp.edit().remove(LAST_AUTH).commit();
	}	
	
	public static boolean doAuth(String str) {
		String pass = sp.getString(PASSWORD, null);
		boolean isAuth = TextUtils.isEmpty(pass) || pass.equals(str);
		if (isAuth)
			sp.edit().putLong(LAST_AUTH, System.currentTimeMillis()).commit();
		return isAuth;
	}
	
	public static int getDefaultSort() {
		return Integer.parseInt(sp.getString(DEFAULT_SORT, "0"));
	}
	
	public static int getTimeOption() {
		return Integer.parseInt(sp.getString(TIME_OPTION, "0"));
	}	
	
	//---
	public static boolean showRemainingTime() {
		return "1".equals(sp.getString(TIME_OPTION, "0"));
	}
	
	public static int getDateRange() {
		return Integer.parseInt(sp.getString(DATE_RANGE, "0"));
	}
	
	public static String getDateFormat() {
		return sp.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
	}	
	
	public static boolean is24Hours() {
		return sp.getBoolean(TIME_FORMAT, true);
	}
	
	public static boolean isVibrate() {
		return sp.getBoolean(VIBRATE_PREF, true);
	}
	
	public static String getRingtone() {
		return sp.getString(RINGTONE_PREF, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}

	//--
}


