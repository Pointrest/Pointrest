package com.pointrestapp.pointrest.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.pointrest.dialog.GeoFences;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.GeofenceTransitionsIntentService;
import com.pointrestapp.pointrest.LocalNotification;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.fragments.NavigationDrawerFragment;
import com.pointrestapp.pointrest.sync.PuntiSyncAdapter;

public class BaseActivity extends Activity implements
			PuntiSyncAdapter.OnDataReadyListener, 
			NavigationDrawerFragment.NavigationDrawerCallbacks,
			ConnectionCallbacks,
			OnConnectionFailedListener,
			ResultCallback<Status> {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;
	private boolean mConnectedToPlayServices = false;

	private static final String ACCOUNT = "pointrestaccount";
	private static final long SYNC_INTERVAL_IN_SECONDS = 360;

    private GeoFences mGeoFences;

	
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		buildGoogleApiClient();
		
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
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
		restoreActionBar();
	}
	
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Intent vIntent = new Intent(this, SimpleActivity.class);
		Serializable which = null;
		
	    switch(position) {
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
		vIntent.putExtra(SimpleActivity.FRAGMENT_TO_LOAD,
				which);
		startActivity(vIntent);
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
	
	@Override
	public void onDataReady() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setupUi();
			}
		});
	}
	
    protected void setupUi() {
		// TODO Auto-generated method stub
		
	}

	private void createSyncAccountAndInitializeSyncAdapter(Context context) {

        Account newAccount = new Account(
                ACCOUNT, getResources().getString(R.string.pointrest_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        
		SharedPreferences vPintrestPreferences =
				this.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		
		if (!vPintrestPreferences.getBoolean(Constants.RAN_FOR_THE_FIRST_TIME, false)) {
	        Bundle settingsBundle = new Bundle();
	        settingsBundle.putBoolean(
	                ContentResolver.SYNC_EXTRAS_MANUAL, true);
	        settingsBundle.putBoolean(
	                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	        ContentResolver.requestSync(newAccount, PuntiContentProvider.AUTHORITY, settingsBundle);
		}
        
        ContentResolver.addPeriodicSync(
                newAccount,
                PuntiContentProvider.AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL_IN_SECONDS);
    }
    
    public void launchLocalNotification(int id) {
    	new LocalNotification(this, id).execute();	    
	}
    
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
	        .addApi(LocationServices.API)
	        .build();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		System.out.println();
	}

	@Override
	public void onConnected(Bundle arg0) {
		mConnectedToPlayServices = true;
		createSyncAccountAndInitializeSyncAdapter(this);
		mGeoFences = new GeoFences(this, mGoogleApiClient);
		mGeoFences.saveGodPointToSharedPreferencesAndReturnIt();
		mGeoFences.setUpGeofences();
	}


	@Override
	public void onConnectionSuspended(int arg0) {
		System.out.println();
	}
	
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

	@Override
	public void onResult(Status arg0) {
		
	}
}
