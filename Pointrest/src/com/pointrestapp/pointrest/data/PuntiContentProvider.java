package com.pointrestapp.pointrest.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PuntiContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.pointrestapp.pointrest.data.PuntiContentProvider";
	public static final String PUNTI_PATH = "punti";
	
	public static final Uri PUNTI_URI =
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + PUNTI_PATH);
	
	private static final int FULL_PUNTI_TABLE = 0;
	private static final int SINGLE_PUNTO = 1;
	
	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mUriMatcher.addURI(AUTHORITY, PUNTI_PATH, FULL_PUNTI_TABLE);
		mUriMatcher.addURI(AUTHORITY, PUNTI_PATH + "/#", SINGLE_PUNTO);
	}
	private DbHelper mHelper;
	
	public static final String MIME_TYPE_PUNTI = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/punti";
	public static final String MIME_TYPE_PUNTO = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/punto";
	
	@Override
	public boolean onCreate() {
		mHelper = new DbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder vQueryBuilder = new SQLiteQueryBuilder();
		
		switch(mUriMatcher.match(uri)) {
			case FULL_PUNTI_TABLE:
				vQueryBuilder.setTables(PuntiDbHelper.TABLE_NAME);
				break;
			case SINGLE_PUNTO:
				vQueryBuilder.setTables(PuntiDbHelper.TABLE_NAME);
				vQueryBuilder.appendWhere(PuntiDbHelper._ID + "=" + uri.getLastPathSegment());
				break;
		}
		SQLiteDatabase vDb = mHelper.getReadableDatabase();
		Cursor vCursor = 
				vQueryBuilder
				.query(vDb, projection, selection, 
						selectionArgs, null, null, sortOrder);
		vCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return vCursor;
	}

	@Override
	public String getType(Uri uri) {
		String vResult = null;
		switch(mUriMatcher.match(uri)) {
		case FULL_PUNTI_TABLE :
			vResult = MIME_TYPE_PUNTI;
			break;
		case SINGLE_PUNTO : 
			vResult =  MIME_TYPE_PUNTO;
			break;
		}
		return vResult;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase vDb;
		long vResult;
		
		switch (mUriMatcher.match(uri)) {
			case FULL_PUNTI_TABLE : 
				vDb = mHelper.getWritableDatabase();
				vResult = vDb.insert(PuntiDbHelper.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(PUNTI_PATH + "/" + vResult);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase vDb = mHelper.getWritableDatabase();
		int vResult = 0;
		String vTmp = null;
		
		switch (mUriMatcher.match(uri)) {
			case SINGLE_PUNTO :
				vTmp = PuntiDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.delete(PuntiDbHelper._ID, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_PUNTI_TABLE :
				vResult = vDb.delete(PuntiDbHelper.TABLE_NAME, selection, selectionArgs);
				break;
		}
		if (vResult > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		return vResult;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase vDb = mHelper.getWritableDatabase();
		int vResult = 0;
		String vTmp = null;
		
		switch (mUriMatcher.match(uri)) {
			case SINGLE_PUNTO :
				vTmp = PuntiDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.update(PuntiDbHelper._ID, values, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_PUNTI_TABLE :
				vResult = vDb.update(PuntiDbHelper.TABLE_NAME, values, selection, selectionArgs);
				break;
		}
		
		if (vResult > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		return vResult;
	}
}
