package com.yichen.procrasinationX;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


public class AuthActivity extends Activity {
	
	//private static final String TAG = "AuthActivity";
	
	public static final int DIALOG_AUTHENTICATE = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }    
    
	@Override
	protected void onResume() {
		super.onResume();
		showDialog(DIALOG_AUTHENTICATE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_AUTHENTICATE:
			final EditText et = new EditText(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Enter Password")
				   .setIcon(android.R.drawable.ic_dialog_alert)
				   .setView(et)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   
				       		if (SmartPad.doAuth(et.getText().toString())) {
				    			Intent intent = new Intent();
				    			intent.setClass(AuthActivity.this, (Class)getIntent().getSerializableExtra("class"));
				    	    	intent.putExtras(getIntent());
				    	    	intent.removeExtra("class");
				    	    	startActivity(intent);
				    		} else {
				    			Toast.makeText(getApplicationContext(), "Incorrect Pasword!", Toast.LENGTH_LONG).show();
				    		}
				       		finish();
			           }
			       })
			       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   	finish();
			           }
			       });
			return builder.create();
		}
		
		return super.onCreateDialog(id);
	}	
}
