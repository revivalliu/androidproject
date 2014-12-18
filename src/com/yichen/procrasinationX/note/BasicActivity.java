package com.yichen.procrasinationX.note;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yichen.procrasinationX.model.Note;


public class BasicActivity extends BaseActivity {
	
	//private static final String TAG = "BasicActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note.setType(Note.BASIC);  
        contentEdit.setTypeface(font);
    }
	
	protected void reset() {
		super.reset();
		addItemBtn.setVisibility(View.GONE);		
		checklistLL.setVisibility(View.GONE);
		gallery.setVisibility(View.GONE);
	}
	
	boolean canSave() {
		return !TextUtils.isEmpty(titleEdit.getText()) || !TextUtils.isEmpty(contentEdit.getText());
	}	

}
