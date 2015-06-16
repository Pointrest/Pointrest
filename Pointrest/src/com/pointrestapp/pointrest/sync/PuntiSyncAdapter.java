package com.pointrestapp.pointrest.sync;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;


public class PuntiSyncAdapter extends AbstractThreadedSyncAdapter {

	private ContentResolver mContentResolver;
	private Context mContext;

	public PuntiSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		//Previously saved user prefs
		SharedPreferences pointrestPreferences =
				mContext.getSharedPreferences(Constants.POINTREST_PREFERENCES, Context.MODE_PRIVATE);
		
		int raggio = pointrestPreferences.getInt(Constants.SharedPreferences.RAGGIO, 100);
		double lang = pointrestPreferences.getLong(Constants.SharedPreferences.LANG, 65);
		double lat = pointrestPreferences.getLong(Constants.SharedPreferences.LAT, 45);
		
		//Try and get the points
		try {
			getPoints(lang, lat, raggio);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    public void getPoints(double lang, double lat, int raggio) throws JSONException {

    	String url = "pi/filter/" + lat + "/" + lang + "/" + raggio;
    	
        PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            	System.out.println();
            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray points) {
            	System.out.println();
            	parseJSONArray(points);
            }
        });
    }

	private void parseJSONArray(JSONArray points) {
		
		//Names of JSON props to extracts
		final String ID = "ID";
		final String NOME = "Nome";
		final String CATEGORIA_ID = "CategoriaID";
		final String CATEGORIA = "Categoria";
		final String SOTTOCATEGORIA_ID = "SottocategoriaID";
		final String SOTTOCATEGORIA = "Sottocategoria";
		final String DESCRIZIONE = "Descrizione";
		final String LATITUDINE = "Latitudine";
		final String LONGITUDINE = "Longitudine";
		final String IMAGES_ID = "ImagesID";
		
		try {
			
			Vector<ContentValues> pointsToAddVector = new Vector<ContentValues>(points.length());
			Vector<ContentValues> pointsToUpdateVector = new Vector<ContentValues>(points.length());
			
			Cursor cursor = mContext.getContentResolver().query(PuntiContentProvider.PUNTI_URI, new String[]{PuntiDbHelper.SERVER_ID}, null, null, null);
			int serverIdIndex = cursor.getColumnIndex(PuntiDbHelper.SERVER_ID);
			Set<Integer> currentPoints = new TreeSet<Integer>();
			
			while (cursor.moveToNext()) {
				currentPoints.add(cursor.getInt(serverIdIndex));
			}
			
			for (int i = 0; i < points.length(); ++i) {
				
				int serverId;
				String name;
				int categoriaId;
				String categoria;
				int sottocategoriaId;
				String sottocategoria;
				String descrizione;
				double lat;
				double lang;
				int[] images;
				
				JSONObject point = points.getJSONObject(i);
				
				serverId = point.getInt(ID);
				name = point.getString(NOME);
				categoriaId = point.getInt(CATEGORIA_ID);
				categoria = point.getString(CATEGORIA);
				sottocategoriaId = point.getInt(SOTTOCATEGORIA_ID);
				sottocategoria = point.getString(SOTTOCATEGORIA);
				descrizione = point.getString(DESCRIZIONE);
				lat = point.getDouble(LATITUDINE);
				lang = point.getLong(LONGITUDINE);
				
				JSONArray imagesArray = point.getJSONArray(IMAGES_ID);
				if (imagesArray.length() > 0) {
					images = new int[imagesArray.length()];
				    for(int j = 0; j < imagesArray.length(); j++){
				        images[j] = imagesArray.getInt(j);
				    }
				}
			    ContentValues pointValues = new ContentValues();
			    
			    pointValues.put(PuntiDbHelper.BLOCKED, false);
			    pointValues.put(PuntiDbHelper.CATEGORY_ID, categoriaId);
			    pointValues.put(PuntiDbHelper.DESCRIZIONE, descrizione);
			    pointValues.put(PuntiDbHelper.FAVOURITE, false);
			    pointValues.put(PuntiDbHelper.LATUTUDE, lat);
			    pointValues.put(PuntiDbHelper.LONGITUDE, lang);
			    pointValues.put(PuntiDbHelper.NOME, name);
			    pointValues.put(PuntiDbHelper.SERVER_ID, serverId);
			    pointValues.put(PuntiDbHelper.SOTTOCATEGORIA_ID, sottocategoriaId);
			    pointValues.put(PuntiDbHelper.IMAGES_ID, 0);
			    //handle the images here fk bullshit and stuff
			    if (currentPoints.contains(serverId))
			    	pointsToUpdateVector.add(pointValues);
			    else
			    	pointsToAddVector.add(pointValues);
			}
			
			if (pointsToAddVector.size() > 0) {
				ContentValues[] cVValues = new ContentValues[pointsToAddVector.size()];
				pointsToAddVector.toArray(cVValues);
				mContentResolver.bulkInsert(PuntiContentProvider.PUNTI_URI, cVValues);
			}
			
			if (pointsToUpdateVector.size() > 0) {
				for (ContentValues cv : pointsToUpdateVector) {
					//mContentResolver.update(PuntiContentProvider.PUNTI_URI, cv, 
					//		PuntiDbHelper.SERVER_ID + "=?", new String[] { cv.getAsInteger(ID) + "" });
					mContentResolver.update(PuntiContentProvider.PUNTI_URI, cv, PuntiDbHelper.SERVER_ID + "=" + cv.getAsInteger(ID), null);
				}
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
