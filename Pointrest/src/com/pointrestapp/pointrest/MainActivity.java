package com.pointrestapp.pointrest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pointrestapp.pointrest.adapters.TabAdapter;
public class MainActivity extends Activity implements
		TabAdapter.Callback,
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		FragmentListFrame.Callback{

	private static final String TAG_MAP_SCREEN = "TAG_MAP_SCREEN";

	private static final String TAG_TITLE_SCREEN = "TAG_TITLE_SCREEN";

	private static final String TAG_NOTIFICHE = "TAG_NOTIFICHE";

	private static final String TAG_INFO_APP = "TAG_INFO_APP";

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
		/*mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));*/
		if (savedInstanceState == null) {
			mTitleScreenFragment = FragmentTitleScreen.getInstance();
			mMapFragment = FragmentMap.getInstance(-1);
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
	}

	@Override
	public void goToDetailScreen(int pointId) {
		
	}
	
	
	public void goToNotifiche() {
		// TODO Auto-generated method stub
		getFragmentManager().beginTransaction()
		.remove(mMapFragment)
		.remove(mTitleScreenFragment)
		.addToBackStack(null)
		.add(R.id.container, NotificheFragment.getInstance(), TAG_NOTIFICHE).commit();
	}
	
	public void goToInfoApp() {
		// TODO Auto-generated method stub
		getFragmentManager().beginTransaction()
		.replace(R.id.container, InfoAppFragment.getInstance(), TAG_INFO_APP).commit();
	}

}
