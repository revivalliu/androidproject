package com.yichen.procrasinationX;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yichen.procrasinationX.alarmclock.AlarmListActivity;
import com.yichen.procrasinationX.login.LoginActivity;
import com.yichen.procrasinationX.model.Category;
import com.yichen.procrasinationX.model.Note;
import com.yichen.procrasinationX.paint.FingerActivity;
import com.yichen.procrasinationX.paint.PaintActivity;
import com.yichen.procrasinationX.paint.TuyaActivity;
import com.yichen.procrasinationX.voicerecognition.VoiceRecognitionActivity;
import com.yichen.procrasinationX.R;

public class ManageActivity extends ExpandableListActivity {
	
	//private static final String TAG = "ManageActivity";
	
	public static final int DIALOG_NEW_CATEGORY = 1;
	public static final int DIALOG_RENAME_CATEGORY = 2;
	
	private Button newBtn;
	private Button settingsBtn;
	private Button newBtn0;
	private Button newBtn1;
	
	private EditText categoryNameEdit;
	private AlertDialog categoryDialog;
	private Category category;
	private Note note;
	
	private SQLiteDatabase db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_manage);
        findViews();
        db = SmartPad.db;
        
        registerForContextMenu(getExpandableListView());
        getExpandableListView().setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				openContextMenu(v);
				return true;
			}
		});
        getExpandableListView().setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				int childCount = getExpandableListAdapter().getChildrenCount(groupPosition);
				if (childCount == 0)
					Toast.makeText(ManageActivity.this, "Empty Folder", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
        
        category = new Category();
        categoryNameEdit = new EditText(this);
        categoryNameEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        categoryDialog = new AlertDialog.Builder(this)
		   .setTitle("New Folder")
		   .setView(categoryNameEdit)
	       .setCancelable(false)
	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if (!TextUtils.isEmpty(categoryNameEdit.getText())) {
		        	   category.setName(categoryNameEdit.getText().toString());
		        	   SmartPad.LASTCREATED_CATEGORYID = category.persist(db);
		        	   
		        	   SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getExpandableListAdapter();
		        	   adapter.getCursor().requery();
		        	   adapter.notifyDataSetChanged();
	        	   }
	           }
	       })
	       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       })
	       .create();
        
        note = new Note();
    }    
    
    @Override
	protected void onResume() {
		super.onResume();
        
		Cursor c = Category.list(db);
		startManagingCursor(c);
        SimpleCursorTreeAdapter adapter = new CategoryTreeAdapter(this, c);
		setListAdapter(adapter);        
	}
	
	private void findViews() {
		newBtn = (Button) findViewById(R.id.new_btn);
		newBtn0 = (Button) findViewById(R.id.new_btn0);
		newBtn1 = (Button) findViewById(R.id.new_btn1);
		settingsBtn = (Button) findViewById(R.id.settings_btn);
	}
	
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.new_btn0:
			intent = new Intent(this, TuyaActivity.class);   
			startActivity(intent);
			break;
		case R.id.new_btn1:
			intent = new Intent(this, VoiceRecognitionActivity.class);
			startActivity(intent); 
			break;
		case R.id.new_btn:
			showDialog(DIALOG_NEW_CATEGORY);
			break;
			
		case R.id.settings_btn:
			intent = new Intent();
			if (!SmartPad.isAuth()) {
	    		intent.setClass(this, AuthActivity.class);
	    		intent.putExtra("class", SettingsActivity.class);
			} else {
				intent.setClass(this, SettingsActivity.class);
			}
			startActivity(intent);			
			break;			
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NEW_CATEGORY:
		case DIALOG_RENAME_CATEGORY:
	        return categoryDialog;
		}
		return super.onCreateDialog(id);
	}	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		switch (id) {
		case DIALOG_NEW_CATEGORY:
			category.reset();
			categoryNameEdit.setText(null);
			break;
			
		case DIALOG_RENAME_CATEGORY:
			break;
		}		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			int type = ExpandableListView.getPackedPositionType(info.packedPosition);
				
			getMenuInflater().inflate(R.menu.contextmenu_manage, menu);
			menu.setHeaderTitle("Choose an Option");
			menu.setHeaderIcon(R.drawable.ic_dialog_menu_generic);
			
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				note.reset();
				note.setId(info.id);
				note.load(db);
				
				category.reset();
				category.setId(note.getCategoryId());
				category.load(db);				
				
				if (category.isLocked()) {
					menu.removeItem(R.id.menu_lock);
					menu.removeItem(R.id.menu_unlock);
				} else if (note.isLocked())
					menu.removeItem(R.id.menu_lock);
				else
					menu.removeItem(R.id.menu_unlock);
				
			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				category.reset();
				category.setId(info.id);
				category.load(db);				
				
				if (category.isLocked())
					menu.removeItem(R.id.menu_lock);
				else
					menu.removeItem(R.id.menu_unlock);
				
				if (info.id == SmartPad.PUBLIC_CATEGORYID) {
					
					menu.removeItem(R.id.menu_delete);
					menu.removeItem(R.id.menu_edit);
				}
			}			
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);

		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			note.reset();
			note.setId(info.id);
			
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			category.reset();
			category.setId(info.id);			
		}

		boolean refresh = false;
		switch (item.getItemId()) {
		//case R.id.menu_edit:
		//	if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		//		BrowseActivity.openNote(info.id, this);
				
		//	} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
		//		category.load(db);
		//		categoryNameEdit.setText(category.getName());
				
		//		showDialog(DIALOG_RENAME_CATEGORY);
		//	}
		//	break;

		case R.id.menu_delete:
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		    	note.delete(db);

			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
		    	category.delete(db);
			}			
			refresh = true;
			break;
			
		case R.id.menu_lock:
		case R.id.menu_unlock:
			boolean status = item.getItemId()==R.id.menu_lock ? true : false;
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				note.setLocked(status);
				note.persist(db);

			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				category.setLocked(status);
				category.persist(db);
			}
			refresh = true;
			break;
			
		case R.id.menu_setReminder:
			Intent intent = new Intent(this, AlarmListActivity.class);   
			startActivity(intent);
			break;
		}
	        
		if (refresh) {
			SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getExpandableListAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();
		}
		
		return true;
	}
	
	
	//--------------------------------------------------------------------------

	private class CategoryTreeAdapter extends SimpleCursorTreeAdapter {
		
		public CategoryTreeAdapter(Context context, Cursor cursor) {
			super(
					context, 
					cursor, 
	        		R.layout.row_category, 
	        		new String[]{Category.COL_NAME}, 
	        		new int[]{android.R.id.text1}, 
	        		R.layout.row_category_note, 
	        		new String[]{Note.COL_TITLE}, 
	        		new int[]{android.R.id.text1});
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			Cursor c = Note.list(SmartPad.db, groupCursor.getString(groupCursor.getColumnIndex(Category.COL_ID)));
			startManagingCursor(c);
			return c;
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
			super.bindGroupView(view, context, cursor, isExpanded);

			boolean showLock = cursor.getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;
			
			((TextView)view.findViewById(android.R.id.text1)).setCompoundDrawablesWithIntrinsicBounds(
					showLock ? R.drawable.folder_locked : R.drawable.folder, 
					0, 
					0, 
					0);
		}
		
		@Override
		protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
			super.bindChildView(view, context, cursor, isLastChild);

			boolean showLock = getCursor().getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;
			// if group is locked then show lock on a child
			showLock = showLock || cursor.getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;
			
			((TextView)view.findViewById(android.R.id.text1)).setCompoundDrawablesWithIntrinsicBounds(
					showLock ? R.drawable.file_locked : R.drawable.file, 
					0, 
					0, 
					0);
		}
		
    }
}
