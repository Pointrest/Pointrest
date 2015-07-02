package com.pointrestapp.pointrest.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.pointrestapp.pointrest.Constants;

public class PuntiSyncAdapterRetrofit extends AbstractThreadedSyncAdapter
		implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final String POINTREST_DEBUG = "POINTREST";
	private Context mContext;
	private GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;
	private int raggio;
	private float lang;
	private float lat;
	private SharedPreferences pointrestPreferences;
	private GeofencesHandler mGeofencesHandler;

	public PuntiSyncAdapterRetrofit(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		buildGoogleApiClient();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		// We're going to sync in the onConnected callback
		if (!mResolvingError) { // more about this later
			mGoogleApiClient.connect();
		}

	}

	private synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(mContext, this, this)
				.addApi(LocationServices.API).build();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {

		mGeofencesHandler = new GeofencesHandler(mContext, mGoogleApiClient);

		// Previously saved user prefs
		pointrestPreferences = mContext.getSharedPreferences(
				Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);

		raggio = pointrestPreferences.getInt(
				Constants.SharedPreferences.RAGGIO, 10);
		lang = pointrestPreferences.getFloat(Constants.SharedPreferences.LANG,
				0);
		lat = pointrestPreferences.getFloat(Constants.SharedPreferences.LAT, 0);

		Log.d(POINTREST_DEBUG, "Starting download, lat is " + lat + " lang is "
				+ lang + " raggio is " + raggio);
		new PuntiDownloader(mContext, mGoogleApiClient, mGeofencesHandler, lat, lang, raggio).download();

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

}
