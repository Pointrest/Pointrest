package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;

public class PuntiDbHelper implements BaseColumns {

	public static final String TABLE_NAME = "punti";
	public static final String NOME = "nome";
	public static final String TYPE = "type";
	public static final String BLOCKED = "blocked";
	public static final String FAVOURITE = "favourite";
	public static final String LATUTUDE = "lat";
	public static final String LONGITUDE = "long";
	
	public static final String CREATE_QUERY = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TYPE + " INTEGER NOT NULL, "
			+ NOME +  " TEXT NOT NULL, "
			+ LATUTUDE + " REAL NOT NULL, "
			+ LONGITUDE + " REAL NOT NULL, "
			+ BLOCKED +  " INTEGER NOT NULL, "
			+ FAVOURITE + " INTEGER NOT NULL "
			+ ");";
}
