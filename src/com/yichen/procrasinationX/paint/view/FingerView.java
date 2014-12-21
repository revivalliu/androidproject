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

	//这个handler主要是为了给activity传递数据
	public Handler dataHandler;
	public Handler getDataHandler() {
		return dataHandler;
	}
	public void setDataHandler(Handler dataHandler) {
		this.dataHandler = dataHandler;
	}
	
	
	FingerMatrix fingerMatrix;  //用于计算触摸矩阵坐标
	
	
	
	//用于对外部activity设置画笔的对应属性
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

	/****************************/
	//背景颜色
	public static  int color = Color.RED;
	public static  int srokeWidth = 15;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;// 画布的画笔
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标
	private static final float TOUCH_TOLERANCE = 4;

	// 保存Path路径的集合,用List集合来模拟栈
	private static List<DrawPath> savePath;
	// 记录Path路径的对象
	private DrawPath dp;

	private int screenWidth, screenHeight;// 屏幕長寬

	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
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

//	public FingerView(Context context, int w, int h) {
//		super(context);
//		init(w, h);
//	}

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

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStrokeWidth(15);// 画笔宽度

		savePath = new ArrayList<DrawPath>();
		
		timer = new Timer(true); //初始化timer
		
		fingerMatrix=new FingerMatrix();
	}

	@Override
	public void onDraw(Canvas canvas) {
		//canvas.drawColor(color);//背景颜色，不设置为透明，否则看不到后面的自定义edittext了
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
	public void undo() {
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉…
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新

			/* 在这里保存图片纯粹是为了方便,保存图片进行验证 */
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

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public void redo() {
		// 如果撤销你懂了的话，那就试试重做吧。
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (null != task){ 
				task.cancel();  //将原任务从队列中移除 
				task = new TimerTask(){
			       public void run() {  
				       Message message = new Message();      
				       message.what = 1;      
				       handler.sendMessage(message); 
			       }  
				 };
			} 

			System.out.println("触摸坐标-----====X坐标:" +x +"----====Y坐标:"+y);
			if(null == fingerMatrix){
				fingerMatrix =new FingerMatrix();
				fingerMatrix.init(x, y);
			}else{
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
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
			if (null != task){ 
				task.cancel();  //将原任务从队列中移除 
				task = new TimerTask(){
			       public void run() {  
				       Message message = new Message();      
				       message.what = 1;      
				       handler.sendMessage(message); 
			       }  
				 };
			} 

			System.out.println("滑动时坐标-----====X坐标:" +x +"----====Y坐标:"+y);
			if(null != fingerMatrix){
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("抬起坐标-----====X坐标:" +x +"----====Y坐标:"+y);
			if(null != fingerMatrix){
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}
			
			touch_up();
			invalidate();
			
			if(null!=timer){
				if (null != task){ 
					task.cancel();  //将原任务从队列中移除 
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
            	 System.out.println("*********0.handler发消息告诉activity处理数据 *********");
            	 Bitmap tempBitmap=mBitmap;
            	 //1.得到绘制的区域坐标
            	 if(null != fingerMatrix){
            		 float maxX = fingerMatrix.getMaxX();
            		 float minX = fingerMatrix.getMinX();
            		 float maxY = fingerMatrix.getMaxY();
                	 float minY = fingerMatrix.getMinY();
                	 System.out.println("矩阵的坐标信息为：-======maxX:"+maxX+"----====maxY:"+maxY+"----====minX:"+minX+"----====minY:"+minY); 
                	 
                	 int cutMinX = (int) (minX - 15);
                	 int cutMinY = (int) (minY - 15);
                	 int cutMaxX = (int) (maxX + 15);
                	 int cutMaxY = (int) (maxY + 15);
                	 //处理设置裁剪位置
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
                	 System.out.println("裁剪bitmap成功");
            	 }
            	 //2.重置位置矩阵信息
            	 fingerMatrix = null;
            	 
//            	 //这里目前传递的是全屏的bitmap，应该为绘制最小x,y轴到最大xy轴的区域 +一定空白区域的图片
//            	 for(int i=0;i<savePath.size();i++){
//            		 DrawPath  tempDrawPath= savePath.get(i);
//            		 Path tempPath = new Path(); 
//            		 tempPath= tempDrawPath.path;
//            	 }
            	 
            	/* 在这里保存图片纯粹是为了方便,保存图片进行验证 */
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
	 
	 
	 /**
	  * 初始化数据并刷新屏幕
	  */
	 private void renovate(){
		 System.out.println("*********1.初始化数据并刷新屏幕 *********");
    	 if (savePath != null && savePath.size() > 0) {
    		 savePath.removeAll(savePath);
    		 mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,Bitmap.Config.ARGB_8888);
    		 mCanvas.setBitmap(mBitmap);
    		 System.out.println("*********2.初始化数据成功 *********");
    	 }
		 invalidate();// 刷新屏幕
		 System.out.println("*********3.更新屏幕数据成功 *********");
	     if(null!=timer){
	    	 task.cancel();
	     }
	 }
	 
}
