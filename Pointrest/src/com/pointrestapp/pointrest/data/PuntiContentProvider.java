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
	public static final String CATEGORIE_PATH = "categorie";
	public static final String SOTTOCATEGORIE_PATH = "sottocategorie";
	public static final String PUNTI_IMAGES_PATH = "punti_images";

	public static final Uri PUNTI_URI =
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + PUNTI_PATH);
	public static final Uri CATEGORIE_URI =
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + CATEGORIE_PATH);
	public static final Uri SOTTOCATEGORIE_URI =
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + SOTTOCATEGORIE_PATH);
	public static final Uri PUNTI_IMAGES_URI =
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + PUNTI_IMAGES_PATH);
	public static final Uri DUMMY_NOTIFIER_URI = 
			Uri.parse(ContentResolver.SCHEME_CONTENT +
					"://" + AUTHORITY + "/" + "doneLoading");			
	
	private static final int FULL_PUNTI_TABLE = 0;
	private static final int SINGLE_PUNTO = 1;
	private static final int FULL_CATEGORIE_TABLE = 2;
	private static final int SINGLE_CATEGORIA = 3;
	private static final int FULL_SOTTOCATEGORIE_TABLE = 4;
	private static final int SINGLE_SOTTOCATEGORIA = 5;
	private static final int FULL_PUNTI_IMAGES_TABLE = 6;
	private static final int SINGLE_PUNTO_IMAGE = 7;
	
	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mUriMatcher.addURI(AUTHORITY, PUNTI_PATH, FULL_PUNTI_TABLE);
		mUriMatcher.addURI(AUTHORITY, PUNTI_PATH + "/#", SINGLE_PUNTO);
		mUriMatcher.addURI(AUTHORITY, CATEGORIE_PATH, FULL_CATEGORIE_TABLE);
		mUriMatcher.addURI(AUTHORITY, CATEGORIE_PATH + "/#", SINGLE_CATEGORIA);
		mUriMatcher.addURI(AUTHORITY, SOTTOCATEGORIE_PATH, FULL_SOTTOCATEGORIE_TABLE);
		mUriMatcher.addURI(AUTHORITY, SOTTOCATEGORIE_PATH + "/#", SINGLE_SOTTOCATEGORIA);
		mUriMatcher.addURI(AUTHORITY, PUNTI_IMAGES_PATH, FULL_PUNTI_IMAGES_TABLE);
		mUriMatcher.addURI(AUTHORITY, PUNTI_IMAGES_PATH + "/#", SINGLE_PUNTO_IMAGE);
	}
	private DbHelper mHelper;
	
	public static final String MIME_TYPE_PUNTI = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/punti";
	public static final String MIME_TYPE_PUNTO = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/punto";
	public static final String MIME_TYPE_CATEGORIE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/categorie";
	public static final String MIME_TYPE_CATEGORIA = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/categoria";
	public static final String MIME_TYPE_SOTTOCATEGORIE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/sottocategorie";
	public static final String MIME_TYPE_SOTTOCATEGORIA = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/sottocategoria";
	public static final String MIME_TYPE_PUNTI_IMAGES = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/punti_images";
	public static final String MIME_TYPE_PUNTI_IMAGE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/punto_image";
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
			case FULL_CATEGORIE_TABLE:
				vQueryBuilder.setTables(CategorieDbHelper.TABLE_NAME);
				break;
			case SINGLE_CATEGORIA:
				vQueryBuilder.setTables(CategorieDbHelper.TABLE_NAME);
				vQueryBuilder.appendWhere(CategorieDbHelper._ID + "=" + uri.getLastPathSegment());
				break;
			case FULL_SOTTOCATEGORIE_TABLE:
				vQueryBuilder.setTables(SottocategoriaDbHelper.TABLE_NAME);
				break;
			case SINGLE_SOTTOCATEGORIA:
				vQueryBuilder.setTables(SottocategoriaDbHelper.TABLE_NAME);
				vQueryBuilder.appendWhere(SottocategoriaDbHelper._ID + "=" + uri.getLastPathSegment());
				break;
			case FULL_PUNTI_IMAGES_TABLE:
				vQueryBuilder.setTables(PuntiImagesDbHelper.TABLE_NAME);
				break;
			case SINGLE_PUNTO_IMAGE:
				vQueryBuilder.setTables(PuntiImagesDbHelper.TABLE_NAME);
				vQueryBuilder.appendWhere(PuntiImagesDbHelper._ID + "=" + uri.getLastPathSegment());
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
		case FULL_CATEGORIE_TABLE :
			vResult = MIME_TYPE_CATEGORIE;
			break;
		case SINGLE_CATEGORIA : 
			vResult =  MIME_TYPE_CATEGORIA;
			break;
		case FULL_SOTTOCATEGORIE_TABLE :
			vResult = MIME_TYPE_SOTTOCATEGORIE;
			break;
		case SINGLE_SOTTOCATEGORIA : 
			vResult =  MIME_TYPE_SOTTOCATEGORIA;
			break;
		case FULL_PUNTI_IMAGES_TABLE :
			vResult = MIME_TYPE_PUNTI_IMAGES;
			break;
		case SINGLE_PUNTO_IMAGE : 
			vResult =  MIME_TYPE_PUNTI_IMAGE;
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
			case FULL_CATEGORIE_TABLE :
				vDb = mHelper.getWritableDatabase();
				vResult = vDb.insert(CategorieDbHelper.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(CATEGORIE_PATH + "/" + vResult);
			case FULL_SOTTOCATEGORIE_TABLE:
				vDb = mHelper.getWritableDatabase();
				vResult = vDb.insert(SottocategoriaDbHelper.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(SOTTOCATEGORIE_PATH + "/" + vResult);
			case FULL_PUNTI_IMAGES_TABLE:
				vDb = mHelper.getWritableDatabase();
				vResult = vDb.insert(PuntiImagesDbHelper.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(PUNTI_IMAGES_PATH + "/" + vResult);
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
			case SINGLE_CATEGORIA :
				vTmp = PuntiDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.delete(CategorieDbHelper._ID, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_CATEGORIE_TABLE :
				vResult = vDb.delete(CategorieDbHelper.TABLE_NAME, selection, selectionArgs);
				break;
			case SINGLE_SOTTOCATEGORIA :
				vTmp = PuntiDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.delete(SottocategoriaDbHelper._ID, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_SOTTOCATEGORIE_TABLE :
				vResult = vDb.delete(SottocategoriaDbHelper.TABLE_NAME, selection, selectionArgs);
				break;
			case SINGLE_PUNTO_IMAGE :
				vTmp = PuntiDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.delete(PuntiImagesDbHelper._ID, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_PUNTI_IMAGES_TABLE :
				vResult = vDb.delete(PuntiImagesDbHelper.TABLE_NAME, selection, selectionArgs);
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
			case SINGLE_CATEGORIA :
				vTmp = CategorieDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.update(CategorieDbHelper._ID, values, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_CATEGORIE_TABLE :
				vResult = vDb.update(CategorieDbHelper.TABLE_NAME, values, selection, selectionArgs);
				break;
			case SINGLE_SOTTOCATEGORIA :
				vTmp = SottocategoriaDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.update(SottocategoriaDbHelper._ID, values, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_SOTTOCATEGORIE_TABLE :
				vResult = vDb.update(SottocategoriaDbHelper.TABLE_NAME, values, selection, selectionArgs);
				break;
			case SINGLE_PUNTO_IMAGE :
				vTmp = PuntiImagesDbHelper._ID + " = " + uri.getLastPathSegment();
				vResult = vDb.update(PuntiImagesDbHelper._ID, values, selection + " AND " + vTmp, selectionArgs);	
				break;
			case FULL_PUNTI_IMAGES_TABLE :
				vResult = vDb.update(PuntiImagesDbHelper.TABLE_NAME, values, selection, selectionArgs);
				break;
		}
		
		if (vResult > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		return vResult;
	}
}
