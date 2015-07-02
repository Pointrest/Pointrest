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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;

public class PuntiSyncAdapterOld extends AbstractThreadedSyncAdapter implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String POINTREST_DEBUG = "POINTREST";
	private ContentResolver mContentResolver;
	private Context mContext;
	private GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;
	private int raggio;
	private float lang;
	private float lat;
	private SharedPreferences pointrestPreferences;
	private GeofencesHandler mGeofencesHandler;

	public PuntiSyncAdapterOld(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		mContentResolver = mContext.getContentResolver();
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

	private void getAllCategorie() {
		
		String url = "categorie";

		PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray categorie) {
				parseCategorieJSONArray(categorie);
				getPoints(lang, lat, raggio);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

		});
	}

	private void getAllSottoCategorie() {

		String url = "sottocategorie";

		PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray sottocategorie) {
				parseSottoCategorieJSONArray(sottocategorie);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

		});
	}

	public void getPoints(double lang, double lat, int raggio) {

		String url = "pi/filter/" + lat + "/" + lang + "/" + raggio;

		PuntiRestClient.get(url, null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				System.out.println();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray points) {
				parsePuntiJSONArray(points);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				onDownloadFailiure();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

		});
	}

	private void parseCategorieJSONArray(JSONArray categorie) {

		final String ID = "ID";
		final String CATEGORY_NAME = "CategoryName";

		Cursor categorieCursor = null;

		// We need to be careful not to add double items so we keep track of two
		// collections
		Vector<ContentValues> categoriesToUpdateVector = new Vector<ContentValues>(
				categorie.length());
		Vector<ContentValues> categoriesToAddVector = new Vector<ContentValues>(
				categorie.length());

		Set<Integer> categoriesCurrentlyInDb = new TreeSet<Integer>();
		Set<Integer> categoriesRecieved = new TreeSet<Integer>();

		try {

			// We'll use this set to figure out if we already have the item in
			// the db
			categorieCursor = mContentResolver.query(
					PuntiContentProvider.CATEGORIE_URI,
					new String[] { CategorieDbHelper._ID }, null, null, null);
			int serverIdIndex = categorieCursor
					.getColumnIndex(CategorieDbHelper._ID);

			while (categorieCursor.moveToNext()) {
				categoriesCurrentlyInDb.add(categorieCursor
						.getInt(serverIdIndex));
			}

			for (int i = 0; i < categorie.length(); ++i) {

				int id;
				String name;

				JSONObject categoria = categorie.getJSONObject(i);

				id = categoria.getInt(ID);
				name = categoria.getString(CATEGORY_NAME);

				ContentValues cv = new ContentValues();
				cv.put(CategorieDbHelper._ID, id);
				cv.put(CategorieDbHelper.NAME, name);

				categoriesRecieved.add(id);

				if (categoriesCurrentlyInDb.contains(id))
					categoriesToUpdateVector.add(cv);
				else
					categoriesToAddVector.add(cv);
			}

			Set<Integer> categoriesToRemove = new TreeSet<Integer>(
					categoriesCurrentlyInDb);
			categoriesToRemove.removeAll(categoriesRecieved);

			for (Integer categoryIdToRemove : categoriesToRemove) {
				mContentResolver.delete(PuntiContentProvider.CATEGORIE_URI,
						CategorieDbHelper._ID + "=?", new String[] { ""
								+ categoryIdToRemove });
			}

			// add to content provider
			if (categoriesToAddVector.size() > 0) {
				ContentValues[] cVValues = new ContentValues[categoriesToAddVector
						.size()];
				categoriesToAddVector.toArray(cVValues);
				mContentResolver.bulkInsert(PuntiContentProvider.CATEGORIE_URI,
						cVValues);
			}

			if (categoriesToUpdateVector.size() > 0) {
				for (ContentValues vals : categoriesToUpdateVector) {
					mContentResolver
							.update(PuntiContentProvider.CATEGORIE_URI,
									vals,
									CategorieDbHelper._ID + "="
											+ vals.getAsInteger(ID), null);
				}
			}
			getAllSottoCategorie();
		} catch (Exception e) {
			handleException(e);
		} finally {
			if (categorieCursor != null)
				categorieCursor.close();
		}
	}

	private void parseSottoCategorieJSONArray(JSONArray sottocategorie) {

		final String ID = "ID";
		final String CATEGORIA_ID = "CategoriaID";
		final String SOTTOCATEGORIA_NAME = "SubCategoryName";

		Cursor sottoCategorieCursor = null;

		Vector<ContentValues> sottocategorieToUpdateVector = new Vector<ContentValues>();
		Vector<ContentValues> sottocategorieToAddVector = new Vector<ContentValues>();

		Set<Integer> sottocategorieCurrentlyInDb = new TreeSet<Integer>();
		Set<Integer> sottocategoriesRecieved = new TreeSet<Integer>();

		try {

			sottoCategorieCursor = mContentResolver.query(
					PuntiContentProvider.SOTTOCATEGORIE_URI,
					new String[] { SottocategoriaDbHelper._ID }, null, null,
					null);
			int sottocategoriaIdIndex = sottoCategorieCursor
					.getColumnIndex(SottocategoriaDbHelper._ID);

			while (sottoCategorieCursor.moveToNext()) {
				sottocategorieCurrentlyInDb.add(sottoCategorieCursor
						.getInt(sottocategoriaIdIndex));
			}

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

				sottocategoriesRecieved.add(id);
				
				if (sottocategorieCurrentlyInDb.contains(id))
					sottocategorieToUpdateVector.add(cv);
				else
					sottocategorieToAddVector.add(cv);
			}

			Set<Integer> sottocategoriesToRemove = new TreeSet<Integer>(
					sottocategorieCurrentlyInDb);
			sottocategoriesToRemove.removeAll(sottocategoriesRecieved);

			for (Integer sottocategoryIdToRemove : sottocategoriesToRemove) {
				mContentResolver.delete(
						PuntiContentProvider.SOTTOCATEGORIE_URI,
						SottocategoriaDbHelper._ID + "=?", new String[] { ""
								+ sottocategoryIdToRemove });
			}

			// add to content provider
			if (sottocategorieToAddVector.size() > 0) {
				ContentValues[] cVValues = new ContentValues[sottocategorieToAddVector
						.size()];
				sottocategorieToAddVector.toArray(cVValues);
				mContentResolver.bulkInsert(
						PuntiContentProvider.SOTTOCATEGORIE_URI, cVValues);
			}

			if (sottocategorieToUpdateVector.size() > 0) {
				for (ContentValues vals : sottocategorieToUpdateVector) {
					mContentResolver.update(
							PuntiContentProvider.SOTTOCATEGORIE_URI,
							vals,
							SottocategoriaDbHelper._ID + "="
									+ vals.getAsInteger(ID), null);
				}
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			if (sottoCategorieCursor != null)
				sottoCategorieCursor.close();
		}
	}

	private void parsePuntiJSONArray(JSONArray points) {

		// Names of JSON props to extracts
		final String ID = "ID";
		final String NOME = "Nome";
		final String CATEGORIA_ID = "CategoriaID";
		final String SOTTOCATEGORIA_ID = "SottocategoriaID";
		final String DESCRIZIONE = "Descrizione";
		final String LATITUDINE = "Latitudine";
		final String LONGITUDINE = "Longitudine";
		final String IMAGES_ID = "ImagesID";

		Cursor imagesCursor = null;
		Cursor puntiCursor = null;

		Vector<ContentValues> pointsToAddVector = new Vector<ContentValues>();
		Vector<ContentValues> pointsToUpdateVector = new Vector<ContentValues>();

		Set<Integer> pointsCurrentlyInDb = new TreeSet<Integer>();
		Set<Integer> pointsRecieved = new TreeSet<Integer>();

		Vector<ContentValues> imagesToAddVector = new Vector<ContentValues>();
		Set<Integer> imagesCurrentlyInDb = new TreeSet<Integer>();

		try {

			puntiCursor = mContentResolver.query(
					PuntiContentProvider.PUNTI_URI,
					new String[] { PuntiDbHelper._ID }, null, null, null);
			int serverIdIndex = puntiCursor.getColumnIndex(PuntiDbHelper._ID);

			while (puntiCursor.moveToNext()) {
				pointsCurrentlyInDb.add(puntiCursor.getInt(serverIdIndex));
			}

			imagesCursor = mContentResolver.query(
					PuntiContentProvider.PUNTI_IMAGES_URI,
					new String[] { PuntiImagesDbHelper._ID }, null, null, null);
			int imageIdIndex = imagesCursor
					.getColumnIndex(PuntiImagesDbHelper._ID);

			while (imagesCursor.moveToNext()) {
				imagesCurrentlyInDb.add(imagesCursor.getInt(imageIdIndex));
			}

			for (int i = 0; i < points.length(); ++i) {

				int id;
				String name;
				int categoriaId;
				int sottocategoriaId;
				String descrizione;
				double lat;
				double lang;

				JSONObject point = points.getJSONObject(i);

				id = point.getInt(ID);
				name = point.getString(NOME);
				categoriaId = point.getInt(CATEGORIA_ID);
				sottocategoriaId = point.getInt(SOTTOCATEGORIA_ID);
				descrizione = point.getString(DESCRIZIONE);
				lat = point.getDouble(LATITUDINE);
				lang = point.getDouble(LONGITUDINE);

				JSONArray imagesArray = point.getJSONArray(IMAGES_ID);
				if (imagesArray.length() > 0) {
					ContentValues imageCv = null;
					for (int j = 0; j < imagesArray.length(); j++) {
						int imgId = imagesArray.getInt(j);
						imageCv = new ContentValues();
						imageCv.put(PuntiImagesDbHelper._ID, imgId);
						imageCv.put(PuntiImagesDbHelper.PUNTO_ID, id);
						if (!imagesCurrentlyInDb.contains(imgId))
							imagesToAddVector.add(imageCv);
					}
				}

				ContentValues pointValues = new ContentValues();

				pointValues.put(PuntiDbHelper.BLOCKED, 0);
				pointValues.put(PuntiDbHelper.CATEGORY_ID, categoriaId);
				pointValues.put(PuntiDbHelper.DESCRIZIONE, descrizione);
				pointValues.put(PuntiDbHelper.FAVOURITE, 0);
				pointValues.put(PuntiDbHelper.LATUTUDE, lat);
				pointValues.put(PuntiDbHelper.LONGITUDE, lang);
				pointValues.put(PuntiDbHelper.NOME, name);
				pointValues.put(PuntiDbHelper._ID, id);
				pointValues.put(PuntiDbHelper.SOTTOCATEGORIA_ID,
						sottocategoriaId);

				pointsRecieved.add(id);

				if (pointsCurrentlyInDb.contains(id))
					pointsToUpdateVector.add(pointValues);
				else
					pointsToAddVector.add(pointValues);
			}

			Set<Integer> pointsToRemove = new TreeSet<Integer>(
					pointsCurrentlyInDb);
			pointsToRemove.removeAll(pointsRecieved);

			for (Integer pointIdToRemove : pointsToRemove) {
				mContentResolver.delete(PuntiContentProvider.PUNTI_URI,
						PuntiDbHelper._ID + "=?", new String[] { ""
								+ pointIdToRemove });
			}

			if (pointsToAddVector.size() > 0) {
				ContentValues[] cVValues = new ContentValues[pointsToAddVector
						.size()];
				pointsToAddVector.toArray(cVValues);
				mContentResolver.bulkInsert(PuntiContentProvider.PUNTI_URI,
						cVValues);
			}

			if (pointsToUpdateVector.size() > 0) {
				for (ContentValues cv : pointsToUpdateVector) {
					// mContentResolver.update(PuntiContentProvider.PUNTI_URI,
					// cv,
					// PuntiDbHelper.SERVER_ID + "=?", new String[] {
					// cv.getAsInteger(ID) + "" });
					mContentResolver
							.update(PuntiContentProvider.PUNTI_URI,
									cv,
									PuntiDbHelper._ID + "="
											+ cv.getAsInteger(ID), null);
				}
			}

			if (imagesToAddVector.size() > 0) {
				ContentValues[] cvImageValues = new ContentValues[imagesToAddVector
						.size()];
				imagesToAddVector.toArray(cvImageValues);
				mContentResolver.bulkInsert(
						PuntiContentProvider.PUNTI_IMAGES_URI, cvImageValues);
			}

			finalizeRequest();

		} catch (JSONException e) {
			handleException(e);
			e.printStackTrace();
		} finally {
			if (imagesCursor != null)
				imagesCursor.close();
			if (puntiCursor != null)
				puntiCursor.close();
		}
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

		Log.d(POINTREST_DEBUG, "Starting download old, lat is " + lat + " lang is "
				+ lang + " raggio is " + raggio);

		getAllCategorie();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	private void handleException(Exception e) {
		String message = "errorrrrrr";
		if (e != null)
			message = e.getMessage();
		if (mGoogleApiClient != null)
			mGoogleApiClient.disconnect();
		Log.e(POINTREST_DEBUG, message);
	}

	private void finalizeRequest() {
		Log.e(POINTREST_DEBUG, "finilizing request");
		pointrestPreferences.edit()
				.putBoolean(Constants.RAN_FOR_THE_FIRST_TIME, true).commit();
		mGeofencesHandler.putUpGeofences();
		mGoogleApiClient.disconnect();
	}

	private void onDownloadFailiure() {
		Intent intent = new Intent(Constants.ERROR_STATUS);
		mContext.sendBroadcast(intent);
	}
}
