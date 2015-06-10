package com.pointrestapp.pointrest;

import java.util.ArrayList;
import java.util.WeakHashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.fragments.FiltriRicercaFragment;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;
import com.pointrestapp.pointrest.fragments.FragmentMap;
import com.pointrestapp.pointrest.fragments.FragmentTitleScreen;
import com.pointrestapp.pointrest.fragments.InfoAppFragment;
import com.pointrestapp.pointrest.fragments.NavigationDrawerFragment;
import com.pointrestapp.pointrest.fragments.NotificheFragment;
import com.pointrestapp.pointrest.fragments.PreferitiFragment;
public class MainActivity extends Activity implements
		TabAdapter.MapCallback,
		TabAdapter.ListCallback,
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		FragmentListFrame.Callback{

	private static final String TAG_MAP_SCREEN = "TAG_MAP_SCREEN";
	private static final String TAG_TITLE_SCREEN = "TAG_TITLE_SCREEN";
	private static final String TAG_INFO_APP = "TAG_INFO_APP";
	private static final String TAG_FRAGMENT_NOTIFICHE = "TAG_NOTIFICHE";
	private static final String TAG_PREFERITI = "TAG_PREFERITI";
	private static final String TAG_FILTRI_RICERCA = "TAG_FILTRI_RICERCA";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private FragmentTitleScreen mTitleScreenFragment;
	
	private FragmentMap mMapFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		if (savedInstanceState == null) {
			mTitleScreenFragment = FragmentTitleScreen.getInstance();
			mMapFragment = FragmentMap.getInstance(0);
			getFragmentManager().beginTransaction()
				.add(R.id.container, mMapFragment, TAG_MAP_SCREEN)
				.add(R.id.container, mTitleScreenFragment, TAG_TITLE_SCREEN)
				.commit();
			
		} else {
			mTitleScreenFragment = (FragmentTitleScreen) getFragmentManager().findFragmentByTag(TAG_TITLE_SCREEN);
			mMapFragment = (FragmentMap) getFragmentManager().findFragmentByTag(TAG_MAP_SCREEN);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Fragment fragment;
		String tag = "";
	    switch(position) {
	        default:
	        case 0:
	        	tag = TAG_FILTRI_RICERCA;
	            fragment = new FiltriRicercaFragment();
	            break;
	        case 1:
	        	tag = TAG_PREFERITI;
	            fragment = new PreferitiFragment();
	            break;
	        case 2:
	        	tag = TAG_FRAGMENT_NOTIFICHE;
	            fragment = new NotificheFragment();
	            break;
	        case 3:
	        	tag = TAG_INFO_APP; 
	            fragment = new InfoAppFragment();
	            break;
	    }
	    
	    getFragmentManager().beginTransaction()
	    .replace(R.id.container, fragment)
	    .addToBackStack(null)
	    .commit();
		// update the main content by replacing fragments
		/* FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit(); */
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}
/*
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	} */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			//restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(int puntoType) {
		mMapFragment.onTabSelected(puntoType);
		mTitleScreenFragment.onTabSelected(puntoType);
	}

	@Override
	public void goToDetailScreen(int pointId) {
		
	}
	
	
	public void goToNotifiche() {
		getFragmentManager().beginTransaction()
		.replace(R.id.container, NotificheFragment.getInstance())
		.addToBackStack(null)
		.commit();	}
	
	public void goToInfoApp() {
		// TODO Auto-generated method stub
		getFragmentManager().beginTransaction()
		.replace(R.id.container, InfoAppFragment.getInstance(), TAG_INFO_APP)
		.addToBackStack(null)
		.commit();
	}
	
	@Override
	public void goToMapScreen(float x, float y) {
		mMapFragment.prepareForShow(x, y);

		getFragmentManager().beginTransaction()
		//.add(R.id.container, mTitleScreenFragment, TAG_MAP_SCREEN)
		.hide(mTitleScreenFragment)
		.addToBackStack(null)
		.commit();
	}
	
	@Override
	public void onBackPressed() {
		
		mMapFragment.onBackPressed();
		mTitleScreenFragment.OnBackPressed();
		super.onBackPressed();
	}

	@Override
	public Fragment getFragmentForTab(int puntoType) {
		return mTitleScreenFragment.getFragmentForTab(puntoType);
	}
	
}
