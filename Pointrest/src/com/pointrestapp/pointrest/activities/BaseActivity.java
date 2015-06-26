package com.pointrestapp.pointrest.activities;

import java.io.Serializable;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.LocalNotification;
import com.pointrestapp.pointrest.R;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.fragments.NavigationDrawerFragment;

public class BaseActivity extends Activity  {
//
//	private static final long SYNC_INTERVAL_IN_SECONDS = 60 * 60 * 24;
//	private NavigationDrawerFragment mNavigationDrawerFragment;
//	private CharSequence mTitle;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		createSyncAccountAndInitializeSyncAdapter(this);
//
//		
//		setContentView(R.layout.activity_main);
//
//		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
//				.findFragmentById(R.id.navigation_drawer);
//		mTitle = getTitle();
//
//		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
//				(DrawerLayout) findViewById(R.id.drawer_layout));
//	}
//	
//	public void onSectionAttached(int number) {
//		switch (number) {
//		case 1:
//			mTitle = getString(R.string.filtri_title);
//			break;
//		case 2:
//			mTitle = getString(R.string.preferiti_title);
//			break;
//		case 3:
//			mTitle = getString(R.string.notifiche_title);
//			break;
//		case 4:
//			mTitle = getString(R.string.info_app_title);
//			break;
//		}
//		restoreActionBar();
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		if (!mNavigationDrawerFragment.isDrawerOpen()) {
//			// Only show items in the action bar relevant to this screen
//			// if the drawer is not showing. Otherwise, let the drawer
//			// decide what to show in the action bar.
//			getMenuInflater().inflate(R.menu.main, menu);
//			//restoreActionBar();
//			return true;
//		}
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public void onNavigationDrawerItemSelected(int position) {
//		Intent vIntent = new Intent(this, SimpleActivity.class);
//		Serializable which = null;
//		
//	    switch(position) {
//	        case 0:
//	        	which = SimpleActivity.FragmentToLoad.FILTERS;
//	            break;
//	        case 1:
//	        	which = SimpleActivity.FragmentToLoad.FAVOURITES;
//	            break;
//	        case 2:
//	        	which = SimpleActivity.FragmentToLoad.NOTIFICATIONS;
//	            break;
//	        case 3:
//	        	which = SimpleActivity.FragmentToLoad.INFOAPP;
//	            break;
//	    }
//		vIntent.putExtra(SimpleActivity.FRAGMENT_TO_LOAD,
//				which);
//		startActivity(vIntent);
//	}
//	
//	public void restoreActionBar() {
//		ActionBar actionBar = getActionBar();
//		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setTitle(mTitle);
//	}
//
//	private void createSyncAccountAndInitializeSyncAdapter(Context context) {
//
//        Account newAccount = new Account(
//                Constants.ACCOUNT, context.getResources().getString(R.string.pointrest_account_type));
//
//        AccountManager accountManager =
//                (AccountManager) context.getSystemService(
//                        Context.ACCOUNT_SERVICE);
//        /*
//         * Add the account and account type, no password or user data
//         * If successful, return the Account object, otherwise report an error.
//         */
//        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
//            /*
//             * If you don't set android:syncable="true" in
//             * in your <provider> element in the manifest,
//             * then call context.setIsSyncable(account, AUTHORITY, 1)
//             * here.
//             */
//        } else {
//            /*
//             * The account exists or some other error occurred. Log this, report it,
//             * or handle it internally.
//             */
//        }
//        
//		SharedPreferences vPintrestPreferences =
//				context.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
//		
//		if (!vPintrestPreferences.getBoolean(Constants.RAN_FOR_THE_FIRST_TIME, false)) {
//	        Bundle settingsBundle = new Bundle();
//	        settingsBundle.putBoolean(
//	                ContentResolver.SYNC_EXTRAS_MANUAL, true);
//	        settingsBundle.putBoolean(
//	                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//	        ContentResolver.requestSync(newAccount, PuntiContentProvider.AUTHORITY, settingsBundle);
//		}
//        
//        ContentResolver.addPeriodicSync(
//                newAccount,
//                PuntiContentProvider.AUTHORITY,
//                Bundle.EMPTY,
//                SYNC_INTERVAL_IN_SECONDS);
//    }
//    
//    public void launchLocalNotification(int id) {
//    	new LocalNotification(this, id).execute();	    
//	}

}
