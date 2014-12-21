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
	private Paint mBitmapPaint;// 画布的画笔
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标
	private static final float TOUCH_TOLERANCE = 4;

	// 保存Path路径的集合,用List集合来模拟栈，用于后退步骤
	private static List<DrawPath> savePath;
	
	// 保存Path路径的集合,用List集合来模拟栈,用于前进步骤
	private static List<DrawPath> canclePath;
	
	// 记录Path路径的对象
	private DrawPath dp;

	private int screenWidth, screenHeight;// 屏幕長寬

	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
	}
	
	//背景颜色
//	public static  int color = Color.RED;
	public static  int color = Color.parseColor("#fe0000");
	public static  int srokeWidth = 15;
	
	/**
	 * 得到画笔
	 * @return
	 */
	public Paint getPaint() {
		return mPaint;
	}
	/**
	 * 设置画笔
	 * @param mPaint
	 */
	public void setPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}
	
	private void init(int w, int h) {
		screenWidth = w;
		screenHeight = h;
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		// 保存一次一次绘制出来的图形
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
//		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(srokeWidth);// 画笔宽度
		mPaint.setColor(color);
		savePath = new ArrayList<DrawPath>();
		canclePath = new ArrayList<DrawPath>();
	}
	
	private void initPaint(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
//		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(srokeWidth);// 画笔宽度
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
		//背景颜色，这里颜色应该是
//		canvas.drawColor(color);
		// 将前面已经画过得显示出来
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if (mPath != null) {
			// 实时的显示
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
			// 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		// 将一条完整的路径保存下来(相当于入栈操作)
		savePath.add(dp);
		mPath = null;// 重新置空
	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public int undo() {
		
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉…
		if (savePath != null && savePath.size() > 0) {
			
			DrawPath dPath =savePath.get(savePath.size() - 1);
			canclePath.add(dPath);
			
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
			
		}else{
			return -1;
		}
		return savePath.size();
	}

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public int redo() {
		// 如果撤销你懂了的话，那就试试重做吧。
		if(canclePath.size()<1)
			return canclePath.size();
		
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉…
		if (canclePath != null && canclePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			DrawPath  dPath = canclePath.get(canclePath.size() - 1);
			savePath.add(dPath);
			canclePath.remove(canclePath.size() - 1);
			
			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
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
			//重置下一步操作
			canclePath = new ArrayList<DrawPath>();
			// 每次down下去重新new一个Path
			mPath = new Path();
			// 每一次记录的路径对象是不一样的
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
