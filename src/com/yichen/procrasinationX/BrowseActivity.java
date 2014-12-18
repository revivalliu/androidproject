package com.yichen.procrasinationX;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.yichen.procrasinationX.model.Category;
import com.yichen.procrasinationX.model.Note;
import com.yichen.procrasinationX.note.BasicActivity;
import com.yichen.procrasinationX.note.ChecklistActivity;
import com.yichen.procrasinationX.note.SnapshotActivity;



public class BrowseActivity extends Activity implements AdapterView.OnItemClickListener {
	

	
	private ListView noteList;
	private TextView emptyView;
	private Button newBtn;
	private Button newBtn0;
	private Button newBtn1;
	private Button sortBtn;	
	
	private SQLiteDatabase db;
	
	private AlertDialog newBtnDialog;
	private AlertDialog sortBtnDialog;
	
	private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	private final Calendar cal = Calendar.getInstance(); 
	
	private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (dialog == newBtnDialog) {
				Intent intent = new Intent();
				switch (which) {
				case 0:
					intent.setClass(BrowseActivity.this, BasicActivity.class);
					break;
				case 1:
					intent.setClass(BrowseActivity.this, ChecklistActivity.class);
					break;
				case 2:
					intent.setClass(BrowseActivity.this, SnapshotActivity.class);
					break;				
				}
				startActivity(intent);
				
			} else if (dialog == sortBtnDialog) {

				Cursor c = Note.list(db, null, getOrderBy(which));
				startManagingCursor(c);
				((SimpleCursorAdapter)noteList.getAdapter()).changeCursor(c);
			}
		}
	};
	
	private String getOrderBy(int which) {
		String orderBy = null;
		switch (which) {
		case 0: 
			orderBy = Note.COL_MODIFIEDTIME + " DESC";
			break;
		case 1: 
			orderBy = Note.COL_TITLE + " COLLATE NOCASE ASC";
			break;
		case 2: 
			orderBy = Note.COL_CREATEDTIME + " ASC";
			break;				
		}
		return orderBy;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_browse);
        findViews();
        db = SmartPad.db;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle("Choose an Option");
        builder.setItems(R.array.new_note_arr, dialogListener);
        newBtnDialog = builder.create();
        
        builder.setTitle("Sort by");
        builder.setItems(R.array.sort_options_arr, dialogListener);
        sortBtnDialog = builder.create();        
        
        noteList.setOnItemClickListener(this);
        noteList.setEmptyView(emptyView);
        
        registerForContextMenu(noteList);
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		Cursor c = Note.list(db, null, getOrderBy(SmartPad.getDefaultSort()));
		startManagingCursor(c);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row_note, 
				c, 
				new String[]{Note.COL_TITLE, 
							Note.COL_TYPE, 
							SmartPad.getTimeOption()==0 ? Note.COL_MODIFIEDTIME : Note.COL_CREATEDTIME}, 
				new int[]{android.R.id.text1, R.id.icon, android.R.id.text2});
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				switch (view.getId()) {
				case R.id.icon:
					String type = cursor.getString(columnIndex);
					int icon = 0;
					
					if (Note.BASIC.equals(type))
						icon = R.drawable.document;
					else if (Note.CHECKLIST.equals(type))
						icon = R.drawable.checklist;
					else if (Note.SNAPSHOT.equals(type))
						icon = R.drawable.pictures;
					
					((ImageView)view).setImageResource(icon);
					return true;					
					
				case android.R.id.text2:
					cal.setTimeInMillis(cursor.getLong(columnIndex));
					((TextView)view).setText(df.format(cal.getTime()));
					return true;					
				}

				return false;
			}
		});
		
		noteList.setAdapter(adapter);
	}
	
	private void findViews() {
		noteList = (ListView) findViewById(R.id.note_list);
		emptyView = (TextView) findViewById(android.R.id.empty);
		newBtn = (Button) findViewById(R.id.new_btn);
		newBtn0 = (Button) findViewById(R.id.clist_btn);
		newBtn1 = (Button) findViewById(R.id.conf_btn);
		sortBtn = (Button) findViewById(R.id.sort_btn);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.note_list) {
			getMenuInflater().inflate(R.menu.contextmenu_browse, menu);
			menu.setHeaderTitle("Choose an Option");
			menu.setHeaderIcon(R.drawable.ic_dialog_menu_generic);			
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case R.id.menu_edit:
			openNote(info.id, this);
			break;
			
		case R.id.menu_delete:
	    	Note note = new Note(info.id);
	    	note.delete(db);
	    	
	    	SimpleCursorAdapter adapter = (SimpleCursorAdapter) noteList.getAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();	    	
			break;			
		}
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_btn:
			newBtnDialog.show();
			break;
			
		case R.id.sort_btn:
			sortBtnDialog.show();
			break;
		case R.id.clist_btn:
			Intent intent = new Intent(BrowseActivity.this, ChecklistActivity.class);   
			startActivity(intent);
			break;
		case R.id.conf_btn:
			Intent intentc = new Intent(BrowseActivity.this, SettingsActivity.class);   
			startActivity(intentc);
			//sortBtnDialog.show();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		openNote(id, this);
	}
	
	static void openNote(long id, Context ctx) {
    	Note note = new Note(id);
    	note.load(SmartPad.db);
    	
    	Class claz = null;
    	String type = note.getType();
    	if (Note.BASIC.equals(type)) {
    		claz = BasicActivity.class;
    	} else if (Note.CHECKLIST.equals(type)) {
    		claz = ChecklistActivity.class;
    	} else if (Note.SNAPSHOT.equals(type)) {
    		claz = SnapshotActivity.class;
    	}
    	
    	boolean isLocked = note.isLocked();
    	if (!isLocked) {
	    	Category category = new Category(note.getCategoryId());
	    	category.load(SmartPad.db);
	    	isLocked = category.isLocked();
    	}
    	
    	Intent intent = new Intent();
        // authenticate
    	if (isLocked && !SmartPad.isAuth()) {
    		intent.setClass(ctx, AuthActivity.class);
    		intent.putExtra("class", claz);
    	} else {
    		intent.setClass(ctx, claz);
    	}
    	intent.putExtra(Note.COL_ID, id);
    	ctx.startActivity(intent);		
	}
	

}
