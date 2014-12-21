package com.yichen.procrasinationX;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class SearchableActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      ///-doMySearch(query);
	    }
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    // Inflate the options menu from XML
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.options_menu, menu);
//
//	    // Get the SearchView and set the searchable configuration
//	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//	    // Assumes current activity is the searchable activity
//	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//	    return true;
//	}
}
