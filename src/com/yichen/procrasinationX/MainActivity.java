package com.yichen.procrasinationX;

import java.lang.reflect.Method;

import com.yichen.procrasinationX.R;
import com.yichen.procrasinationX.note.SnapshotActivity;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;


public class MainActivity extends TabActivity implements OnTabChangeListener {
	
	//private static final String TAG = "MainActivity";
	
	public static final String TAB_MANAGE = "manage";
	public static final String TAB_BROWSE = "browse";
	private LayoutInflater inflater;
	private Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pxmain);
        
        inflater = getLayoutInflater();
        res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
        // Browse
        intent = new Intent().setClass(this, BrowseActivity.class);
        spec = tabHost.newTabSpec(TAB_BROWSE).setIndicator(createTabIndicator(res.getString(R.string.browse))).setContent(intent);
        tabHost.addTab(spec);
        
        // Manage
        intent = new Intent().setClass(this, ManageActivity.class);
        spec = tabHost.newTabSpec(TAB_MANAGE).setIndicator(createTabIndicator(res.getString(R.string.manage))).setContent(intent);
        tabHost.addTab(spec);        
        
  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    private View createTabIndicator(String label) {
    	View tabIndicator = inflater.inflate(R.layout.tabindicator, null);
    	TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
    	tv.setText(label);
    	return tabIndicator;
    }

	@Override
	public void onTabChanged(String tabId) {
	}
	
	//------
			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
			    // Handle presses on the action bar items
				//ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
				//int type = ExpandableListView.getPackedPositionType(info.packedPosition);

			    switch (item.getItemId()) {
			        case R.id.snapshot:
			        //	opensnap();
			        	Intent intent = new Intent(MainActivity.this, SnapshotActivity.class);   
					startActivity(intent); 
						
			            return true;
			        default:
			            return super.onOptionsItemSelected(item);
			    }
			}
			@Override
			public boolean onMenuOpened(int featureId, Menu menu)
			{
			    if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
			        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
			            try{
			                Method m = menu.getClass().getDeclaredMethod(
			                    "setOptionalIconsVisible", Boolean.TYPE);
			                m.setAccessible(true);
			                m.invoke(menu, true);
			            }
			            catch(NoSuchMethodException e){
			                //Log.e(TAG, "onMenuOpened", e);
			            }
			            catch(Exception e){
			                throw new RuntimeException(e);
			            }
			        }
			    }
			    return super.onMenuOpened(featureId, menu);
			}
			//------
	@Override
	protected void onDestroy() {
		SmartPad.clearAuth();
		super.onDestroy();
	}	

}