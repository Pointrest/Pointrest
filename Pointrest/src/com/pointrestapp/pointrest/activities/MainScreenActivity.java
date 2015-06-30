package com.pointrestapp.pointrest.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.widget.Toast;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.adapters.TabAdapter;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.fragments.FragmentListFrame;
import com.pointrestapp.pointrest.fragments.FragmentMap;
import com.pointrestapp.pointrest.fragments.FragmentTitleScreen;

public class MainScreenActivity extends NewBaseActivity implements
		TabAdapter.TabSelectedListener,
		FragmentListFrame.Callback {

	private static final String TAG_MAP_SCREEN = "TAG_MAP_SCREEN";
	private static final String TAG_TITLE_SCREEN = "TAG_TITLE_SCREEN";

	private FragmentTitleScreen mTitleScreenFragment;	
	private FragmentMap mMapFragment;
	
    private ContentObserver mObserver;
    private boolean mInitialized = false;
    private boolean mRanForTheFirstTime = false;
    BroadcastReceiver errorReciever;
	
    @Override
    protected void onResume() {
    	super.onResume();
    	if (!mRanForTheFirstTime) {
	        IntentFilter errorStatusFilter = new IntentFilter(Constants.ERROR_STATUS);
	        errorReciever = new BroadcastReceiver() {
	    		
	    		@Override
	    		public void onReceive(Context context, Intent intent) {
	    			handleDownloadingError();
	    		}
	    	};
	        registerReceiver(errorReciever, errorStatusFilter);
    	}
    };
    
	protected void handleDownloadingError() {
		Toast.makeText(this, "errorescaricamento", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (errorReciever != null)
			unregisterReceiver(errorReciever);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences vPintrestPreferences =
				this.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		
		mRanForTheFirstTime = vPintrestPreferences.getBoolean(Constants.RAN_FOR_THE_FIRST_TIME, false);
		if (mRanForTheFirstTime)
			initializeScreen(savedInstanceState);
		
		Bundle b = null;
		if (savedInstanceState != null)
			b = (Bundle) savedInstanceState.clone();
		final Bundle bFinal = b;
		
		//The first time the app is launched, we must wait until the Categories are populated
		//before we initialize the viewPager. We know PUNTI get populated for last
		//so we listen to those changes
		//Maybe we should add a dummy uri to avvise us?
		mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
			@Override
			public void onChange(boolean selfChange, Uri uri) {
				super.onChange(selfChange, uri);
				if (!mInitialized)
					initializeScreen(bFinal);
				else if (mMapFragment != null)
					mMapFragment.updateMarkers();
			}
		};
        getContentResolver().registerContentObserver(PuntiContentProvider.DUMMY_NOTIFIER_URI, false, mObserver);
        getContentResolver().registerContentObserver(PuntiContentProvider.PUNTI_URI, false, mObserver);
        getContentResolver().registerContentObserver(PuntiContentProvider.SOTTOCATEGORIE_URI, false, mObserver);
	}

	protected void initializeScreen(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			mTitleScreenFragment = FragmentTitleScreen.getInstance();
			mMapFragment = FragmentMap.getInstance(0);
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, mMapFragment, TAG_MAP_SCREEN)
				.add(R.id.container, mTitleScreenFragment, TAG_TITLE_SCREEN)
				.commit();
			
		} else {
			mTitleScreenFragment = (FragmentTitleScreen) getSupportFragmentManager().findFragmentByTag(TAG_TITLE_SCREEN);
			mMapFragment = (FragmentMap) getSupportFragmentManager().findFragmentByTag(TAG_MAP_SCREEN);
		}
		mInitialized = true;
	}

	@Override
	public void onTabSelected(int puntoType) {
		//Ogni tanto è null, come fa ad essere chiamato questo prima dell'oncreate dell'activity!?
		if (mMapFragment != null && mTitleScreenFragment != null) {
			mMapFragment.onTabSelected(puntoType);
			mTitleScreenFragment.onTabSelected(puntoType);
		}
	}

	@Override
	public void goToDetailScreen(int pointId) {
		
	}
	
	@Override
	public void goToMapScreen(float x, float y) {
		//mMapFragment.prepareForShow(x, y);
		getSupportFragmentManager().beginTransaction()
		.hide(mTitleScreenFragment)
		.addToBackStack(null)
		.commit();
	}
	
	/**
	 * The two Fragments need to know when back is pressed to undo
	 * the transformations 
	 */
	@Override
	public void onBackPressed() {
		if (mMapFragment != null)
			mMapFragment.onBackPressed();
		if (mTitleScreenFragment != null)
			mTitleScreenFragment.OnBackPressed();
		super.onBackPressed();
	}

	@Override
	public void goToMapScreen(MotionEvent event) {
		mMapFragment.prepareForShow(event);
		getSupportFragmentManager().beginTransaction()
		.hide(mTitleScreenFragment)
		.addToBackStack(null)
		.commit();
	}
	
}
