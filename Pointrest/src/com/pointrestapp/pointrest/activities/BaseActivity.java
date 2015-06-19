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
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.GeofenceTransitionsIntentService;
import com.pointrestapp.pointrest.LocalNotification;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.fragments.NavigationDrawerFragment;

public class BaseActivity extends Activity implements
			NavigationDrawerFragment.NavigationDrawerCallbacks,
			ConnectionCallbacks,
			OnConnectionFailedListener,
			ResultCallback<Status> {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;
	private boolean mConnectedToPlayServices;
	private PendingIntent mGeofencePendingIntent;
	private List<Geofence> mGeofenceList = new ArrayList<Geofence>();
	private static final String ACCOUNT = "pointrestaccount";
	private static final long SYNC_INTERVAL_IN_SECONDS = 360;

	private MyLatLng godPoint; 

	
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

        ContentResolver.addPeriodicSync(
                newAccount,
                PuntiContentProvider.AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL_IN_SECONDS);
        
        // manual update for testing
        // remember to launch it on first run of the application, use
        // the "learned about notification drawer flag" to check
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(newAccount, PuntiContentProvider.AUTHORITY, settingsBundle);
    }
    
    public void launchLocalNotification(int id) {
    	new LocalNotification(this, id).execute();	    
	}
	
    public Location getCurrentUserLocation(){
    	if (!mConnectedToPlayServices)
    		return null;
    	return LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }
    
    /**
     * Here we setup the fences.
     * We'll first put up a fence around the user's current location
     * so we can use it's exit callback to update the db since
     * the user left our aggiornated zone.
     * We chose 3 km for now.
     * And since the maximum number of geofences is 99,
     * we add points them until we reach the limit and call it a day
     * In that case the scatto for db aggiornating lowers to the 
     * distance of the farthest point.
     * This is to be implemented soon.
     */
	static double distance(double fromLat, double fromLon, double toLat, double toLon) {
	    double radius = 6378137;   // approximate Earth radius, *in meters*
	    double deltaLat = toLat - fromLat;
	    double deltaLon = toLon - fromLon;
	    double angle = 2 * Math.asin( Math.sqrt(
	        Math.pow(Math.sin(deltaLat/2), 2) + 
	        Math.cos(fromLat) * Math.cos(toLat) * 
	        Math.pow(Math.sin(deltaLon/2), 2) ) );
	    return radius * angle;
	}
	
	private class MyLatLng implements Comparable<MyLatLng> {
		
		public int id;
		public double lat, lang;
		
		public MyLatLng(int id, double lat, double lang) {
			this.id = id;
			this.lat = lat;
			this.lang = lang;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			long temp;
			temp = Double.doubleToLongBits(lang);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(lat);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyLatLng other = (MyLatLng) obj;
			if (id != other.id)
				return false;
			if (Double.doubleToLongBits(lang) != Double
					.doubleToLongBits(other.lang))
				return false;
			if (Double.doubleToLongBits(lat) != Double
					.doubleToLongBits(other.lat))
				return false;
			return true;
		}

		@Override
		public int compareTo(MyLatLng another) {
			double thisDistanceFromGodPoint = distance(godPoint.lat, godPoint.lang, another.lat, another.lang);
			double otherDistanceFromGodPoint = distance(godPoint.lat, godPoint.lang, lat, lang);
			
			return (int) Math.round(thisDistanceFromGodPoint - otherDistanceFromGodPoint);
		}
		
	}
	
    public void setUpGeofences() {
    	
    	Cursor c = getContentResolver().query
    			(PuntiContentProvider.PUNTI_URI,
    					null, null, null, null);
    	
    	int langIndex = c.getColumnIndex(PuntiDbHelper.LONGITUDE);
    	int latIndex =  c.getColumnIndex(PuntiDbHelper.LATUTUDE);
    	int idIndex = c.getColumnIndex(PuntiDbHelper._ID);
    	
    	Set<MyLatLng> points = new TreeSet<MyLatLng>();

    	while (c.moveToNext()) {
    		double lat = c.getDouble(latIndex);
    		double lang = c.getDouble(langIndex);
    		int id = c.getInt(idIndex);
    		
    		points.add(new MyLatLng(id, lat, lang));    		
    	}
    	
    	int maxFences = 99;
    	for (MyLatLng myLatLng : points) {
    		if (maxFences > 0 && myLatLng.lang < 90 && myLatLng.lat < 90) {
        		mGeofenceList.add(new Geofence.Builder()
                .setRequestId(myLatLng.id + "")
                .setCircularRegion(
                		myLatLng.lat,
                		myLatLng.lang,
                        Constants.POINT_NOTIFICATION_RADIUS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
        		--maxFences;
    		}
		}
    	
	    LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    	
    }
    
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	private GeofencingRequest getGeofencingRequest() {
	    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
	    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
	    builder.addGeofences(mGeofenceList);
	    return builder.build();
	}
	
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		System.out.println();
	}

	@Override
	public void onConnected(Bundle arg0) {
		mConnectedToPlayServices = true;
		godPoint = saveGodPointToSharedPreferencesAndReturnIt();
		createSyncAccountAndInitializeSyncAdapter(this);		
		//Temporarily here; Will be moved elsewhere
		setUpGeofences();
		//
	}
	

	private MyLatLng saveGodPointToSharedPreferencesAndReturnIt() {
		
    	Location loc = getCurrentUserLocation();
    	double lat = loc.getLatitude();
    	double lang = loc.getLongitude();
    	
		SharedPreferences pointrestPreferences =
				this.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pointrestPreferences.edit();
	    editor.putFloat(Constants.SharedPreferences.LANG, (float)lang);
	    editor.putFloat(Constants.SharedPreferences.LAT, (float)lat);
	    editor.commit();
	    
	    return new MyLatLng(-1, lat, lang);
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
