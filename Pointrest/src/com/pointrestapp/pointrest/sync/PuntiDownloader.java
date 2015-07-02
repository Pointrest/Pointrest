package com.pointrestapp.pointrest.sync;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.pointrestapp.pointrest.Constants;
import com.pointrestapp.pointrest.data.CategorieDbHelper;
import com.pointrestapp.pointrest.data.PuntiContentProvider;
import com.pointrestapp.pointrest.data.PuntiDbHelper;
import com.pointrestapp.pointrest.data.PuntiImagesDbHelper;
import com.pointrestapp.pointrest.data.SottocategoriaDbHelper;
import com.pointrestapp.pointrest.models.Categoria;
import com.pointrestapp.pointrest.models.Punto;
import com.pointrestapp.pointrest.models.Sottocategoria;

public class PuntiDownloader implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private double _lat;
	private double _lang;
	private int _raggio;
	private Context mContext;
	private GoogleApiClient mGoogleApiClient;
	private GeofencesHandler mGeofencesHandler;
	private static final String POINTREST_DEBUG = "POINTREST";
	private SharedPreferences pointrestPreferences;
	private ContentResolver mContentResolver;
	private boolean mResolvingError = false;

	public PuntiDownloader(Context cntext) {
		mContext = cntext;
		mContentResolver = cntext.getContentResolver();
		buildGoogleApiClient();
	}

	private synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(mContext, this, this)
				.addApi(LocationServices.API).build();
	}

	public void download() {

		// We're going to sync in the onConnected callback
		if (!mResolvingError) { // more about this later
			mGoogleApiClient.connect();
		}
	}

	void downloadCategories() {
		RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(
				Constants.BASE_URL2).build();

		PointrestService service = retrofit.create(PointrestService.class);

		Callback<List<Categoria>> callback = new Callback<List<Categoria>>() {

			@Override
			public void failure(RetrofitError arg0) {
				handleFailure(arg0.getMessage());
			}

			@Override
			public void success(List<Categoria> arg0, Response arg1) {
				handleCategories(arg0);
			}

		};
		service.listCategorie(callback);
	}

	protected void downloadSottocategories() {
		RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(
				Constants.BASE_URL2).build();

		PointrestService service = retrofit.create(PointrestService.class);

		Callback<List<Sottocategoria>> callback = new Callback<List<Sottocategoria>>() {

			@Override
			public void failure(RetrofitError arg0) {
				handleFailure(arg0.getMessage());
			}

			@Override
			public void success(List<Sottocategoria> arg0, Response arg1) {
				handleSottocategories(arg0);
			}

		};
		service.listSottocategorie(callback);
	}

	protected void downloadPunti() {
		RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(
				Constants.BASE_URL2).build();

		PointrestService service = retrofit.create(PointrestService.class);

		Callback<List<Punto>> callback = new Callback<List<Punto>>() {

			@Override
			public void failure(RetrofitError arg0) {
				handleFailure(arg0.getMessage());
			}

			@Override
			public void success(List<Punto> arg0, Response arg1) {
				handlePoints(arg0);
				finalizeRequest();
			}

		};
		service.listPunti(_lat, _lang, _raggio, callback);
	}

	protected void handleCategories(List<Categoria> categorie) {
		Log.d("POINTREST", "cats downloaded!");

		Vector<ContentValues> categoriesToUpdateVector = new Vector<ContentValues>(
				categorie.size());
		Vector<ContentValues> categoriesToAddVector = new Vector<ContentValues>(
				categorie.size());

		Set<Integer> categoriesCurrentlyInDb = new TreeSet<Integer>();
		Set<Integer> categoriesRecieved = new TreeSet<Integer>();

		Cursor categorieCursor = null;

		try {

			// We'll use this set to figure out if we already have the item in
			// the db
			categorieCursor = mContext.getContentResolver().query(
					PuntiContentProvider.CATEGORIE_URI,
					new String[] { CategorieDbHelper._ID }, null, null, null);
			int serverIdIndex = categorieCursor
					.getColumnIndex(CategorieDbHelper._ID);

			while (categorieCursor.moveToNext()) {
				categoriesCurrentlyInDb.add(categorieCursor
						.getInt(serverIdIndex));
			}

			for (Categoria categoria : categorie) {

				int id;
				String name;

				id = categoria.ID;
				name = categoria.CategoryName;

				ContentValues cv = new ContentValues();
				cv.put(CategorieDbHelper._ID, id);
				cv.put(CategorieDbHelper.NAME, name);

				categoriesRecieved.add(id);

				if (categoriesCurrentlyInDb.contains(id))
					categoriesToUpdateVector.add(cv);
				else
					categoriesToAddVector.add(cv);

			}

		} finally {
			if (categorieCursor != null)
				categorieCursor.close();
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
				mContentResolver.update(
						PuntiContentProvider.CATEGORIE_URI,
						vals,
						CategorieDbHelper._ID + "="
								+ vals.getAsInteger(CategorieDbHelper._ID),
						null);
			}
		}
	}

	protected void handleSottocategories(List<Sottocategoria> sottocategorie) {
		Log.d("POINTREST", "sottocats downloaded!");

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

			for (Sottocategoria sottocategoria : sottocategorie) {

				int id;
				int catId;
				String name;

				id = sottocategoria.ID;
				catId = sottocategoria.CategoriaID;
				name = sottocategoria.SubCategoryName;

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
					mContentResolver
							.update(PuntiContentProvider.SOTTOCATEGORIE_URI,
									vals,
									SottocategoriaDbHelper._ID
											+ "="
											+ vals.getAsInteger(SottocategoriaDbHelper._ID),
									null);
				}
			}
		} finally {
			if (sottoCategorieCursor != null)
				sottoCategorieCursor.close();
		}

	}

	protected void handlePoints(List<Punto> points) {
		Log.d("POINTREST", "points downloaded!");

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

			for (Punto point : points) {

				int id;
				String name;
				int categoriaId;
				int sottocategoriaId;
				String descrizione;
				double lat;
				double lang;
				int[] imagesArray;

				id = point.ID;
				name = point.Nome;
				categoriaId = point.CategoriaID;
				sottocategoriaId = point.SottocategoriaID;
				descrizione = point.Descrizione;
				lat = point.Latitudine;
				lang = point.Longitudine;
				imagesArray = point.ImagesID;

				if (imagesArray.length > 0) {
					ContentValues imageCv = null;
					for (int j = 0; j < imagesArray.length; j++) {
						int imgId = imagesArray[j];
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
					mContentResolver.update(
							PuntiContentProvider.PUNTI_URI,
							cv,
							PuntiDbHelper._ID + "="
									+ cv.getAsInteger(PuntiDbHelper._ID), null);
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

		} finally {
			if (imagesCursor != null)
				imagesCursor.close();
			if (puntiCursor != null)
				puntiCursor.close();
		}

	}

	private void handleFailure(String message) {
		Intent intent = new Intent(Constants.ERROR_STATUS);
		mContext.sendBroadcast(intent);
		if (mGoogleApiClient != null)
			mGoogleApiClient.disconnect();
		if (message == null)
			message = "Failure";
		Log.e(POINTREST_DEBUG, message);
	}

	private void finalizeRequest() {
		Log.e(POINTREST_DEBUG, "finilizing request");
		pointrestPreferences.edit()
				.putBoolean(Constants.RAN_FOR_THE_FIRST_TIME, true).commit();
		mGeofencesHandler.putUpGeofences();
		mGoogleApiClient.disconnect();
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

		_raggio = pointrestPreferences.getInt(
				Constants.SharedPreferences.RAGGIO, 10);
		_lang = pointrestPreferences.getFloat(Constants.SharedPreferences.LANG,
				0);
		_lat = pointrestPreferences
				.getFloat(Constants.SharedPreferences.LAT, 0);

		Log.d(POINTREST_DEBUG, "Starting download, lat is " + _lat
				+ " lang is " + _lang + " raggio is " + _raggio);
		downloadCategories();
		downloadSottocategories();
		downloadPunti();

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}
}