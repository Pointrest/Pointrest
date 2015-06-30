package com.pointrestapp.pointrest.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.GeofenceTransitionsIntentService;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;

public class GeofencesHandler {
    
	private MyLatLng godPoint; 
	private PendingIntent mGeofencePendingIntent;
	private List<Geofence> mGeofenceList = new ArrayList<Geofence>();
	private Context mContext;
	private GoogleApiClient mGoogleApiClient;
	
	public GeofencesHandler (Context aContext, GoogleApiClient gac) {
		mContext = aContext;
		mGoogleApiClient = gac;
		godPoint = saveGodPointToSharedPreferencesAndReturnIt();
	}

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
	
	public class MyLatLng implements Comparable<MyLatLng> {
		
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

    public Location getCurrentUserLocation(){
    	return LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

	public MyLatLng saveGodPointToSharedPreferencesAndReturnIt() {
		
    	Location loc = getCurrentUserLocation();
    	
    	double lat = loc.getLatitude();
    	double lang = loc.getLongitude();
    	
		SharedPreferences pointrestPreferences =
				mContext.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pointrestPreferences.edit();
	    editor.putFloat(Constants.SharedPreferences.LANG, (float)lang);
	    editor.putFloat(Constants.SharedPreferences.LAT, (float)lat);
	    editor.commit();
	    
	    return new MyLatLng(-1, lat, lang);
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
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
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
    public void putUpGeofences() {
    	
    	mGeofenceList.add(putUpUpdateTriggerFenceAndReturnIt());
    	
    	Cursor c = mContext.getContentResolver().query
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
    	
    	c.close();
    	
    	emptyGeofences();
    	
    	int maxFences = 99;
    	for (MyLatLng myLatLng : points) {
    		if (maxFences > 0) {
        		mGeofenceList.add(new Geofence.Builder()
                .setRequestId(myLatLng.id + "")
                .setCircularRegion(
                		myLatLng.lat,
                		myLatLng.lang,
                        Constants.POINT_NOTIFICATION_RADIUS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());
        		--maxFences;
    		}
		}
    	
    	if (mGoogleApiClient.isConnected())
		    LocationServices.GeofencingApi.addGeofences(
	                mGoogleApiClient,
	                getGeofencingRequest(),
	                getGeofencePendingIntent()
	        ).setResultCallback((ResultCallback<Status>) mContext);
    	
    }

	private void emptyGeofences() {
    	if (mGoogleApiClient.isConnected()) {
		    LocationServices.GeofencingApi
		    	.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());
    	}
	}

	private Geofence putUpUpdateTriggerFenceAndReturnIt() {
		return new Geofence.Builder()
        .setRequestId(Constants.TRIGGER_RADIUS_FENCE_ID)
        .setCircularRegion(
        		godPoint.lat,
        		godPoint.lang,
                Constants.POINT_NOTIFICATION_RADIUS
        )
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
        .build();
	}
}
