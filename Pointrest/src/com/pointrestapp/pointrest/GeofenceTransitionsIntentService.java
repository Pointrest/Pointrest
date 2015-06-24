package com.pointrestapp.pointrest;

import java.util.List;

import android.accounts.Account;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.pointrestapp.pointrest.data.PuntiContentProvider;

public class GeofenceTransitionsIntentService extends IntentService {

	Context c;
	public GeofenceTransitionsIntentService() {
		super("");
		// TODO Auto-generated constructor stub
	}

	protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "errorrrrrre";
            Log.e("errrr", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        for (Geofence fence : triggeringGeofences) {
        	String fenceId = fence.getRequestId();
        	if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT && 
        			fenceId.equals(Constants.TRIGGER_RADIUS_FENCE_ID)) {
        		Account newAccount = new Account(
                        Constants.ACCOUNT, getApplicationContext().getResources().getString(R.string.pointrest_account_type));
        		ContentResolver.requestSync(newAccount, PuntiContentProvider.AUTHORITY, null);
        	} else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && 
        			!fenceId.equals(Constants.TRIGGER_RADIUS_FENCE_ID))
            	new LocalNotification(getApplicationContext(),
            			Integer.parseInt(fenceId)).execute();
		}
    }

}