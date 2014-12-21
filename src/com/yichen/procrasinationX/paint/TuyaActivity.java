package com.yichen.procrasinationX.paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yichen.procrasinationX.R;
import com.yichen.procrasinationX.paint.util.BigSize;
import com.yichen.procrasinationX.paint.util.Colours;
import com.yichen.procrasinationX.paint.view.MyHorizontalScrollView;
import com.yichen.procrasinationX.paint.view.OnScrollListener1;
import com.yichen.procrasinationX.paint.view.TuyaView;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class TuyaActivity extends Activity implements OnClickListener,OnScrollListener1{

	private ImageView imageview_background;
	private TuyaView tuyaView; 
	FrameLayout tuyaFrameLayout =null;
	
	private static final int UNDO_PATH = 1;
	private static final int REDO_PATH = 2;
	private static final int USE_ERASER = 3;
	private static final int USE_PAINT = 4;
	
	//
	private static final String TAG = "TuyaActivity";
	private LinearLayout linearlayout;
	private Button colourtag;
	private Button bigtag;
	
	private ScrollView scrollviewcolour;
	private ScrollView scrollviewbig;
	
	private MyHorizontalScrollView hscrollViewcolour;
	private MyHorizontalScrollView hscrollViewsize;
	
	private List<Colours> colours = new ArrayList();
	private Colours colour;
	private List<BigSize> sizes = new ArrayList();
	private BigSize size;
	private int index;
	//
	private int CANCLE_BACKGROUND_IMAGE = 0;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UNDO_PATH:
					int undo = tuyaView.undo(); //撤销上次操作
					System.out.println("可以撤销："+undo);
					if(undo<0){
						CANCLE_BACKGROUND_IMAGE ++;
						switch (CANCLE_BACKGROUND_IMAGE) {
						case 0:
							break;
						case 1:
							System.out.println("设置imageview为默认");
							imageview_background.setBackgroundColor(defaultColor);
					    	imageview_background.setImageBitmap(null);
					    	CANCLE_BACKGROUND_IMAGE =0;
							break;
						}
					}
					
					break;
				case REDO_PATH:
					int redo = tuyaView.redo(); //返回上次操作
					System.out.println("可以前进："+ redo);
					if(redo<1){
						//设置按钮不可用
					}
					break;
				case USE_ERASER:
//					TuyaView.color=Color.CYAN;
					if(linearlayout.getVisibility()==View.VISIBLE){
						linearlayout.setVisibility(View.GONE);
					}
					TuyaView.color=Color.parseColor("#C9DDFE");
					TuyaView.srokeWidth = 15;
//					Paint eraser = tuyaView.getPaint();
//					eraser.setColor(Color.CYAN);
					break;
				case USE_PAINT:
					if(linearlayout.getVisibility()==View.GONE){
						linearlayout.setVisibility(View.VISIBLE);
						/*for(int i=0;i<sizes.size();i++){
							if(sizes.get(i).getButton().getBackground()==
								getResources().getDrawable(R.drawable.brushsizeselectedbg)){
								TuyaView.srokeWidth = sizes.get(i).getName()+10;
								break;
							}
						}*/
						TuyaView.srokeWidth = sizes.get(index).getName()+10;
						for(int i=0;i<colours.size();i++){
							if(colours.get(i).getButtonbg().getVisibility()==View.VISIBLE){
								TuyaView.color = Color.parseColor("#"+colours.get(i).getName());
								break;
							}
						}
					}else{
						linearlayout.setVisibility(View.GONE);
					}
					break;
			}
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuya_layout);
		
		tuyaFrameLayout =(FrameLayout) findViewById(R.id.tuya_layout);
		
		imageview_background = (ImageView) findViewById(R.id.imageview_background);
		tuyaView=(TuyaView)findViewById(R.id.tuyaView);
		
		//
        colourtag = (Button)this.findViewById(R.id.colourtag);
        bigtag = (Button)this.findViewById(R.id.bigtag);
        
        scrollviewcolour = (ScrollView)this.findViewById(R.id.scrollviewcolour);
        scrollviewbig = (ScrollView)this.findViewById(R.id.scrollviewbig);
        hscrollViewcolour = (MyHorizontalScrollView)this.findViewById(R.id.HorizontalScrollView01);
        hscrollViewcolour.setOnScrollListener(this);
        hscrollViewsize = (MyHorizontalScrollView)this.findViewById(R.id.HorizontalScrollView02);
        hscrollViewsize.setOnScrollListener(this);
        
        linearlayout = (LinearLayout)this.findViewById(R.id.ScrollView01);
        
        initColourButton();
	}
	
	private final int defaultColor=Color.parseColor("#C9DDFE");
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode){
			case 10:
				if(resultCode == Activity.RESULT_CANCELED)
					return;
			    if(data==null)
			    	return;
				Uri uri = data.getData();  
			    if(uri==null)
			    	return;
			    String[] proj = { MediaStore.Images.Media.DATA }; 
			    Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
			    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
			    actualimagecursor.moveToFirst();  
			    String img_path = actualimagecursor.getString(actual_image_column_index);  
			    File file = new File(img_path);  
			    if (file.exists()){
			    	System.out.println("图片路径为："+img_path);
			    	//设置背景图
			    	Bitmap bitmap = loadFromSdCard(img_path);
			    	imageview_background.setBackgroundColor(defaultColor);
			    	imageview_background.setImageBitmap(bitmap);
			    }
				break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {// 返回键
//			this.finish();
//			int undo = tuyaView.undo(); //撤销上次操作
//			if(undo<1){
//				System.out.println("设置imageview为默认");
//				imageview_background.setBackgroundColor(0xff99CCCC);
//		    	imageview_background.setImageBitmap(null);
//			}
//			return true;
//		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(linearlayout.getVisibility()==View.VISIBLE){
				linearlayout.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.button_changebg:
				System.out.println("打开图库");
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);   
				intent.addCategory(Intent.CATEGORY_OPENABLE);   
				intent.setType("image/*");  
				startActivityForResult(intent, 10); 
				break;
			case R.id.button_undo:
				Message undo_message = new Message();      
				undo_message.what = UNDO_PATH;      
			    handler.sendMessage(undo_message); 
				break;
			case R.id.button_redo:
				Message redo_message = new Message();      
				redo_message.what = REDO_PATH;      
			    handler.sendMessage(redo_message); 
				break;
			case R.id.button_eraser:
				Message eraser_message = new Message();      
				eraser_message.what = USE_ERASER;      
			    handler.sendMessage(eraser_message); 
				break;
			case R.id.button_pen:
				Message pen_message = new Message();      
				pen_message.what = USE_PAINT;      
			    handler.sendMessage(pen_message); 
				break;
			case R.id.colourtag:
				if(linearlayout.getVisibility()==View.VISIBLE){
					scrollviewcolour.setVisibility(View.VISIBLE);
					scrollviewbig.setVisibility(View.GONE);
					colourtag.setBackgroundResource(R.drawable.tuya_selectedtrue);
					bigtag.setBackgroundResource(R.drawable.tuya_selectedfalse);
				}
				break;
			case R.id.bigtag:
				if(linearlayout.getVisibility()==View.VISIBLE){
					scrollviewcolour.setVisibility(View.GONE);
					scrollviewbig.setVisibility(View.VISIBLE);
					bigtag.setBackgroundResource(R.drawable.tuya_selectedtrue);
					colourtag.setBackgroundResource(R.drawable.tuya_selectedfalse);
				}
				break;
			case R.id.sendbutton:
				sendTuyaBitmap();
				break;
		}
		//颜色
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
			TuyaView.color = Color.parseColor("#"+colours.get(j).getName());
			return;
		}
		
		//大小
		for(int i=0;i<sizes.size();i++){
			if(v.getId()==sizes.get(i).getTag()){
				j=i;
			}
		}
		if (j != -1) {
			for (int i = 0; i < sizes.size(); i++) {
				sizes.get(i).getButton().setBackgroundResource(0);
			}
			sizes.get(j).getButton().setBackgroundResource(R.drawable.tuya_brushsizeselectedbg);
			//改变大小
			sizes.get(j).getName();
			Log.i(TAG, ""+sizes.get(j).getName());
			TuyaView.srokeWidth=sizes.get(j).getName()+10;
			index = j;
			return;
		}
	}


	/**
	 * 发送图片
	 */
	private void sendTuyaBitmap() {
		//发送按钮
		 boolean drawingCacheEnabled = tuyaFrameLayout.isDrawingCacheEnabled();
		  if(!drawingCacheEnabled){
			  tuyaFrameLayout.setDrawingCacheEnabled(true);
			  tuyaFrameLayout.buildDrawingCache();
		  }
		   //获得当前的Bitmap对象
		  Bitmap bitmap = tuyaFrameLayout.getDrawingCache();
		  if(bitmap!=null){
			   //读取SD卡状态
			   boolean sdCardIsMounted = android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
			   if (!sdCardIsMounted){
				   Toast.makeText(this, "请插入存储卡！", 1000).show();
			   }else{
				   //保存到SD卡
				   String filepath =  Environment.getExternalStorageDirectory() + "/caiman/tuya.jpg";
		   		   try {
		   				FileOutputStream fos = new FileOutputStream(new File(filepath));
		   				bitmap.compress(CompressFormat.JPEG, 100, fos);
		   				fos.flush();
		   				fos.close();
		   			    Toast.makeText(this, "保存成功！", 1000).show();
		   			    //跳转到对应界面 
		   			
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
	
	
	private Bitmap loadFromSdCard(String filePath) {
		File file = new File(filePath);
		Bitmap bmp = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			bmp = BitmapFactory.decodeStream(fis);
			if (bmp != null) {
				return bmp;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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
		colours.get(1).getButtonbg().setVisibility(View.VISIBLE);
		
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton01));
		size.setName(15);
		size.setTag(R.id.sizebutton01);
		sizes.add(size);
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton02));
		size.setName(10);
		size.setTag(R.id.sizebutton02);
		sizes.add(size);
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton03));
		size.setName(5);
		size.setTag(R.id.sizebutton03);
		sizes.add(size);
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton04));
		size.setName(0);
		size.setTag(R.id.sizebutton04);
		sizes.add(size);
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton05));
		size.setName(-5);
		size.setTag(R.id.sizebutton05);
		sizes.add(size);
		size = new BigSize();
		size.setButton((Button)this.findViewById(R.id.sizebutton06));
		size.setName(-10);
		size.setTag(R.id.sizebutton06);
		sizes.add(size);
		for(int i=0;i<sizes.size();i++){
			sizes.get(i).getButton().setOnClickListener(this);
			sizes.get(i).getButton().setBackgroundResource(0);
		}
		sizes.get(2).getButton().setBackgroundResource(R.drawable.tuya_brushsizeselectedbg);
		index = 2;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		TuyaView.color = Color.parseColor("#fe0000");
		TuyaView.srokeWidth = 15;
	}
}