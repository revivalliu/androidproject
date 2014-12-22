package com.yichen.procrasinationX.paint.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.yichen.procrasinationX.paint.TuyaActivity;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class FingerView extends View {

	
	public Handler dataHandler;
	public Handler getDataHandler() {
		return dataHandler;
	}
	public void setDataHandler(Handler dataHandler) {
		this.dataHandler = dataHandler;
	}
	
	
	FingerMatrix fingerMatrix;  
	
	
	
	
	public Paint getmPaint() {
		return mPaint;
	}
	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}
	
	private static int mWidth;
	private static int mHeight;
	
	public void setScreenSize(int Width,int Height){
		mWidth=Width;
		mHeight=Height;
		
	}


	
	public static  int color = Color.RED;
	public static  int srokeWidth = 15;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint mPaint;
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	
	private static List<DrawPath> savePath;
	
	private DrawPath dp;

	private int screenWidth, screenHeight;

	private class DrawPath {
		public Path path;
		public Paint paint;
	}
	
	
	private static final int CUT_BITMAP_SEND_TO_ACTIVITY = 1;
	
	public FingerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
	}

	public FingerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
	}

	public FingerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
	}



	private void init(int w, int h) {
		screenWidth = w;
		screenHeight = h;

		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		
		mCanvas = new Canvas(mBitmap);

		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setStrokeWidth(15);

		savePath = new ArrayList<DrawPath>();
		
		timer = new Timer(true); 
		
		fingerMatrix=new FingerMatrix();
	}

	@Override
	public void onDraw(Canvas canvas) {
		
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if (mPath != null) {
			
			canvas.drawPath(mPath, mPaint);
		}
		
	}

	private void touch_start(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(mY - y);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		
		savePath.add(dp);
		mPath = null;
	}

	
	public void undo() {
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		
		if (savePath != null && savePath.size() > 0) {
			
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();

			
			String fileUrl = Environment.getExternalStorageDirectory()
					.toString() + "/android/data/test.png";
			try {
				FileOutputStream fos = new FileOutputStream(new File(fileUrl));
				mBitmap.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	
	public void redo() {
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (null != task){ 
				task.cancel();  
				task = new TimerTask(){
			       public void run() {  
				       Message message = new Message();      
				       message.what = 1;      
				       handler.sendMessage(message); 
			       }  
				 };
			} 

			
			if(null == fingerMatrix){
				fingerMatrix =new FingerMatrix();
				fingerMatrix.init(x, y);
			}else{
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
			
			mPath = new Path();
			
			dp = new DrawPath();
			dp.path = mPath;
			dp.paint = mPaint;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (null != task){ 
				task.cancel();  
				task = new TimerTask(){
			       public void run() {  
				       Message message = new Message();      
				       message.what = 1;      
				       handler.sendMessage(message); 
			       }  
				 };
			} 

			
			if(null != fingerMatrix){
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			
			if(null != fingerMatrix){
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
			touch_up();
			invalidate();
			
			if(null!=timer){
				if (null != task){ 
					task.cancel();   
					task = new TimerTask(){
				       public void run() {  
					       Message message = new Message();      
					       message.what = 1;      
					       handler.sendMessage(message); 
				       }  
					 };
				} 
				timer.schedule(task,1200, 1200); 
			}
			else{
				timer = new Timer(true);
				timer.schedule(task,1200, 1200); 
			}
			break;
		}
		return true;
	}
	
	
	Timer timer;
	Handler handler = new Handler(){  
      public void handleMessage(Message msg) {  
         switch (msg.what) {      
             case CUT_BITMAP_SEND_TO_ACTIVITY:
            	
            	 Bitmap tempBitmap=mBitmap;
            	 
            	 if(null != fingerMatrix){
            		 float maxX = fingerMatrix.getMaxX();
            		 float minX = fingerMatrix.getMinX();
            		 float maxY = fingerMatrix.getMaxY();
                	 float minY = fingerMatrix.getMinY();
                	 
                	 
                	 int cutMinX = (int) (minX - 15);
                	 int cutMinY = (int) (minY - 15);
                	 int cutMaxX = (int) (maxX + 15);
                	 int cutMaxY = (int) (maxY + 15);
                	
                	 if(cutMinX<0){
                		 cutMinX=1;
                	 }
                	 if(cutMinY<0){
                		 cutMinY=1;
                	 }
//                	 if(cutMaxX>mWidth){
//                		 cutMaxX=mHeight-1;
//                	 }
//                	 if(cutMaxY>mHeight){
//                		 cutMinX=mHeight-1;
//                	 }
                	 if(cutMaxX>tempBitmap.getWidth()){
                		 cutMaxX=tempBitmap.getWidth()-1;
                	 }
                	 if(cutMaxY>tempBitmap.getHeight()){
                		 cutMaxY=tempBitmap.getHeight()-1;
                	 }

                	 
                	 int width = (int)(cutMaxX-cutMinX);
                	 int height = (int)(cutMaxY-cutMinY);
                	 
                	 tempBitmap = Bitmap.createBitmap(tempBitmap, cutMinX, cutMinY, width, height);  
//                	 tempBitmap = Bitmap.createBitmap(tempBitmap, (int)minX, (int)minY, width, height);  
                
            	 }
            	 
            	 fingerMatrix = null;
            	 
//           
     			String fileUrl = Environment.getExternalStorageDirectory()
     					.toString() + "/android/data/test111.png";
     			try {
     				FileOutputStream fos = new FileOutputStream(new File(fileUrl));
     				tempBitmap.compress(CompressFormat.PNG, 100, fos);
     				fos.flush();
     				fos.close();
     			} catch (FileNotFoundException e) {
     				e.printStackTrace();
     			} catch (IOException e) {
     				e.printStackTrace();
     			}
            	 
            	 
            	 Message message = new Message();      
		       	 message.what = 1; 
		       	 Bundle data= new Bundle();
		       	 data.putParcelable("bitmap", tempBitmap);
		       	 message.setData(data);
		         dataHandler.sendMessage(message);
            	 renovate();
                 break;     
             case 2:

            	 
            	 break;
              }      
             super.handleMessage(msg);  
         }    
	}; 
	
	
	TimerTask task = new TimerTask(){  
	       public void run() {  
		       Message message = new Message();      
		       message.what = 1;      
		       handler.sendMessage(message); 
	    }  
	 };
	 
	 
	 
	 private void renovate(){
		
    	 if (savePath != null && savePath.size() > 0) {
    		 savePath.removeAll(savePath);
    		 mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,Bitmap.Config.ARGB_8888);
    		 mCanvas.setBitmap(mBitmap);
    		 
    	 }
		 invalidate();
		
	     if(null!=timer){
	    	 task.cancel();
	     }
	 }
	 
}
