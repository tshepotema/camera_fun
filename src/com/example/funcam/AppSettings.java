package com.example.funcam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

public class AppSettings extends Activity {
	EditText etUploaderName, etDistanceKM;
	CheckBox cbWifiOn;
	SeekBar sbMarkerSize;
	Button btSave;
	Integer wifiOn;

	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String nameKey = "name_key"; 
	public static final String distanceKey = "distance_key"; 
	public static final String wifiKey = "wifi_key"; 
	public static final String markerKey = "marker_key"; 
	
	SharedPreferences sharedPref;
	Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_settings);
		
		initializeLayout();
		
		sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		etUploaderName.setText(sharedPref.getString(nameKey, "funcam_GuestUser"));
		etDistanceKM.setText(sharedPref.getString(distanceKey, "50"));
		sbMarkerSize.setProgress(sharedPref.getInt(markerKey, 50));		
		wifiOn = sharedPref.getInt(wifiKey, 1);
	      	      
		switch (wifiOn) {
			case 1:
				cbWifiOn.setChecked(true);
				break;
			case 2:
				cbWifiOn.setChecked(false);
				break;
		}		
		
		btSave.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// update the shared preferences
				String uploaderName = etUploaderName.getText().toString();
				String distanceKM = etDistanceKM.getText().toString();
				Integer markerSize = sbMarkerSize.getProgress() + 25;
				
				editor.putString(nameKey, uploaderName);
				editor.putString(distanceKey, distanceKM);				
				editor.putInt(markerKey, markerSize);
				if (cbWifiOn.isChecked()) {
					editor.putInt(wifiKey, 1);
				} else {
					editor.putInt(wifiKey, 2);					
				}
				
				//save changes in SharedPreferences
				editor.commit();
				
				onPause();
			}
		});								
	}
	
	private void initializeLayout() {
		etUploaderName = (EditText) findViewById(R.id.etUploaderName);
		etDistanceKM = (EditText) findViewById(R.id.etDefaultMapDistance);
		cbWifiOn = (CheckBox) findViewById(R.id.cbUploadWifi);
		sbMarkerSize = (SeekBar) findViewById(R.id.sbMarkerSize);
		btSave = (Button) findViewById(R.id.btSave);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_video:
			Intent videoMain = new Intent(getApplication(), Video.class);
			startActivity(videoMain);
			return true;
		case R.id.action_app_main:
			finish();
			return true;
		case R.id.action_app_help:
			Intent openHelp = new Intent(getApplication(), AppHelp.class);
			startActivity(openHelp);
			return true;
		case R.id.action_share:
			Intent shareMain = new Intent(getApplication(), SocialShare.class);
			startActivity(shareMain);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}