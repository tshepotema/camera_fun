package com.example.funcam;

import com.example.funcam.R;
import com.example.funcam.videos.DeveloperKey;
import com.example.funcam.videos.YouTubeFailureRecoveryActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Video extends YouTubeFailureRecoveryActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.videoplayer_fragmentview);

		YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
 		youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cuePlaylist(VideosList.PLAYLIST_ID);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
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
		case R.id.action_video_upload:
			Intent uploadVideo = new Intent(getApplication(), VideoUpload.class);
			startActivity(uploadVideo);
			return true;
		case R.id.action_videos_list:
			Intent listVideo = new Intent(getApplication(), VideosList.class);
			startActivity(listVideo);
			return true;
		case R.id.action_take_photo:
			Intent openCamera = new Intent(getApplication(), Camera.class);
			startActivity(openCamera);
			return true;
		case R.id.action_app_settings:
			Intent openSettings = new Intent(getApplication(), AppSettings.class);
			startActivity(openSettings);
			return true;		
		case R.id.action_app_help:
			Intent openHelp = new Intent(getApplication(), AppHelp.class);
			startActivity(openHelp);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	    
		
		
}