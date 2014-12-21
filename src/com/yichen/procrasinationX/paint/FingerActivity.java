package com.yichen.procrasinationX.paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yichen.procrasinationX.R;
import com.yichen.procrasinationX.paint.util.Colours;
import com.yichen.procrasinationX.paint.view.FingerEditText;
import com.yichen.procrasinationX.paint.view.FingerView;
import com.yichen.procrasinationX.paint.view.MyHorizontalScrollView;
import com.yichen.procrasinationX.paint.view.OnScrollListener1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class FingerActivity extends Activity implements OnClickListener ,OnScrollListener1{

	private FingerView fingerView = null;
	private FingerEditText fingerEditText = null;
	
	
	private static int mWidth;
	private static int mHeight;
	
	private int editTextLineHeight;
	
	private Paint mPaint;//自定义view中的画笔
	
	private static final int RECEIVE_BITMAP_TO_UPDATE_ACTIVITY = 1;
	
	//
	private static final String TAG = "FingerActivity";
	private Button colourbutton;
	private LinearLayout linearlayout;
	private MyHorizontalScrollView hscrollView;
	private List<Colours> colours = new ArrayList();
	private Colours colour;
	//
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECEIVE_BITMAP_TO_UPDATE_ACTIVITY:
				System.out.println("*********4.activity已收到数据,正在组织数据 *********");
				Bundle bundle = new Bundle();
				bundle = msg.getData();
				Bitmap tempBitmap = bundle.getParcelable("bitmap");
				if (null != tempBitmap) {
					System.out.println("*********5.将bitmap变为指定大小，并将其赋值给edittext *********");
					//获得tempBitmap的大小，如果大就缩小，如果小就方法或不变
					int width = tempBitmap.getWidth();
					int height = tempBitmap.getHeight();
					//得到当前的行高与行宽（估算值），实际画上去的bitmap应该小于行高
					System.out.println("-------------------------------mHeight"+mHeight+"-------------------mWidth"+mWidth);
					int lineHeight =  (int) (mHeight/6f);
					int lineWidth = (int) (mWidth/4f); 
					if(mHeight == 1280 && mWidth == 800){
						lineHeight =  (int) (mHeight/5.4f);
						lineWidth = (int) (mWidth/4f); 
					}
					
//					int lineHeight =  223;
//					int lineWidth = 200; 
//					int lineHeight =  editTextLineHeight-3;
//					int lineWidth = editTextLineHeight-3; 
					
					if(width >= lineWidth && height >= lineWidth){ 
						//整体缩小
						System.out.println("整体缩小");
						tempBitmap = PicZoom(tempBitmap, lineWidth, lineHeight);
					}
					if(width >= lineWidth && height <= lineWidth){
						//宽度缩小
						System.out.println("按宽度缩小");
						tempBitmap = PicZoom3(tempBitmap,  lineWidth);
					}
					if(width <= lineWidth && height >= lineWidth){
						//高度缩小
						System.out.println("按高度缩小");
						tempBitmap = PicZoom2(tempBitmap,  lineHeight);
					}
					if(width <= lineWidth && height <= lineWidth){ 
						System.out.println("整体放大，这里暂时不需要，直接显示小的就行了");
					}
					//添加到edittext上
					System.out.println("绘制bitmap的高度："+tempBitmap.getHeight());
					editInsertBitmap(tempBitmap);
				}
				break;
			}
		}
	};
	
	/**
	 * 不等比缩放bitmap
	 * @param bmp
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {      
        int bmpWidth = bmp.getWidth();      
        int bmpHeght = bmp.getHeight();      
        Matrix matrix = new Matrix();      
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);      
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);      
    } 
	/**
	 * 等比高度缩放
	 * @param bmp
	 * @param height
	 * @return
	 */
	public static Bitmap PicZoom2(Bitmap bmp, int height) {      
        int bmpWidth = bmp.getWidth();      
        int bmpHeght = bmp.getHeight();      
        Matrix matrix = new Matrix();      
        float f= (float) height / bmpHeght;
        matrix.postScale(f, f);      
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);      
    }  

	/**
	 * 等比宽度缩放
	 * @param bmp
	 * @param height
	 * @return
	 */
	public static Bitmap PicZoom3(Bitmap bmp, int width ) {      
        int bmpWidth = bmp.getWidth();      
        int bmpHeght = bmp.getHeight();      
        Matrix matrix = new Matrix();      
        float f= (float) width / bmpWidth;
        matrix.postScale(f, f);      
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);      
    } 
	

	/**
	 * 将bitmap添加到自定义edittext中
	 * @param bitmap
	 */
	public void editInsertBitmap(Bitmap bitmap) {
		SpannableString ss = new SpannableString("1");
		// Drawable d = getResources().getDrawable(R.drawable.icon);
		// d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(bitmap, ImageSpan.ALIGN_BOTTOM);
		ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		fingerEditText.append(ss);
		fingerEditText.setPadding(0, 3, 0, 0);
	}
	
	/**
	 * 删除fingerEditText中，最后一个bitmap
	 */
	public void deletFingerEditText(){
		Editable editable = fingerEditText.getText();
		//删除最后一个
		int end = fingerEditText.getSelectionEnd();
		if(end<1)
			return;
//		editable.delete(end-1, end);
//		editable.replace(end-1, end, "");
		CharSequence charSequence =editable.subSequence(0, end-1);
		fingerEditText.setText(charSequence);
		fingerEditText.setSelection(end-1);
		System.out.println(end);
	}
	

//	private void insertIntoEditText(SpannableString ss) {
//		Editable et = fingerEditText.getText();// 先获取Edittext中的内容
//		int start = fingerEditText.getSelectionStart();
//		et.insert(start, ss);// 设置ss要添加的位置
//		fingerEditText.setText(et);// 把et添加到Edittext中
//		fingerEditText.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
//	}
//
//	/**
//	 * 图片转成SpannableString加到EditText中
//	 * 
//	 * @param pic
//	 * @param uri
//	 * @return
//	 */
//	private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
//		int imgWidth = pic.getWidth();
//		int imgHeight = pic.getHeight();
//		float scalew = (float) 40 / imgWidth;
//		float scaleh = (float) 40 / imgHeight;
//		Matrix mx = new Matrix();
//		mx.setScale(scalew, scaleh);
//		pic = Bitmap.createBitmap(pic, 0, 0, imgWidth, imgHeight, mx, true);
//		String smile = uri.getPath();
//		SpannableString ss = new SpannableString(smile);
//		ImageSpan span = new ImageSpan(this, pic);
//		ss.setSpan(span, 0, smile.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		return ss;
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tuya_finger_layout);
		super.onCreate(savedInstanceState);
		
		
		Display display = this.getWindowManager().getDefaultDisplay();
		mWidth = display.getWidth();
		mHeight = display.getHeight();
		
		fingerView = (FingerView) this.findViewById(R.id.fingerView);
		fingerView.setDataHandler(handler); //设置activity的handler
		fingerView.setScreenSize(mWidth, mHeight); //设置屏幕的尺寸
		
		
		fingerEditText = (FingerEditText) this.findViewById(R.id.fingerEditText);
		editTextLineHeight= fingerEditText.getLineHeight();
		System.out.println("行高为："+editTextLineHeight);
		
		mPaint =fingerView.getmPaint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
		mPaint.setStrokeWidth(15);// 画笔宽度
//		mPaint.setColor(Color.BLUE);
		mPaint.setColor(Color.parseColor("#011eff"));
		
		//
		colourbutton = (Button)this.findViewById(R.id.colour);
        linearlayout = (LinearLayout)this.findViewById(R.id.ScrollView01);
        hscrollView = (MyHorizontalScrollView)this.findViewById(R.id.HorizontalScrollView01);
        colourbutton.setOnClickListener(this);
		hscrollView.setOnScrollListener(this);
		initColourButton();
		//
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {// 返回键
//			tuyaView.undo(); //撤销上次操作
//			return true;
//		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(linearlayout.getVisibility()==View.VISIBLE){
				linearlayout.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
			case R.id.button_delete:
				deletFingerEditText();
				break;
//			case R.id.button_opentuya:
//				Intent openIntent = new Intent(this,TuyaActivity.class);
//				startActivity(openIntent);
//				break;
			case R.id.colour://颜色按钮
				if(linearlayout.getVisibility()==View.INVISIBLE){
					linearlayout.setVisibility(View.VISIBLE);
				}else{
					linearlayout.setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.sendbutton:
				//发送按钮
				sendFingerBitmap();
				break;
		}
		//
		int j = -1;
		for(int i=0;i<colours.size();i++){
			if(v.getId()==colours.get(i).getTag()){
				j=i;
			}
		}
		if (j != -1) {
			for (int i = 0; i < colours.size(); i++) {
				colours.get(i).getButtonbg().setVisibility(View.INVISIBLE);
			}
			colours.get(j).getButtonbg().setVisibility(View.VISIBLE);
			//改变颜色
			colours.get(j).getName();
			Log.i(TAG, ""+colours.get(j).getName());
			mPaint.setColor(Color.parseColor("#"+colours.get(j).getName()));
			return;
		}
	}
	/**
	 * 发送
	 */
	private void sendFingerBitmap() {
		boolean drawingCacheEnabled = fingerEditText.isDrawingCacheEnabled();
		  if(!drawingCacheEnabled){
			  fingerEditText.setDrawingCacheEnabled(true);
			  fingerEditText.buildDrawingCache();
		  }
		   //获得当前的Bitmap对象
		  fingerEditText.setFocusable(false);
		  Bitmap bitmap = fingerEditText.getDrawingCache();
		  if(bitmap!=null){
			   //读取SD卡状态
			   boolean sdCardIsMounted = android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
			   if (!sdCardIsMounted){
				   Toast.makeText(this, "请插入存储卡！", 1000).show();
			   }else{
				   //保存到SD卡
				   String filepath =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/caiman/shouxie.jpg";
		   		   try {
//		    	   			fingerEditText.setFocusableInTouchMode(false);
			   			FileOutputStream fos = new FileOutputStream(new File(filepath));
			   			bitmap.compress(CompressFormat.JPEG, 100, fos);
			   			fos.flush();
			   			fos.close();
			   			Toast.makeText(this, "保存成功！", 1000).show();
			   			//回归光标
			   			fingerEditText.setFocusable(true);
			   			fingerEditText.setFocusableInTouchMode(true);
			   			fingerEditText.requestFocus();
			   			//跳转页面
			   			
			   			//计算字体的位置
			   			TextPaint paint=fingerEditText.getPaint();
			   			float sss = paint.measureText(fingerEditText.getText(), 0, fingerEditText.length());
			   			System.out.println("当前光标位置为："+sss);
			   			
			   			Paint pp = fingerEditText .getPaint();
			   			float textWidth = pp.measureText(fingerEditText.getText().toString());
			   			System.out.println("paint打印出来的位置："+ textWidth);
		   			 
		   			} catch (FileNotFoundException e) {
		   				e.printStackTrace();
		   			} catch (IOException e) {
		   				e.printStackTrace();
		   			}
			   }
		   }else{
			   Toast.makeText(this, "未能发送图片，请重试！", 1000).show();
		   }
	}
	@Override
	public void onLeft() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRight() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScroll() {
		// TODO Auto-generated method stub
		
	}
	private void initColourButton() {
		// TODO Auto-generated method stub
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button01));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview01));
		colour.setName("140c09");
		colour.setTag(R.id.button01);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button02));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview02));
		colour.setName("fe0000");
		colour.setTag(R.id.button02);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button03));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview03));
		colour.setName("ff00ea");
		colour.setTag(R.id.button03);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button04));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview04));
		colour.setName("011eff");
		colour.setTag(R.id.button04);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button05));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview05));
		colour.setName("00ccff");
		colour.setTag(R.id.button05);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button06));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview06));
		colour.setName("00641c");
		colour.setTag(R.id.button06);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button07));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview07));
		colour.setName("9bff69");
		colour.setTag(R.id.button07);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button08));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview08));
		colour.setName("f0ff00");
		colour.setTag(R.id.button08);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button09));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview09));
		colour.setName("ff9c00");
		colour.setTag(R.id.button09);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button10));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview10));
		colour.setName("ff5090");
		colour.setTag(R.id.button10);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button11));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview11));
		colour.setName("9e9e9e");
		colour.setTag(R.id.button11);
		colours.add(colour);
		
		colour = new Colours();
		colour.setButton((Button)this.findViewById(R.id.button12));
		colour.setButtonbg((ImageView)this.findViewById(R.id.imageview12));
		colour.setName("f5f5f5");
		colour.setTag(R.id.button12);
		colours.add(colour);
		
		
		for(int i=0;i<colours.size();i++){
			colours.get(i).getButton().setOnClickListener(this);
			colours.get(i).getButtonbg().setVisibility(View.INVISIBLE);
		}
		colours.get(3).getButtonbg().setVisibility(View.VISIBLE);
		
	}
}
