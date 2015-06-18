package com.pointrestapp.pointrest.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.GeofenceTransitionsIntentService;
import android.view.View;
import com.pointrestapp.pointrest.Constants;
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
	private int cont;

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
		createSyncAccountAndInitializeSyncAdapter(this);
		
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
    	new CreateNotification(id).execute();	    
	}
    
    private class CreateNotification extends AsyncTask<Void, Void, Void> {

        private int id;

		public CreateNotification(int id) {
         this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {

        	int temp = 2;
    		
    		 Intent resultIntent = new Intent(getApplication(), MainScreenActivity.class);
    		 
    		 Cursor cursor = getContentResolver().query(PuntiContentProvider.PUNTI_URI, null, PuntiDbHelper._ID + "=?",
    						new String[]{temp + "" }, null);
    		 
    		 int nameIndex = cursor.getColumnIndex(PuntiDbHelper.NOME);
    		 int descriptionIndex = cursor.getColumnIndex(PuntiDbHelper.DESCRIZIONE);	
    		 
    		 String name = "pointerest notification";
    		 String description = "pointerest description";
    		 if(cursor.moveToNext()){
    			 name = cursor.getString(nameIndex); 
    			 description = cursor.getString(descriptionIndex);
    		 }
    		 
    		 //GET IMAGE ID
//    		 cursor = getContentResolver().query(PuntiContentProvider.PUNTI_IMAGES_URI, null, PuntiImagesDbHelper.PUNTO_ID + "=?",
//    					new String[]{temp + "" }, null);
//    		 
//    		 int idImageIndex = cursor.getColumnIndex(PuntiImagesDbHelper._ID);
//    		 
//    		 int idImage = -1;
//    		 if(cursor.moveToNext())
//    			 idImage = cursor.getInt(idImageIndex);
    		 
    		 	TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());

    	        // Adds the back stack for the Intent (but not the Intent itself).
    	        stackBuilder.addParentStack(MainScreenActivity.class);

    	        // Adds the Intent that starts the Activity to the top of the stack.
    	        stackBuilder.addNextIntent(resultIntent);
    		 
    			PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    						 
    			 if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
    				 
    				NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
    		        notiStyle.setBigContentTitle(name);
    		        notiStyle.setSummaryText("Pointerest");
    		        	
    		        String sample_url = "http://codeversed.com/androidifysteve.png";
    		        
    		        Bitmap remote_picture = null;
    		        
    				try {
    		            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
    		        } catch (IOException e) {
    		            e.printStackTrace();
    		        }

    		        // Add the big picture to the style.
    		        notiStyle.bigPicture(remote_picture);
    		        
   				 NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplication())
 				.setSmallIcon(R.drawable.ic_launcher)
 	            .setAutoCancel(true)
 	            .setLargeIcon(remote_picture)
 	            .setContentIntent(resultPendingIntent)
// 	            .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
// 	            .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
// 	            .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
 	            .setContentTitle(name)
 	            .setContentText(description)
 	            .setTicker("Pointerest");

    				 notification.setStyle(notiStyle);
    				 notification.setNumber(cont++);
    				 
    				 NotificationManager mNotificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);			
    				 mNotificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
    				 
    			 }else{
    				 NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplication());
    				 notification.setAutoCancel(true)
    			     .setDefaults(Notification.DEFAULT_ALL)
    			     .setWhen(System.currentTimeMillis())         
    			     .setSmallIcon(R.drawable.ic_launcher)
    			     .setTicker("Pointerest")            
    			     .setContentTitle(name)
    			     .setContentText(description)
    			     .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
    			     .setContentIntent(resultPendingIntent)
    			     .setAutoCancel(true);
    				 
    				 NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
    				    notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
    			 }	
           

            return null;

        }
    }
	
    public Location getCurrentUserLocation(){
    	if (!mConnectedToPlayServices)
    		return null;
    	return LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }
    
    /**
     * Here we setup the fences.
     * We'll first put a fence around the user's current location
     * so we can use it's exit callback to update the db since
     * the user left our aggiornated zone.
     * We chose 4 km for now, but the ideal solution would be to
     * keep adding fences for the various points, and when we reach 99
     * which is the max geofences we can have, we call it a day and
     * take the distance of the farthest point as the trigger radius.
     * As of now I don't know how to efficiently implement this.
     */
    public void setUpGeofences() {
    	/*
    	Cursor c = getContentResolver().query
    			(PuntiContentProvider.PUNTI_URI,
    					null, null, null, null);
    	
    	if (c.moveToNext()) {
    		
    	}
    	
		SharedPreferences pointrestPreferences =
				this.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		
		int raggio = pointrestPreferences.getInt(Constants.SharedPreferences.RAGGIO, 100);
		double lang = pointrestPreferences.getLong(Constants.SharedPreferences.LANG, 65);
		double lat = pointrestPreferences.getLong(Constants.SharedPreferences.LAT, 45);
    	*/
    	Location l = getCurrentUserLocation();
    	if (l == null)
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	mGeofenceList.add(new Geofence.Builder()
        // Set the request ID of the geofence. This is a string to identify this
        // geofence.
        .setRequestId(Constants.BASE_FENCE_ID)

        .setCircularRegion(
                l.getLatitude(),
                l.getLongitude(),
                10
        )
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                Geofence.GEOFENCE_TRANSITION_EXIT)
        .build());
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
        // Reuse the PendingIntent if we already have it.
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
		// TODO Auto-generated method stub
		System.out.println();
		mConnectedToPlayServices = true;
		setUpGeofences();
	    LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		System.out.println();
	}
	
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

	@Override
	public void onResult(Status arg0) {
		Toast.makeText(this, "geofffff", Toast.LENGTH_SHORT).show();
	}
}
