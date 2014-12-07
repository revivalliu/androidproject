package com.yichen.procrasinationX.login;

import com.yichen.procrasinationX.MainActivity;
import com.yichen.procrasinationX.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//--
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;

//--

public class LoginActivity extends Activity 
{
	Button btnSignIn,btnSignUp;
	LoginDataBaseAdapter loginDataBaseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.main);
	     //----
	     Context context = getApplicationContext();
	        // Create layout inflator object to inflate toast.xml file
	        LayoutInflater inflater = getLayoutInflater();
	          
	        // Call toast.xml file for toast layout 
	        View toastRoot = inflater.inflate(R.layout.toast, null);
	          
	        Toast toast = new Toast(context);
	         
	        // Set layout to toast 
	        toast.setView(toastRoot);
	        toast.setGravity(Gravity.BOTTOM,//CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
	                0, 0);
	        toast.setDuration(Toast.LENGTH_LONG);
	        toast.show();
	       
	     //----
	     
	     // create a instance of SQLite Database
	     loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	     loginDataBaseAdapter=loginDataBaseAdapter.open();
	     
	     
	     btnSignIn=(Button)findViewById(R.id.buttonSignIN);
	     btnSignUp=(Button)findViewById(R.id.buttonSignUp);
			
	    // Set OnClick Listener on SignUp button 
	    btnSignUp.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			/// Create an Intent for SignUpActivity and Start the Activity
			Intent intentSignUp=new Intent(getApplicationContext(),SignUpActivity.class);
			startActivity(intentSignUp);
			}
		});
	}
	
	public void signIn(View V)
	   {
			final Dialog dialog = new Dialog(LoginActivity.this);
			dialog.setContentView(R.layout.login);
		    dialog.setTitle("Login");
		   
		    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
		    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
		    
			Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
				
			btnSignIn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// get The User name and Password
					String userName=editTextUserName.getText().toString();
					String password=editTextPassword.getText().toString();
					
					// fetch the Password form database for respective user name
					String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);
					
					// check if the Stored password matches with Password entered by user
					if(password.equals(storedPassword))
					{
						///Toast.makeText(LoginActivity.this, "Success, now do your work.", Toast.LENGTH_LONG).show();
						///dialog.dismiss();
						//---
												
						//--
		
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				        startActivity(intent);
					}
					else
					{
						Toast.makeText(LoginActivity.this, "User Name or Password does not match :(", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			dialog.show();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {  //to show the menu
        super.onCreateOptionsMenu(menu); //First call super
        MenuInflater mi = getMenuInflater(); //Call Activities method to retrieve the Menuinflater.
        //Inflate the menu I defined in sign_to_list_menu passing in the R file reference for the menu description.
        mi.inflate(R.menu.sign_to_list_menu, menu);  
        return true;
    }

     @Override
 //Capture the menu action
      public boolean onMenuItemSelected(int featureId, MenuItem item) {
          if(item.getItemId() == R.id.sign_to_list_menu) { //check the item ID to see if the add action was selected. 
        	  Intent i = new Intent(this, MainActivity.class); //create a new intent to select ReminderListActivity 
        	// and then start it. 
        	startActivity(i); 
            return true; //return true to indicate the select event was processed. 
        }
        //pass the call on to super for any menu items that may be in the menu. 
        return super.onMenuItemSelected(featureId, item);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	    // Close The Database
		loginDataBaseAdapter.close();
	}
}
