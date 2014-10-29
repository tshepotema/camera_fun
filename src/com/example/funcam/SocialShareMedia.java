package com.example.funcam;

import java.util.List;

import org.brickred.socialauth.Photo;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SocialShareMedia extends Activity {

	// SocialAuth Component
	SocialAuthAdapter adapter;
	Profile profileMap;
	List<Photo> photosList;

	// Android Components
	Button btUpdate;
	EditText edit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.social_share_layout);

		// Create Your Own Share Button
		Button share = (Button) findViewById(R.id.sharebutton);
		share.setText("Share");
		share.setTextColor(Color.WHITE);
		share.setBackgroundResource(R.drawable.button_gradient);

		// Add it to Library
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Add providers
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);

		// Providers that require setting user call Back url
		adapter.addCallBack(Provider.TWITTER, "http://store.zetail.co.za/android/funcam/callback.php");

		// Enable Provider
		adapter.enable(share);

	}

	/**
	 * Listens Response from Library
	 * 
	 */

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			Log.d("funcam", "funcam socialshare Authentication Successful");
			btUpdate.setEnabled(true);
			
			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("funcam", "funcam socialshare Provider Name = " + providerName);
			Toast.makeText(SocialShareMedia.this, providerName + " connected", Toast.LENGTH_LONG).show();

			btUpdate = (Button) findViewById(R.id.update);
			edit = (EditText) findViewById(R.id.editTxt);
			
			btUpdate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter.updateStatus(edit.getText().toString(), new MessageListener(), false);
				}
			});
		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d("funcam", "funcam socialshare Authentication Error: " + error.getMessage());
			btUpdate.setEnabled(false);
		}

		@Override
		public void onCancel() {
			Log.d("funcam", "funcam socialshare Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("funcam", "funcam socialshare Dialog Closed by pressing Back Key");
		}

	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(SocialShareMedia.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(SocialShareMedia.this, "Message not posted on " + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

}