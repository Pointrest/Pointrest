package com.pointrestapp.pointrest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "pointrest.db";
	private final static int DB_VERSION = 8;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CategorieDbHelper.CREATE_QUERY);
		db.execSQL(SottocategoriaDbHelper.CREATE_QUERY);
		db.execSQL(PuntiImagesDbHelper.CREATE_QUERY);
		db.execSQL(PuntiDbHelper.CREATE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CategorieDbHelper.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SottocategoriaDbHelper.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PuntiImagesDbHelper.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PuntiDbHelper.TABLE_NAME);

		onCreate(db);
	}
}
