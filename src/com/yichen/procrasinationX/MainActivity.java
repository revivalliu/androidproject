package com.yichen.procrasinationX;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.http.protocol.HTTP;

import com.yichen.procrasinationX.R;
import com.yichen.procrasinationX.R.string;
import com.yichen.procrasinationX.note.SnapshotActivity;
import com.yichen.procrasinationX.remindme.ReminderMainActivity;


import android.app.SearchManager;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;


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
        
        //--reserved for doodle tab, create xml and update string and manifest file
        /*
        intent = new Intent().setClass(this, FingerActivity.class);
        spec = tabHost.newTabSpec(TAB_Doodle).setIndicator(createTabIndicator(res.getString(R.string.doodle))).setContent(intent);
        tabHost.addTab(spec); 
        */
  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
     // Associate searchable configuration with the SearchView
       // SearchManager searchManager =
         //      (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView =
          //      (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSearchableInfo(
          //      searchManager.getSearchableInfo(getComponentName()));

        //-----------------widge-------------
        // Get the SearchView and set the searchable configuration
           SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
           SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
           // Assumes current activity is the searchable activity
           searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
           searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
           
           //-----------------------------------
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
				Intent intent;
			    switch (item.getItemId()) {
			        case R.id.snapshot:
			         
			        	intent = new Intent(MainActivity.this, SnapshotActivity.class);   
					startActivity(intent); 
				    Toast.makeText(MainActivity.this,"Take a photo memo!", Toast.LENGTH_SHORT).show();
			        
					return true;
					
			        case R.id.share:
			        	//
			        	//string textMessage;
			        	// Create the text message with a string
			        	Intent sendIntent = new Intent();
			        	sendIntent.setAction(Intent.ACTION_SEND);
			        	//sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
			        	sendIntent.setType(HTTP.PLAIN_TEXT_TYPE); // "text/plain" MIME type

			        	// Verify that the intent will resolve to an activity
			        	if (sendIntent.resolveActivity(getPackageManager()) != null) {
			        	    startActivity(sendIntent);
			        	}
			        	//--
			        
						
			            return true;
			        case R.id.settings:
				        //	opensnap();
//				        	intent = new Intent(MainActivity.this, ReminderMainActivity.class);   
//						startActivity(intent); 
			        	/*
						final Intent intentDeviceTest = new Intent("android.intent.action.MAIN"); //intent or view               
						intentDeviceTest.setComponent(new ComponentName("com.yichen.procrasinationX","com.yichen.procrasinationX.remindme.ReminderMainActivity"));
						startActivity(intentDeviceTest);
						*/
						
						intent = new Intent(MainActivity.this, SettingsActivity.class);
						startActivity(intent);
							
				        return true;
				        
			        case R.id.countdown:
			        	intent = new Intent(MainActivity.this, CountdownActivity.class);   
					startActivity(intent); 
				        return true;    
				        
			        case R.id.audio:
			        	intent = new Intent(MainActivity.this, AudioActivity.class);   
					startActivity(intent); 
				        return true;
				        
			        case R.id.help:
			        	intent = new Intent(MainActivity.this, Help.class);   
					startActivity(intent); 
				    return true;
			        
			        case R.id.location:
			        	Uri locations = Uri.parse("geo:0,0?q=1600+Holloway+Avenue,+San+Francisco,+California");
			        	Intent mapIntents = new Intent(Intent.ACTION_VIEW, locations);
			       
				        
			

			        	// Verify it resolves
			        	PackageManager packageManagers = getPackageManager();
			        	List<ResolveInfo> activitieses = packageManagers.queryIntentActivities(mapIntents, 0);
			        	boolean isIntentSafes = activitieses.size() > 0;

			        	// Start an activity if it's safe
			        	if (isIntentSafes) {
			        	    startActivity(mapIntents);
			        	}
							
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