package com.example.funcam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class Splash extends Activity implements AnimationListener {

	ImageView ivLogo;
	Animation animSequential;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		ivLogo = (ImageView) findViewById(R.id.ivLogo);
		
		// load the animation
		animSequential = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sequential);

		// set animation listener
		animSequential.setAnimationListener(this);

		// start the animation
		ivLogo.startAnimation(animSequential);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		Intent openMain = new Intent("com.example.funcam.MAINACTIVITY");
		startActivity(openMain);		
		finish();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		onPause();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub		
	}

}
