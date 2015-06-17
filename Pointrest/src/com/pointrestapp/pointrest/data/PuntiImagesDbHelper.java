package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;

public class PuntiImagesDbHelper implements BaseColumns  {
	
	public static final String TABLE_NAME = "punti_images";
	public static final String SERVER_ID = "server_id";
	public static final String PUNTO_ID = "punto_id";
	
	public static final String CREATE_QUERY = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SERVER_ID +  " INTEGER NOT NULL, "
			+ PUNTO_ID +  " INTEGER NOT NULL "
			+ ");";
}
