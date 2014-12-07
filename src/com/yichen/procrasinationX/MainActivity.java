package com.yichen.procrasinationX;

import com.yichen.procrasinationX.R;
import com.yichen.procrasinationX.slidingmenu.SlidemenuMainActivity;
import com.yichen.procrasinationX.swipeback.SwipeBackActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;


//public class MainActivity extends TabActivity implements OnTabChangeListener {
 public class MainActivity extends FragmentActivity{
	 private FragmentTabHost tabHost;
	//private static final String TAG = "MainActivity";
	
	public static final String TAB_MANAGE = "manage";
	public static final String TAB_BROWSE = "browse";
	
	private LayoutInflater inflater;
	private Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pxmain);
        
        //inflater = getLayoutInflater();
        res = getResources();
        //TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        //mTabHost.addTab(
        //        mTabHost.newTabSpec("tab1").setIndicator("Tab 1", null),
        //        FragmentTab.class, null);
        // mTabHost.addTab(
        //        mTabHost.newTabSpec("tab2").setIndicator("Tab 2", null),
        //        FragmentTab.class, null);
        // mTabHost.addTab(
             //   mTabHost.newTabSpec("tab3").setIndicator("Tab 3", null),
               // FragmentTab.class, null);
        //--
        intent = new Intent(this, SlidemenuMainActivity.class);
        //startActivity(intent);
        //--

        tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
        // Browse
        intent = new Intent().setClass(this, BrowseActivity.class);
        spec = tabHost.newTabSpec(TAB_BROWSE).setIndicator(createTabIndicator(res.getString(R.string.browse))).setContent(intent);
        tabHost.addTab(spec);
        
        // Manage
        intent = new Intent().setClass(this, ManageActivity.class);
        spec = tabHost.newTabSpec(TAB_MANAGE).setIndicator(createTabIndicator(res.getString(R.string.manage))).setContent(intent);
        tabHost.addTab(spec);   
      
        
        
       // tabHost.setOnTabChangedListener(this);
    }
    
    private View createTabIndicator(String label) {
    	View tabIndicator = inflater.inflate(R.layout.tabindicator, null);
    	TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
    	tv.setText(label);
    	return tabIndicator;
    }
/*--
	@Override
	public void onTabChanged(String tabId) {
	}
	*/
	@Override
	protected void onDestroy() {
		SmartPad.clearAuth();
		super.onDestroy();
	}	

}