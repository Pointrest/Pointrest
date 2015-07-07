package com.pointrestapp.pointrest.activities;

import java.lang.ref.WeakReference;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.LocalNotification;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.fragments.DetailFragment;
import com.pointrestapp.pointrest.fragments.FiltriRicercaFragment;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;
import com.pointrestapp.pointrest.fragments.InfoAppFragment;
import com.pointrestapp.pointrest.fragments.NewMainFragment;
import com.pointrestapp.pointrest.fragments.NotificheFragment;
import com.pointrestapp.pointrest.fragments.PreferitiFragment;
import com.pointrestapp.pointrest.sync.PuntiDownloader;

public class MainActivity extends AppCompatActivity implements
		TabAdapter.TabSelectedListener, FragmentListFrame.Callback,
		OnNavigationItemSelectedListener {

	DrawerLayout drawerLayout;
	ActionBarDrawerToggle drawerToggle;

	private static final long SYNC_INTERVAL_IN_SECONDS = 60 * 60 * 6;
	private CharSequence mTitle;
	private OnSharedPreferenceChangeListener mSharedPreferencesListener;

	private WeakReference<NewMainFragment> mNewFragment;

	private WeakReference<Fragment> lastAddedFragment;

	private ContentObserver mObserver;
	private boolean mInitialized = false;
	private boolean mRanForTheFirstTime = false;
	private BroadcastReceiver errorReciever;
	private BroadcastReceiver newDataReciever;
	private LinearLayout errorLayout;
	private LinearLayout loadingLayout;
	private Bundle lastBundle;

	@Override
	protected void onResume() {
		super.onResume();
		if (!mRanForTheFirstTime) {
			IntentFilter errorStatusFilter = new IntentFilter(
					Constants.ERROR_STATUS);
			errorReciever = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					handleDownloadingError();
				}
			};
			registerReceiver(errorReciever, errorStatusFilter);
		}
		
		IntentFilter newDataFilter = new IntentFilter(Constants.NEW_DATA_STATUS);
		newDataReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				handleNewData();
			}
		};
		registerReceiver(newDataReciever, newDataFilter);
	};

	protected void handleNewData() {
		if (!mInitialized)
			initializeScreen(lastBundle);
		else if (mNewFragment != null)
			mNewFragment.get().updateMarkers();
	}

	protected void handleDownloadingError() {
		errorLayout.setVisibility(View.VISIBLE);
		loadingLayout.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		if (errorReciever != null)
			unregisterReceiver(errorReciever);
		unregisterReceiver(newDataReciever);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createSyncAccountAndInitializeSyncAdapter(this);
		setContentView(R.layout.activity_base);

		initDrawer();

		setUpGui(savedInstanceState);

		if (savedInstanceState != null)
			lastBundle = savedInstanceState;
	}

	private void initDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.string.filtri_title, R.string.preferiti_title);
		drawerLayout.setDrawerListener(drawerToggle);
		NavigationView nv = (NavigationView) findViewById(R.id.navigation);
		nv.setNavigationItemSelectedListener(this);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpGui(Bundle savedInstanceState) {
		errorLayout = (LinearLayout) findViewById(R.id.error_layout);
		loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);

		findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				errorLayout.setVisibility(View.GONE);
				loadingLayout.setVisibility(View.VISIBLE);
				MainActivity.this.runExpeditedUpdate(getApplicationContext());
			}
		});

		SharedPreferences vPintrestPreferences = this.getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);

		mRanForTheFirstTime = vPintrestPreferences.getBoolean(
				Constants.RAN_FOR_THE_FIRST_TIME, false);
		if (mRanForTheFirstTime)
			initializeScreen(savedInstanceState);
		else
			loadingLayout.setVisibility(View.VISIBLE);
	}

	protected void initializeScreen(Bundle savedInstanceState) {
		loadingLayout.setVisibility(View.GONE);
		errorLayout.setVisibility(View.GONE);
		if (savedInstanceState == null) {
			NewMainFragment f = NewMainFragment.getInstance();
			mNewFragment = new WeakReference<NewMainFragment>(f);
			lastAddedFragment = new WeakReference<Fragment>(f);
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.container, mNewFragment.get(), R.id.navItem0 + "")
					.commit();

		} else {
			mNewFragment = new WeakReference<NewMainFragment>(
					(NewMainFragment) getSupportFragmentManager()
							.findFragmentByTag(R.id.navItem0 + ""));
		}
		mInitialized = true;
	}

	@Override
	public void onTabSelected(int puntoType) {
		// Ogni tanto è null, come fa ad essere chiamato questo prima
		// dell'oncreate dell'activity!?
		// Succede. Quindi brutto hack sotto.
		if (mNewFragment == null) {
			mNewFragment = new WeakReference<NewMainFragment>(
					(NewMainFragment) getSupportFragmentManager()
							.findFragmentByTag(R.id.navItem0 + ""));
		}
		mNewFragment.get().onTabSelected(puntoType);

	}

	@Override
	public void goToDetailScreen(int pointId) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, DetailFragment.getInstance(pointId))
				.addToBackStack(null).commit();
	}

	@Override
	public void goToMapScreen(float x, float y) {
		// mMapFragment.prepareForShow(x, y);
		getSupportFragmentManager().beginTransaction().hide(mNewFragment.get())
				.addToBackStack(null).commit();
	}

	/**
	 * The two Fragments need to know when back is pressed to undo the
	 * transformations
	 */
	@Override
	public void onBackPressed() {
		if (mNewFragment != null)
			mNewFragment.get().onBackPressed();
		super.onBackPressed();
	}

	@Override
	public void goToMapScreen(MotionEvent event) {
		// mNewFragment.prepareForShow(event);

	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item))
			return true;

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void restoreActionBar(CharSequence title) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	private void createSyncAccountAndInitializeSyncAdapter(Context context) {

		final Account newAccount = new Account(Constants.ACCOUNT, context
				.getResources().getString(R.string.pointrest_account_type));

		AccountManager accountManager = (AccountManager) context
				.getSystemService(Context.ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		accountManager.addAccountExplicitly(newAccount, null, null);

		SharedPreferences vPintrestPreferences = context.getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);

		mSharedPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				if (key.equals(Constants.SharedPreferences.RAGGIO))
					runExpeditedUpdate(getApplicationContext());
			}
		};

		vPintrestPreferences
				.registerOnSharedPreferenceChangeListener(mSharedPreferencesListener);

		if (!vPintrestPreferences.getBoolean(Constants.RAN_FOR_THE_FIRST_TIME,
				false)) {
			runExpeditedUpdate(getApplicationContext());
		}

		ContentResolver.addPeriodicSync(newAccount,
				PuntiContentProvider.AUTHORITY, Bundle.EMPTY,
				SYNC_INTERVAL_IN_SECONDS);
	}

	public void runExpeditedUpdate(Context context) {
		new PuntiDownloader(context).download();
	}

	public void launchLocalNotification(int id) {
		new LocalNotification(this, id).execute();
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem arg0) {
		mTitle = arg0.getTitle();
		boolean fragmentAlreadyAdded = false;
		Fragment which = null;
		switch (arg0.getItemId()) {
		case R.id.navItem0:
			SharedPreferences mSettings = getSharedPreferences(
					Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean(Constants.SharedPreferences.SEARCH_ENABLED, false);
			editor.commit();
			which = (NewMainFragment) getSupportFragmentManager()
					.findFragmentByTag(R.id.navItem0 + "");
			if (which == null)
				which = NewMainFragment.getInstance();
			else {
				fragmentAlreadyAdded = true;
				mNewFragment = new WeakReference<NewMainFragment>(
						(NewMainFragment) which);
			}
			break;
		case R.id.navItem1:
			which = (FiltriRicercaFragment) getSupportFragmentManager()
					.findFragmentByTag(R.id.navItem1 + "");
			if (which == null)
				which = FiltriRicercaFragment.getInstance();
			else
				fragmentAlreadyAdded = true;
			break;
		case R.id.navItem2:
			which = (PreferitiFragment) getSupportFragmentManager()
					.findFragmentByTag(R.id.navItem2 + "");
			if (which == null)
				which = PreferitiFragment.getInstance();
			else
				fragmentAlreadyAdded = true;
			break;
		case R.id.navItem3:
			which = (NotificheFragment) getSupportFragmentManager()
					.findFragmentByTag(R.id.navItem3 + "");
			if (which == null)
				which = NotificheFragment.getInstance();
			else
				fragmentAlreadyAdded = true;
			break;
		case R.id.navItem4:
			which = (InfoAppFragment) getSupportFragmentManager()
					.findFragmentByTag(R.id.navItem4 + "");
			if (which == null)
				which = InfoAppFragment.getInstance();
			else
				fragmentAlreadyAdded = true;
			break;
		}

		if (lastAddedFragment != null && which == lastAddedFragment.get()) {
			drawerLayout.closeDrawer(GravityCompat.START);
			return true;
		} else {
			lastAddedFragment = new WeakReference<Fragment>(which);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, which, arg0.getItemId() + "")
					.addToBackStack(null).commitAllowingStateLoss();
		}

		drawerLayout.closeDrawer(GravityCompat.START);
		restoreActionBar(mTitle);
		return true;
		/*
		 * mTitle = arg0.getTitle(); Fragment which = null; switch
		 * (arg0.getItemId()) { case R.id.navItem0: which =
		 * NewMainFragment.getInstance(); mNewFragment = new
		 * WeakReference<NewMainFragment>( (NewMainFragment) which); break; case
		 * R.id.navItem1: which = FiltriRicercaFragment.getInstance(); break;
		 * case R.id.navItem2: which = PreferitiFragment.getInstance(); break;
		 * case R.id.navItem3: which = NotificheFragment.getInstance(); break;
		 * case R.id.navItem4: which = InfoAppFragment.getInstance(); break; }
		 * 
		 * getSupportFragmentManager().beginTransaction()
		 * .replace(R.id.container, which, arg0.getItemId() + "")
		 * .addToBackStack(null).commitAllowingStateLoss();
		 * 
		 * drawerLayout.closeDrawer(GravityCompat.START);
		 * restoreActionBar(mTitle); return true;
		 */
	}

}
