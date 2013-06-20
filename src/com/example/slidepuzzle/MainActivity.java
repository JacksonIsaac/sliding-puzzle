package com.example.slidepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.slidepuzzle.R;

public class MainActivity extends Activity {
	
	private static final int CAMERA_REQUEST_CODE = 100;
	private static final int GALLERY_REQUEST_CODE = 2000;
	private final String CAMERA_FILE_NAME = "camera_file";
	private Uri mCapturedImageURI;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Disable all animations
		getWindow().setWindowAnimations(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_exit) {
			this.finish();
		}
		return true;
	}
	
	public void startPuzzleWithIncludedImage(View v) {
		Intent intent = new Intent(this, PuzzleActivity.class);

		switch(v.getId()) {
		case R.id.city_Button:
			intent.putExtra("image", Integer.valueOf(R.drawable.city));
			break;
		case R.id.clouds_Button:
			intent.putExtra("image", Integer.valueOf(R.drawable.clouds));
			break;
		case R.id.grid_Button:
			intent.putExtra("image", Integer.valueOf(R.drawable.grid));
			break;
		case R.id.tree_Button:
			intent.putExtra("image", Integer.valueOf(R.drawable.tree));
			break;
		}

		startActivity(intent);
	}
	
	public void selectFromGallery(View v) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(i, GALLERY_REQUEST_CODE); 
	}
	
	public void useCamera(View v) {
		
        	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		ContentValues values = new ContentValues();
		values.put(android.provider.MediaStore.Images.Media.TITLE,CAMERA_FILE_NAME);
		
		mCapturedImageURI = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,	mCapturedImageURI); 

		startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
	
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data); 

	    String imageFilePath = null;
	    
    	switch(requestCode) { 
	    case GALLERY_REQUEST_CODE:
	    	if (resultCode == RESULT_OK) {
	    		imageFilePath = getFilePath(data.getData());
	    	}
	        break;
	    
	    case CAMERA_REQUEST_CODE:
	    	if (resultCode == RESULT_OK) {
				imageFilePath = getFilePath(this.mCapturedImageURI);
				 
				Toast.makeText(this, "Image saved" + data.getData(),Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				getContentResolver().delete(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"TITLE = '" + CAMERA_FILE_NAME + "' ", null);
			} else {
				// Image capture failed, advise user
			}

			break;
	    };
	    
	    if (imageFilePath != null) {
	    	// start puzzle activity using image at imageFilePath
	    	Intent intent = new Intent(this, PuzzleActivity.class);
			intent.putExtra("image", imageFilePath);

			startActivity(intent);
	    }
	}
	
	private String getFilePath(Uri selectedImage) {
		String[] filePathColumn = {MediaStore.Images.Media.DATA};
		
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        
        return filePath;
	}
}
