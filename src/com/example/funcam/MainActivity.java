package com.example.funcam;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.funcam.MapSectionFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the primary sections of the app, one at a time.
     */
    ViewPager mViewPager;
    
    static Context mainContext;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = getApplicationContext();
        
        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        
        private static String[] tabTitles = {"Web Photos", "My Photos", "Photo Map"};

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                	//list photos uploaded to web
                    return new WebPhotosFragment();
                case 1:
                	//list photos uploaded to web
                    return new MyPhotosFragment();
                case 2:
                	//show the map view on this segment
                    return new MapSectionFragment();
                default:
                	return new MyPhotosFragment();
        	}
        }

        @Override
        public int getCount() {
        	return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_take_photo:
			Intent openCamera = new Intent(getApplication(), Camera.class);
			startActivity(openCamera);
			finish();
			return true;
		case R.id.action_app_settings:
			Intent openSettings = new Intent(getApplication(), AppSettings.class);
			startActivity(openSettings);
			return true;		
		case R.id.action_app_help:
			Intent openHelp = new Intent(getApplication(), AppHelp.class);
			startActivity(openHelp);
			return true;
		case R.id.action_sync:
			Intent syncPhotos = new Intent(getApplication(), UploadService.class);
			startActivity(syncPhotos);
			finish();
			return true;
		case R.id.action_video:
			Intent videoMain = new Intent("com.example.funcam.VIDEO");
			startActivity(videoMain);
			return true;
		case R.id.action_share:
			Intent shareMain = new Intent("com.example.funcam.SOCIALSHARE");
			startActivity(shareMain);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	    
    
}