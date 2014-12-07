package com.yichen.procrasinationX.note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.yichen.procrasinationX.model.Attachment;
import com.yichen.procrasinationX.model.Note;
import com.yichen.procrasinationX.R;

/**
 * @author appsrox.com
 *
 */
public class SnapshotActivity extends BaseActivity implements AdapterView.OnItemClickListener {
	
	//private static final String TAG = "SnapshotActivity";
	
	public static final int TAKE_SNAPSHOT = 1;
	public static final String TEMP_IMAGE = "snapshot.jpg";
	private Uri tempImageUri;// on internal storage
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note.setType(Note.SNAPSHOT);
        
		gallery.setOnItemClickListener(this);
		addItemBtn.setImageResource(R.drawable.ksnapshot);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, PhotoActivity.class);
		intent.putExtra(Attachment.COL_ID, id);
		startActivity(intent);
	}    
	
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.additem_btn:
            try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				openFileOutput(TEMP_IMAGE, MODE_WORLD_WRITEABLE).close();
				tempImageUri = Uri.fromFile(getFileStreamPath(TEMP_IMAGE));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);                	
                startActivityForResult(intent, TAKE_SNAPSHOT);
                
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No camera app found!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
            	Toast.makeText(getApplicationContext(), "Unable to write file on internal storage", Toast.LENGTH_LONG).show();
			}
			break;
			
		case R.id.deleteitem_btn:
			break;			
		}
	}
	
	protected void reset() {
		super.reset();
		contentEdit.setVisibility(View.GONE);
		checklistLL.setVisibility(View.GONE);
		
	    Display disp = getWindowManager().getDefaultDisplay();
	    final int screenW = disp.getWidth();
	    final int screenH = disp.getHeight();		
		
		if (note.getId() > 0) {
			Cursor c = Attachment.list(db, String.valueOf(note.getId()));
			startManagingCursor(c);
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					this, 
					R.layout.gallery_item, 
					c, 
					new String[]{Attachment.COL_URI}, 
					new int[]{R.id.image});
			
			adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					ImageView iv = (ImageView) view;
					iv.setAdjustViewBounds(true);
					iv.setMaxHeight(screenH/2);
					iv.setMaxWidth(screenW-50);
					return false;
				}
			});
			gallery.setAdapter(adapter);
		}
	}
	
	protected void saveAttachment(Uri snapshotUri) {
		if (snapshotUri == null) return;
		
		if (note.getId() <= 0)
			super.persist();
		
		Attachment attachment = new Attachment(); 
		attachment.setNoteId(note.getId());
		attachment.setUri(snapshotUri.toString());
		attachment.setId(attachment.persist(db));
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_SNAPSHOT) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempImageUri != null) {
                	// write snapshot to SD card
                	Bitmap bitmap = getBitmap(tempImageUri);
                	if (bitmap != null) {
                		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                			
                			FileOutputStream fos = null;
                    		try {
								File rootPath = new File(Environment.getExternalStorageDirectory(), "SmartPad" + File.separator + "images");
								if (!rootPath.exists()) 
									rootPath.mkdirs();
								
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");
								String snapshotImage = df.format(new Date()) + ".jpg";
								File snapshotFile = new File(rootPath, snapshotImage);

								fos = new FileOutputStream(snapshotFile);
								bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
								
								// persist snapshot as attachment
								saveAttachment(Uri.fromFile(snapshotFile));
								
							} catch (FileNotFoundException e) {
								Toast.makeText(this, "Unable to write file on external storage", Toast.LENGTH_LONG).show();
							} finally {
								if (fos != null) {
									try {
										fos.close();
									} catch (IOException e) {}
								}
							}
            				
                		} else {
                			Toast.makeText(this, "Unable to write file on external storage", Toast.LENGTH_LONG).show();
                		}               		
                	} else {
                		Toast.makeText(this, "Unable to decode image", Toast.LENGTH_LONG).show();
                	}
                }
            } else {
            	Toast.makeText(this, "Unable to capture image", Toast.LENGTH_LONG).show();
            }
            
            tempImageUri = null;
            deleteFile(TEMP_IMAGE);
        }
    }
    
	private Bitmap getBitmap(Uri imageUri) {
		try {
		    // Get the dimensions of the bitmap
		    BitmapFactory.Options opts = new BitmapFactory.Options();
		    opts.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(imageUri.getPath(), opts);
		    int photoW = opts.outWidth;
		    int photoH = opts.outHeight;
		    
		    // Get the dimensions of the screen
		    Display disp = getWindowManager().getDefaultDisplay();
		    int screenW = disp.getWidth();
		    int screenH = disp.getHeight();
		    
		    // Determine how much to scale down the image
		    int scaleFactor = Math.max(photoW, photoH) / Math.min(screenW, screenH);
		  
		    // Decode the image file into a Bitmap sized to fill the View
		    opts.inJustDecodeBounds = false;
		    opts.inSampleSize = scaleFactor * 1;
		    opts.inPurgeable = true;
		    
			return BitmapFactory.decodeFile(imageUri.getPath(), opts);
			
		} catch (Exception e) {
		}
		
		return null;
	}
	
	boolean canSave() {
		return !TextUtils.isEmpty(titleEdit.getText());
	}

}
