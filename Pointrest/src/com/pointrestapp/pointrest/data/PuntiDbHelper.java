package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;

public class PuntiDbHelper implements BaseColumns {

	public static final String TABLE_NAME = "punti";
	public static final String NOME = "nome";
	public static final String TYPE = "type";
	public static final String BLOCKED = "blocked";

	public static final String CREATE_QUERY = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ NOME +  " TEXT NOT NULL, "
			+ BLOCKED +  " INTEGER NOT NULL, "
			+ TYPE + " INTEGER NOT NULL "
			+ ");";
}
