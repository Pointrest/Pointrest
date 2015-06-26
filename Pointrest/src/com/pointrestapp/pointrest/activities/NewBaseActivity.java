package com.pointrestapp.pointrest.activities;

import java.io.Serializable;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.LocalNotification;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;

public class NewBaseActivity extends AppCompatActivity implements
		OnNavigationItemSelectedListener {

	DrawerLayout drawerLayout;
	ActionBarDrawerToggle drawerToggle;

	private static final long SYNC_INTERVAL_IN_SECONDS = 60 * 60 * 24;
	private CharSequence mTitle;
	private OnSharedPreferenceChangeListener mSharedPreferencesListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createSyncAccountAndInitializeSyncAdapter(this);
		setContentView(R.layout.activity_base);
		initInstances();
	}

	private void initInstances() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.string.filtri_title, R.string.preferiti_title);
		drawerLayout.setDrawerListener(drawerToggle);
		NavigationView nv = (NavigationView) findViewById(R.id.navigation);
		nv.setNavigationItemSelectedListener(this);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

	public void onNavigationDrawerItemSelected(int position) {
		Intent vIntent = new Intent(this, SimpleActivity.class);
		Serializable which = null;

		switch (position) {
		case -1:
			Intent i = new Intent(this, MainScreenActivity.class);
			startActivity(i);
			return;
		case 0:
			which = SimpleActivity.FragmentToLoad.FILTERS;
			break;
		case 1:
			which = SimpleActivity.FragmentToLoad.FAVOURITES;
			break;
		case 2:
			which = SimpleActivity.FragmentToLoad.NOTIFICATIONS;
			break;
		case 3:
			which = SimpleActivity.FragmentToLoad.INFOAPP;
			break;
		}
		vIntent.putExtra(SimpleActivity.FRAGMENT_TO_LOAD, which);
		startActivity(vIntent);
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
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}

		SharedPreferences vPintrestPreferences = context.getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);

		mSharedPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				runExpeditedUpdate(newAccount);
			}
		};

		vPintrestPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferencesListener);

		if (!vPintrestPreferences.getBoolean(Constants.RAN_FOR_THE_FIRST_TIME,
				false)) {
			runExpeditedUpdate(newAccount);
		}

		ContentResolver.addPeriodicSync(newAccount,
				PuntiContentProvider.AUTHORITY, Bundle.EMPTY,
				SYNC_INTERVAL_IN_SECONDS);
	}

	private void runExpeditedUpdate(Account newAccount) {
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
				true);
		ContentResolver.requestSync(newAccount,
				PuntiContentProvider.AUTHORITY, settingsBundle);
	}

	public void launchLocalNotification(int id) {
		new LocalNotification(this, id).execute();
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem arg0) {
		mTitle = arg0.getTitle();

		switch (arg0.getItemId()) {
		case R.id.navItem0:
			onNavigationDrawerItemSelected(-1);
			break;
		case R.id.navItem1:
			onNavigationDrawerItemSelected(0);
			break;
		case R.id.navItem2:
			onNavigationDrawerItemSelected(1);
			break;
		case R.id.navItem3:
			onNavigationDrawerItemSelected(2);
			break;
		case R.id.navItem4:
			onNavigationDrawerItemSelected(3);
			break;
		}
		restoreActionBar(mTitle);
		return true;
	}

}
