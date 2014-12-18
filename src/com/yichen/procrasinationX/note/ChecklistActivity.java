package com.yichen.procrasinationX.note;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yichen.procrasinationX.model.CheckItem;
import com.yichen.procrasinationX.model.Note;
import com.yichen.procrasinationX.R;


public class ChecklistActivity extends BaseActivity {
	
	//private static final String TAG = "ChecklistActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note.setType(Note.CHECKLIST);
    }
	
	public void onClick(View v) {
		super.onClick(v);
		
		LinearLayout checkitemLL;
		switch (v.getId()) {
		case R.id.additem_btn:
			checkitemLL = (LinearLayout) inflater.inflate(R.layout.row_checkitem, null);
			EditText itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);  
	        itemEdit.setTypeface(font);			
			checklistLL.addView(checkitemLL);
			break;
			
		case R.id.deleteitem_btn:
			checkitemLL = (LinearLayout) v.getParent();
			TextView itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
			if (!TextUtils.isEmpty(itemId.getText()))
				new CheckItem(Long.parseLong(itemId.getText().toString())).delete(db);
			checklistLL.removeView(checkitemLL);
			break;			
		}
	}
	
	protected void reset() {
		super.reset();
		contentEdit.setVisibility(View.GONE);
		gallery.setVisibility(View.GONE);
		checklistLL.removeAllViews();
		
		if (note.getId() > 0) {
			Cursor c = CheckItem.list(db, String.valueOf(note.getId()));
			if (c.moveToFirst()) {
				LinearLayout checkitemLL;
				TextView itemId;
				CheckBox itemCb;
				EditText itemEdit;
				
				do {
					checkitemLL = (LinearLayout) inflater.inflate(R.layout.row_checkitem, null);
					itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
					itemCb = (CheckBox) checkitemLL.findViewById(R.id.item_cb);
					itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);
					
					itemId.setText(c.getString(c.getColumnIndex(CheckItem.COL_ID)));
					itemCb.setChecked(c.getInt(c.getColumnIndex(CheckItem.COL_STATUS))==1 ? true : false);
					itemEdit.setText(c.getString(c.getColumnIndex(CheckItem.COL_NAME)));
					itemEdit.setTypeface(font);
					
					checklistLL.addView(checkitemLL);
				} while(c.moveToNext());
			}
			c.close();
		}
	}
	
	protected void persist() {
		super.persist();
		
		// persist check items
		int itemCount = checklistLL.getChildCount();
		LinearLayout checkitemLL;
		TextView itemId;
		CheckBox itemCb;
		EditText itemEdit;
		CheckItem checkitem;
		CharSequence id, edit;
		for (int i=0; i<itemCount; i++) {
			checkitemLL = (LinearLayout) checklistLL.getChildAt(i);
			itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
			itemCb = (CheckBox) checkitemLL.findViewById(R.id.item_cb);
			itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);
			
			id = itemId.getText();
			edit = itemEdit.getText();
			
			if (TextUtils.isEmpty(id) && TextUtils.isEmpty(edit))
				continue;
			
			if (!TextUtils.isEmpty(id))
				checkitem = new CheckItem(Long.parseLong(id.toString()));
			else
				checkitem = new CheckItem();
			checkitem.setNoteId(note.getId());
			checkitem.setStatus(itemCb.isChecked());
			checkitem.setName(edit.toString());
			checkitem.setId(checkitem.persist(db));
		}
	}
	
	boolean canSave() {
		return !TextUtils.isEmpty(titleEdit.getText()) || checklistLL.getChildCount() > 0;
	}

}
