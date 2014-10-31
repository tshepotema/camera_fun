package com.example.funcam;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoTake extends Activity {

	VideoView vvClip;
	Button btClose, btListVideos, btNewVideo;
	
	Intent shootVideo;
	
	private Uri fileUri;
	
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	boolean bTakeVideo = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_camera);
		
		initializeLayout();
		
		if (bTakeVideo) {
		    //create new Intent
		    shootVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	
		    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
		    shootVideo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
	
		    shootVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
	
		    // start the Video Capture Intent
		    startActivityForResult(shootVideo, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);		
		}
	}

	private void initializeLayout() {		
		vvClip = (VideoView) findViewById(R.id.vvClip);
		btClose = (Button) findViewById(R.id.btClose);
		btNewVideo = (Button) findViewById(R.id.btNewVideo);
		btListVideos = (Button) findViewById(R.id.btListVideos);
		
		btClose.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		btNewVideo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
			    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
			    shootVideo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

			    shootVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

			    // start the Video Capture Intent
			    startActivityForResult(shootVideo, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);										
			}
		});
		
		btListVideos.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent listVideo = new Intent(getApplication(), VideosList.class);
				startActivity(listVideo);
				
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data); //this was removed on the api docs
		bTakeVideo = false;
	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Log.d("videoResult", "funcam -- video successfully save to " + fileUri.toString());
	            Toast.makeText(this, "Video successfully saved", Toast.LENGTH_LONG).show();
	            vvClip.setVideoURI(fileUri);
	            vvClip.start();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	            //Log.d("videoResult", "funcam -- video save cancelled");
	        	Toast.makeText(this, "Video discarded", Toast.LENGTH_SHORT).show();
	        } else {
	            // Video capture failed, advise user
	            //Log.d("videoResult", "funcam -- video failed");
	        	Toast.makeText(this, "Video capture failed", Toast.LENGTH_SHORT).show();
	        }
	    }				
	}
	

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
	
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "funcam");
	
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("funcam", "funcam -- failed to create directory");
	            return null;
	        }
	    }
	
	    // Create a media file name
	    Long tsLong = System.currentTimeMillis()/1000;
	    String timeStamp = tsLong.toString();	    
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	
	    return mediaFile;
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_extras_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_video_upload:
			Intent uploadVideo = new Intent(getApplication(), VideoUpload.class);
			startActivity(uploadVideo);
			finish();
			return true;
		case R.id.action_take_photo:
			Intent openCamera = new Intent(getApplication(), Camera.class);
			startActivity(openCamera);
			finish();
			return true;
		case R.id.action_app_settings:
			Intent openSettings = new Intent(getApplication(), AppSettings.class);
			startActivity(openSettings);
			finish();
			return true;		
		case R.id.action_app_help:
			Intent openHelp = new Intent(getApplication(), AppHelp.class);
			startActivity(openHelp);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	    
	
	
}