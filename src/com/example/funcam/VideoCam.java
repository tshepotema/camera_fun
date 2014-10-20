package com.example.funcam;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoCam extends Activity {

	VideoView vvClip;
	
	private Uri fileUri;
	
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_camera);
		
	    //create new Intent
	    Intent shootVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

	    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
	    shootVideo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

	    shootVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

	    // start the Video Capture Intent
	    startActivityForResult(shootVideo, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data); //this was removed on the api docs
		
	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Log.d("videoResult", "funcam -- video successfully save to " + fileUri.toString());
	            Toast.makeText(this, "Video saved to:\n" +
	            		fileUri.toString(), Toast.LENGTH_LONG).show();
	            vvClip.setVideoURI(fileUri);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	            Log.d("videoResult", "funcam -- video save cancelled");
	        } else {
	            // Video capture failed, advise user
	            Log.d("videoResult", "funcam -- video failed");
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
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	
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
	
}
