package com.yichen.procrasinationX.note;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.yichen.procrasinationX.SmartPad;
import com.yichen.procrasinationX.model.Category;
import com.yichen.procrasinationX.model.Note;
import com.yichen.procrasinationX.R;


abstract class BaseActivity extends Activity {
	
	//private static final String TAG = "BaseActivity";
	
	protected ImageButton categoryBtn;
	protected Spinner spinner;
	protected EditText titleEdit;
	protected EditText contentEdit;
	protected ImageButton addItemBtn;
	protected LinearLayout checklistLL;
	protected Gallery gallery;
	
	protected LayoutInflater inflater;
	protected SQLiteDatabase db;
	protected Note note;
	
	protected Typeface font;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);
        findViews();
        
        inflater = getLayoutInflater();
        db = SmartPad.db;
        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        
        // create instance
        long noteId = getIntent().getLongExtra(Note.COL_ID, 0);
        if (noteId > 0)
        	note = new Note(noteId);
        else
        	note = new Note();
        
		Cursor c = Category.list(db);
		startManagingCursor(c);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				android.R.layout.simple_spinner_item, 
				c, 
				new String[]{Category.COL_NAME}, 
				new int[]{android.R.id.text1});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);        

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				note.setCategoryId(id);
				SmartPad.LASTSELECTED_CATEGORYID = id;
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});		
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (note.getId() > 0)
			outState.putLong(Note.COL_ID, note.getId());
	}	

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState.containsKey(Note.COL_ID))
			note.setId(savedInstanceState.getLong(Note.COL_ID)); 
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// load data
		if (note.getId() > 0)
			note.load(db);
		
		// initialize views
        reset();		
	}

	protected void findViews() {
		categoryBtn = (ImageButton) findViewById(R.id.category_btn);
		spinner = (Spinner) findViewById(R.id.spinner);
		titleEdit = (EditText) findViewById(R.id.title_et);
		contentEdit = (EditText) findViewById(R.id.content_et);
		addItemBtn = (ImageButton) findViewById(R.id.additem_btn);
		checklistLL = (LinearLayout) findViewById(R.id.checklist_ll);
		gallery = (Gallery) findViewById(R.id.gallery);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.category_btn:
			spinner.performClick();
			break;		
		case R.id.done_btn:
			finish();
			break;			
		}
	}
	
	protected void reset() {
		SpinnerAdapter adapter = spinner.getAdapter();
		int count = adapter.getCount();
		long categoryId = note.getCategoryId()>0 ? note.getCategoryId() : getDefaultCategoryId();
		for (int i=0; i<count; i++) {
			if (adapter.getItemId(i) == categoryId) {
				spinner.setSelection(i);
				break;
			}
		}
		
		titleEdit.setText(note.getTitle());
		contentEdit.setText(note.getContent());
	}
	
	private long getDefaultCategoryId() {
		SpinnerAdapter adapter = spinner.getAdapter();
		int count = adapter.getCount();
		
		switch(SmartPad.getDefaultCategoryOpt()) {
		case 1:
			for (int i=0; i<count; i++) {
				if (adapter.getItemId(i) == SmartPad.LASTCREATED_CATEGORYID) {
					return SmartPad.LASTCREATED_CATEGORYID;
				}			
			}			
			break;
		case 2:
			for (int i=0; i<count; i++) {
				if (adapter.getItemId(i) == SmartPad.LASTSELECTED_CATEGORYID) {
					return SmartPad.LASTSELECTED_CATEGORYID;
				}			
			}			
			break;			
		}

		return SmartPad.PUBLIC_CATEGORYID;
	}
	
	protected void persist() {
		note.setTitle(titleEdit.getText().toString());
		note.setContent(contentEdit.getText().toString());
		note.setId(note.persist(db));
	}
	
	@Override
	protected void onPause() {
		if (note.getId() > 0 || canSave())
			persist();
		super.onPause();
	}
	
	abstract boolean canSave();

}
