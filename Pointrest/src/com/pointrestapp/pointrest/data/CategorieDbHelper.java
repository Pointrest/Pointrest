package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;


public class CategorieDbHelper implements BaseColumns {
	
	public static final String TABLE_NAME = "categorie";
	public static final String NAME = "name";
	
	public static final String CREATE_QUERY = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ _ID + " INTEGER PRIMARY KEY, "
			+ NAME +  " TEXT NOT NULL "
			+ ");";
}
