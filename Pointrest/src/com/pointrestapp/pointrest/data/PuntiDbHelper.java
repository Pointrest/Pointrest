package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;

public class PuntiDbHelper implements BaseColumns {

	public static final String TABLE_NAME = "punti";
	public static final String NOME = "nome";
	public static final String CATEGORY_ID = "categoria_id";
	public static final String BLOCKED = "blocked";
	public static final String FAVOURITE = "favourite";
	public static final String LATUTUDE = "lat";
	public static final String LONGITUDE = "long";
	public static final String SOTTOCATEGORIA_ID = "sottocategoria_id";
	public static final String DESCRIZIONE = "desc";
	
	public static final String CREATE_QUERY = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ _ID + " INTEGER PRIMARY KEY, "
			+ CATEGORY_ID + " INTEGER NOT NULL, "
			+ NOME +  " TEXT NOT NULL, "
			+ LATUTUDE + " REAL NOT NULL, "
			+ LONGITUDE + " REAL NOT NULL, "
			+ BLOCKED +  " INTEGER NOT NULL, "
			+ FAVOURITE + " INTEGER NOT NULL, "
			+ SOTTOCATEGORIA_ID +  " INTEGER NOT NULL, "
			+ DESCRIZIONE +  " TEXT NOT NULL "
			+ ");";
}
