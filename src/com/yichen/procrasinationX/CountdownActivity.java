package com.yichen.procrasinationX;

import java.util.concurrent.TimeUnit;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CountdownActivity extends Activity {
		Button btnStart, btnStop,btn;
		TextView textViewTime;

		@Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.countdown);
		 
		        btnStart = (Button)findViewById(R.id.btnStart);
		        btnStop = (Button)findViewById(R.id.btnStop);
		        btn = (Button)findViewById(R.id.btnBackhome);
		        textViewTime  = (TextView)findViewById(R.id.textViewTime);
		        textViewTime.setText("00:3:00"); 
		        
		        final CounterClass timer = new CounterClass(180000,1000);
		        btnStart.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                timer.start();
		            }
		        });
		        
		        btnStop.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                timer.cancel();
		            }
		        });
		        btn.setOnClickListener(new View.OnClickListener() {
		           @Override
		            public void onClick(View v) {
		            Intent intent = new Intent(CountdownActivity.this, MainActivity.class);
		    			startActivity(intent);
		            }
		        });
		        
		    }

		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@SuppressLint("NewApi")
		public class CounterClass extends CountDownTimer {

			public CounterClass(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
			}

			@Override
		        public void onFinish() {
		            textViewTime.setText("Completed.");
		        }

			@SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			public void onTick(long millisUntilFinished) {
				
				  long millis = millisUntilFinished;
				    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
				    System.out.println(hms);
				    
				    textViewTime.setText(hms);
			}
			
		}
	}
	
