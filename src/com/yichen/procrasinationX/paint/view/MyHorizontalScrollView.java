package com.yichen.procrasinationX.paint.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
public class MyHorizontalScrollView extends HorizontalScrollView{
	public MyHorizontalScrollView(Context context) {
		super(context);
	}
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}
	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
	        View view = (View) getChildAt(getChildCount()-1);

	        if(view.getLeft()-getScrollX()==0){
	        	onScrollListener.onLeft();
	        	Log.d("TAG", "leftmost");
	        }else if((view.getRight()-(getWidth()+getScrollX()))==0){
	        	onScrollListener.onRight();
	          	Log.d("TAG", "rightmost");
	        }else{
	        	onScrollListener.onScroll();
	        	Log.d("TAG", "center");
	        }
	        super.onScrollChanged(l, t, oldl, oldt);
	}
	    
	    

	    private OnScrollListener1 onScrollListener;
	    public void setOnScrollListener(OnScrollListener1 onScrollListener){
	    	this.onScrollListener=onScrollListener;
	    }
}
