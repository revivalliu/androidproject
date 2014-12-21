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
//	        int diff = (view.getLeft()-getScrollX());// 如果为0，证明滑动到最左边
//	        int diff = (view.getRight()-(getWidth()+getScrollX()));// 如果为0证明滑动到最右边
	        if(view.getLeft()-getScrollX()==0){// 如果为0，证明滑动到最左边
	        	onScrollListener.onLeft();
	        	Log.d("TAG", "最左边");
	        }else if((view.getRight()-(getWidth()+getScrollX()))==0){//如果为0证明滑动到最右边
	        	onScrollListener.onRight();
	          	Log.d("TAG", "最右边");
	        }else{//说明在中间
	        	onScrollListener.onScroll();
	        	Log.d("TAG", "中间");
	        }
	        super.onScrollChanged(l, t, oldl, oldt);
	}
	    
	    /**
	     * 定义接口
	     * @author admin
	     *
	     */
//	    public interface OnScrollListener1{
//	    	void onRight();
//	    	void onLeft();
//	    	void onScroll();
//	    }
	    private OnScrollListener1 onScrollListener;
	    public void setOnScrollListener(OnScrollListener1 onScrollListener){
	    	this.onScrollListener=onScrollListener;
	    }
}
