package com.yichen.procrasinationX.paint;

import com.yichen.procrasinationX.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


public class PaintActivity extends TabActivity {
    /** Called when the activity is first created. */
	
	private TabHost tabHost;
	private LayoutInflater mLayoutInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		
		setContentView(R.layout.tuya_paint_layout);
		
		mLayoutInflater = LayoutInflater.from(this);
	    tabHost = getTabHost();
	    TabSpec spec;
	    Intent intent;
 
	    //第一个TAB 手写
	    intent = new Intent(this,FingerActivity.class);
	    spec = tabHost.newTabSpec("tab1").setIndicator(
				getTabItemView(0)).setContent(intent);
	    tabHost.addTab(spec);
 /*
	    //第二个 涂鸦
	    intent = new Intent(this,TuyaActivity.class);
	    spec = tabHost.newTabSpec("tab2")
	    .setIndicator(getTabItemView(1))
	    .setContent(intent);
	    tabHost.addTab(spec);
	    
	    */
	    
	    tabHost.setCurrentTab(0);//默认手写
	}

	private View getTabItemView(int index) {
		// TODO Auto-generated method stub
		if (index == 0) {
			View view = mLayoutInflater.inflate(R.layout.tuya_tab_item_view_left, null);
			return view;
		}else if (index == 1) {
			View view = mLayoutInflater.inflate(R.layout.tuya_tab_item_view_right, null);
			return view;
		}
		return null;
	}
}