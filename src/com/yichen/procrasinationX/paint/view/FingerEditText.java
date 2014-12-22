package com.yichen.procrasinationX.paint.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.EditText;
public class FingerEditText extends EditText {
	
	private Paint mPaint;
	private Rect mRect;
	private Context mContext;
	
	public FingerEditText(Context context) {
		super(context);

        mContext = context;
		
	}
	

	public FingerEditText(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();     
        mPaint.setStyle(Paint.Style.STROKE);     
        mPaint.setColor(Color.RED);
        
        mRect =new Rect();
        mContext = context;
	
	}
	public FingerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mPaint = new Paint();     
        mPaint.setStyle(Paint.Style.STROKE);     
        mPaint.setColor(Color.BLUE);
        
        mRect =new Rect();
        
        mContext = context;
	}
	
	
	
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		System.out.println("left:"+left+".top:"+top+".right:"+right+".bottom:"+bottom);
		super.onLayout(changed, left, top, right, bottom);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		int count = getLineCount();
//        Rect r = mRect;
//        Paint paint = mPaint;
//
//        for (int i = 0; i < count; i++) {
//             int baseline = getLineBounds(i, r);
//             canvas.drawLine(r.left, baseline + 5, r.right, baseline + 5, paint);
////           canvas.drawLine(0,this.getHeight()-1,  this.getWidth()-1, this.getHeight()-1, mPaint);  
//        }
        
     
//        canvas.drawLine(0,this.getHeight()-1,  this.getWidth()-1, this.getHeight()-1, mPaint);     
		
		/*int newCount = getLineCount();
//		int myHeight = getHeight();
		int myWidth = getWidth();
		int temp = getHeight() / getLineHeight() - 1;
		newCount = (temp > newCount) ? temp : newCount;
		temp = getLineHeight() + 8;
		mPaint.setColor(Color.RED);
		for (int i = 0; i < newCount + 1; i++) {
			canvas.drawLine(5, temp + 3, myWidth - 5, temp + 3, mPaint);
			temp += getLineHeight();
		}*/
		
		WindowManager wm = (WindowManager) mContext.getSystemService("window");  
        int windowWidth = wm.getDefaultDisplay().getWidth();  
        int windowHeight = wm.getDefaultDisplay().getHeight();  
          
        Paint paint = new Paint();  
        paint.setStyle(Paint.Style.FILL);  
        paint.setColor(Color.BLACK);  
          
        int paddingTop    = getPaddingTop();  
        int paddingBottom = getPaddingBottom();  
          
        int scrollY       = getScrollY();  
        int scrollX       = getScrollX()+windowWidth;  
        int innerHeight   = scrollY + getHeight() - paddingTop - paddingBottom;  
        int lineHeight    = getLineHeight();  
        int baseLine      = scrollY + (lineHeight - (scrollY % lineHeight));  
          
        int x = 8;  
        while (baseLine < innerHeight) {  
            //canvas.drawBitmap(line, x, baseLine + paddingTop, paint);   
            canvas.drawLine(x, baseLine + paddingTop,scrollX, baseLine + paddingTop, paint);  
            baseLine += lineHeight;  
//            baseLine += 115;  
        } 
	}
	
	
	
}
