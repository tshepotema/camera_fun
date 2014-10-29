package com.example.funcam;

import com.google.android.youtube.player.YouTubeIntents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class VideoUpload extends Activity {

	// This is the value of Intent.EXTRA_LOCAL_ONLY for API level 11 and above.
	private static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
	private static final int SELECT_VIDEO_REQUEST = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.videoupload_layout);

		Intent intent = new Intent(Intent.ACTION_PICK, null).setType("video/*");
		intent.putExtra(EXTRA_LOCAL_ONLY, true);
		startActivityForResult(intent, SELECT_VIDEO_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
	        case SELECT_VIDEO_REQUEST:
	            Intent intent = YouTubeIntents.createUploadIntent(this, returnedIntent.getData());
	            startActivity(intent);
	            finish();	
	            break;
			}
		}
		super.onActivityResult(requestCode, resultCode, returnedIntent);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_app_main:
			finish();
			return true;
		case R.id.action_video:
			Intent takeVideo = new Intent(getApplication(), VideoTake.class);
			startActivity(takeVideo);
			return true;
		/*case R.id.action_video_upload:
			Intent uploadVideo = new Intent(getApplication(), VideoUpload.class);
			startActivity(uploadVideo);
			return true;*/
			case R.id.action_videos_list:
			Intent listVideo = new Intent(getApplication(), VideosList.class);
			startActivity(listVideo);
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