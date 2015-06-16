package com.pointrestapp.pointrest.sync;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


public class PuntiSyncAdapter extends AbstractThreadedSyncAdapter {

	private ContentResolver mContentResolver;

	public PuntiSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContentResolver = context.getContentResolver();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		AsyncHttpClient client = new AsyncHttpClient();
		
	}
	
    public void getPoints() throws JSONException {
        PuntiRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
      
            }
        });
    }
	
}
