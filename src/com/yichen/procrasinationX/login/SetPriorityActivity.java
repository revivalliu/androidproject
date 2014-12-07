package com.yichen.procrasinationX.login;
//package com.yichen.px;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener; 
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//	
//public class SetPriorityActivity extends Activity{
//	
//	LinearLayout priority; CheckBox star;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//	super.onCreate(savedInstanceState); 
//	setContentView(R.layout.reminder_edit);
//	//---get the layout containing the stars---
//	priority = (LinearLayout) findViewById(R.id.priority);
//	for (int i = 1; i <= 5; i++) {
//	star = (CheckBox) priority.findViewWithTag(String.valueOf(i)); 
//	star.setOnClickListener(starsListener);
//	} }
//	private OnClickListener starsListener = new OnClickListener() { 
//		public void onClick(View view) {
//
//	//---get the tag of the star selected---
//	int tag = Integer.valueOf((String) view.getTag());
//	//---check all the stars up to the one touched---
//	for (int i = 1; i <= tag; i++) {
//		star = (CheckBox) priority.findViewWithTag(String.valueOf(i)); 
//		star.setChecked(true);
//		}
//		//---uncheck all remaining stars--- 
//	for (int i = tag + 1; i <= 5; i++) {
//		star = (CheckBox) priority.findViewWithTag(String.valueOf(i));
//		star.setChecked(false); 
//		}
//       }
//    };
//}
//
//
