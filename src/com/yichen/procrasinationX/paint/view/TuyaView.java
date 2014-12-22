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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class TuyaView extends View {

	
	
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint mPaint;
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	
	private static List<DrawPath> savePath;
	
	
	private static List<DrawPath> canclePath;
	
	
	private DrawPath dp;

	private int screenWidth, screenHeight;

	private class DrawPath {
		public Path path;
		public Paint paint;
	}
	

//	public static  int color = Color.RED;
	public static  int color = Color.parseColor("#fe0000");
	public static  int srokeWidth = 15;
	
	
	public Paint getPaint() {
		return mPaint;
	}
	
	public void setPaint(Paint mPaint) {
		this.mPaint = mPaint;
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
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(srokeWidth);
		mPaint.setColor(color);
		savePath = new ArrayList<DrawPath>();
		canclePath = new ArrayList<DrawPath>();
	}
	
	private void initPaint(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(srokeWidth);
		mPaint.setColor(color);
	}
	
	
	public TuyaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
	}

	public TuyaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
	}
	
	
	public TuyaView(Context context, int w, int h) {
		super(context);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		init(dm.widthPixels, dm.heightPixels);
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

	
	public int undo() {
		
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		if (savePath != null && savePath.size() > 0) {
			
			DrawPath dPath =savePath.get(savePath.size() - 1);
			canclePath.add(dPath);
			
			
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();
			
		}else{
			return -1;
		}
		return savePath.size();
	}

	public int redo() {
		
		if(canclePath.size()<1)
			return canclePath.size();
		
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		if (canclePath != null && canclePath.size() > 0) {
		
			DrawPath  dPath = canclePath.get(canclePath.size() - 1);
			savePath.add(dPath);
			canclePath.remove(canclePath.size() - 1);
			
			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();
		}
		return canclePath.size();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			initPaint();
			
			canclePath = new ArrayList<DrawPath>();
			
			mPath = new Path();
			
			dp = new DrawPath();
			dp.path = mPath;
			dp.paint = mPaint;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

}
