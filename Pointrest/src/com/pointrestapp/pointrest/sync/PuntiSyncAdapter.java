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
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;


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
		double lang = pointrestPreferences.getFloat(Constants.SharedPreferences.LANG, 65);
		double lat = pointrestPreferences.getFloat(Constants.SharedPreferences.LAT, 45);
		
		//Try and get the points
		try {
			getAllCategorie();
			getAllSottoCategorie();
			getPoints(lang, lat, raggio);
			pointrestPreferences.edit().putBoolean(Constants.RAN_FOR_THE_FIRST_TIME, true).commit();
			mContext.getContentResolver().notifyChange(PuntiContentProvider.DUMMY_NOTIFIER_URI, null, false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    private void getAllSottoCategorie() {

    	String url = "sottocategorie";
    	
        PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray sottocategorie) {
            	parseSottoCategorieJSONArray(sottocategorie);
            }
        });
	}

	private void getAllCategorie() {

    	String url = "categorie";
    	
        PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray categorie) {
            	parseCategorieJSONArray(categorie);
            }
        });
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
            	parsePuntiJSONArray(points);
            }
        });
    }

	private void parseCategorieJSONArray(JSONArray categorie) {
		
        final String ID = "ID";
        final String CATEGORY_NAME = "CategoryName";
        
        //We need to be careful not to add double items so we keep track of two collections
		Vector<ContentValues> categoriesToUpdateVector = new Vector<ContentValues>(categorie.length());
		Vector<ContentValues> categoriesToAddVector = new Vector<ContentValues>(categorie.length());
		
		//We'll use this set to figure out if we already have the item in the db
		Set<Integer> categoriesCurrentlyInDb = new TreeSet<Integer>();
		Cursor cursor = mContext.getContentResolver().query(PuntiContentProvider.CATEGORIE_URI, new String[]{CategorieDbHelper._ID}, null, null, null);
		int serverIdIndex = cursor.getColumnIndex(CategorieDbHelper._ID);
		
		while (cursor.moveToNext()) {
			categoriesCurrentlyInDb.add(cursor.getInt(serverIdIndex));
		}
        
        try {
        	
            for (int i = 0; i < categorie.length(); ++i) {
            	
            	int id;
            	String name;
            	
            	JSONObject categoria = categorie.getJSONObject(i);
            	
            	id = categoria.getInt(ID);
            	name = categoria.getString(CATEGORY_NAME);
            	
            	ContentValues cv = new ContentValues();
            	cv.put(CategorieDbHelper._ID, id);
            	cv.put(CategorieDbHelper.NAME, name);
            	
            	if (categoriesCurrentlyInDb.contains(id))
            		categoriesToUpdateVector.add(cv);
            	else
            		categoriesToAddVector.add(cv);
            	
            	//add to content provider
    			if (categoriesToAddVector.size() > 0) {
    				ContentValues[] cVValues = new ContentValues[categoriesToAddVector.size()];
    				categoriesToAddVector.toArray(cVValues);
    				mContentResolver.bulkInsert(PuntiContentProvider.CATEGORIE_URI, cVValues);
    			}
    			
    			if (categoriesToUpdateVector.size() > 0) {
    				for (ContentValues vals : categoriesToUpdateVector) {
    					mContentResolver.update(PuntiContentProvider.CATEGORIE_URI,
    							vals, CategorieDbHelper._ID + "=" + vals.getAsInteger(ID), null);
    				}
    			}
            	
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void parseSottoCategorieJSONArray(JSONArray sottocategorie) {
		
        final String ID = "ID";
        final String CATEGORIA_ID = "CategoriaID";
        final String SOTTOCATEGORIA_NAME = "SubCategoryName";
        
        
		Vector<ContentValues> sottocategorieToUpdateVector = new Vector<ContentValues>(sottocategorie.length());
		Vector<ContentValues> sottocategorieToAddVector = new Vector<ContentValues>(sottocategorie.length());
		
		Cursor cursor = mContext.getContentResolver().query(PuntiContentProvider.SOTTOCATEGORIE_URI, new String[]{SottocategoriaDbHelper._ID}, null, null, null);
		int serverIdIndex = cursor.getColumnIndex(SottocategoriaDbHelper._ID);
		Set<Integer> sottocategorieCurrentlyInDb = new TreeSet<Integer>();
		
		while (cursor.moveToNext()) {
			sottocategorieCurrentlyInDb.add(cursor.getInt(serverIdIndex));
		}
        
        try {
        	
            for (int i = 0; i < sottocategorie.length(); ++i) {
            	
            	int id;
            	int catId;
            	String name;
            	
            	JSONObject categoria = sottocategorie.getJSONObject(i);
            	
            	id = categoria.getInt(ID);
            	catId = categoria.getInt(CATEGORIA_ID);
            	name = categoria.getString(SOTTOCATEGORIA_NAME);
            	
            	ContentValues cv = new ContentValues();
            	cv.put(SottocategoriaDbHelper._ID, id);
            	cv.put(SottocategoriaDbHelper.NAME, name);
            	cv.put(SottocategoriaDbHelper.CATEGORIA_ID, catId);
            	
            	if (sottocategorieCurrentlyInDb.contains(id))
            		sottocategorieToUpdateVector.add(cv);
            	else
            		sottocategorieToAddVector.add(cv);
            	
            	//add to content provider
    			if (sottocategorieToAddVector.size() > 0) {
    				ContentValues[] cVValues = new ContentValues[sottocategorieToAddVector.size()];
    				sottocategorieToAddVector.toArray(cVValues);
    				mContentResolver.bulkInsert(PuntiContentProvider.SOTTOCATEGORIE_URI, cVValues);
    			}
    			
    			if (sottocategorieToUpdateVector.size() > 0) {
    				for (ContentValues vals : sottocategorieToUpdateVector) {
    					mContentResolver.update(PuntiContentProvider.SOTTOCATEGORIE_URI,
    							vals, SottocategoriaDbHelper._ID + "=" + vals.getAsInteger(ID), null);
    				}
    			}
            	
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void parsePuntiJSONArray(JSONArray points) {
		
		//Names of JSON props to extracts
		final String ID = "ID";
		final String NOME = "Nome";
		final String CATEGORIA_ID = "CategoriaID";
		final String SOTTOCATEGORIA_ID = "SottocategoriaID";
		final String DESCRIZIONE = "Descrizione";
		final String LATITUDINE = "Latitudine";
		final String LONGITUDINE = "Longitudine";
		final String IMAGES_ID = "ImagesID";
		
		try {
			
			Vector<ContentValues> pointsToAddVector = new Vector<ContentValues>(points.length());
			Vector<ContentValues> pointsToUpdateVector = new Vector<ContentValues>(points.length());
			
			Vector<ContentValues> imagesToAddVector = new Vector<ContentValues>();
			
			Cursor cursor = mContext.getContentResolver().query(PuntiContentProvider.PUNTI_URI, new String[]{PuntiDbHelper._ID}, null, null, null);
			int serverIdIndex = cursor.getColumnIndex(PuntiDbHelper._ID);
			Set<Integer> pointsCurrentlyInDb = new TreeSet<Integer>();
			
			while (cursor.moveToNext()) {
				pointsCurrentlyInDb.add(cursor.getInt(serverIdIndex));
			}
			
			Cursor imagesCursor = mContext.getContentResolver().query(PuntiContentProvider.PUNTI_IMAGES_URI, new String[]{PuntiImagesDbHelper._ID}, null, null, null);
			int imageIdIndex = imagesCursor.getColumnIndex(PuntiImagesDbHelper._ID);
			Set<Integer> imagesCurrentlyInDb = new TreeSet<Integer>();
			
			while (imagesCursor.moveToNext()) {
				imagesCurrentlyInDb.add(imagesCursor.getInt(imageIdIndex));
			}
			
			for (int i = 0; i < points.length(); ++i) {
				
				int serverId;
				String name;
				int categoriaId;
				int sottocategoriaId;
				String descrizione;
				double lat;
				double lang;
				
				JSONObject point = points.getJSONObject(i);
				
				serverId = point.getInt(ID);
				name = point.getString(NOME);
				categoriaId = point.getInt(CATEGORIA_ID);
				sottocategoriaId = point.getInt(SOTTOCATEGORIA_ID);
				descrizione = point.getString(DESCRIZIONE);
				lat = point.getDouble(LATITUDINE);
				lang = point.getDouble(LONGITUDINE);
				
				JSONArray imagesArray = point.getJSONArray(IMAGES_ID);
				if (imagesArray.length() > 0) {
					ContentValues imageCv = null;
				    for(int j = 0; j < imagesArray.length(); j++){
				    	int imgId = imagesArray.getInt(j);
						imageCv = new ContentValues();
						imageCv.put(PuntiImagesDbHelper._ID, imgId);
						imageCv.put(PuntiImagesDbHelper.PUNTO_ID, serverId);
						if (!imagesCurrentlyInDb.contains(imgId))
							imagesToAddVector.add(imageCv);
				    }
				}
				
				
			    ContentValues pointValues = new ContentValues();
			    
			    pointValues.put(PuntiDbHelper.BLOCKED, 1);
			    pointValues.put(PuntiDbHelper.CATEGORY_ID, categoriaId);
			    pointValues.put(PuntiDbHelper.DESCRIZIONE, descrizione);
			    pointValues.put(PuntiDbHelper.FAVOURITE, 1);
			    pointValues.put(PuntiDbHelper.LATUTUDE, lat);
			    pointValues.put(PuntiDbHelper.LONGITUDE, lang);
			    pointValues.put(PuntiDbHelper.NOME, name);
			    pointValues.put(PuntiDbHelper._ID, serverId);
			    pointValues.put(PuntiDbHelper.SOTTOCATEGORIA_ID, sottocategoriaId);
			    
			    if (pointsCurrentlyInDb.contains(serverId))
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
					mContentResolver.update(PuntiContentProvider.PUNTI_URI, cv, PuntiDbHelper._ID + "=" + cv.getAsInteger(ID), null);
				}
			}
			
			if (imagesToAddVector.size() > 0) {
				ContentValues[] cvImageValues = new ContentValues[imagesToAddVector.size()];
				imagesToAddVector.toArray(cvImageValues);
				mContentResolver.bulkInsert(PuntiContentProvider.PUNTI_IMAGES_URI, cvImageValues);
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public interface OnDataReadyListener {
		void onDataReady();
	}

}
