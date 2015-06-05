package com.pointrestapp.pointrest.data;

import android.provider.BaseColumns;

public class NotificheBloccateHelper implements BaseColumns{

	public static final String TABLE_NAME = "notifiche_bloccate";
	public static final String NAME = "nome";
	public static final String IMAGE = "image";
	
	public static final String CREATE_QUERY =
			" CREATE TABLE " + TABLE_NAME
			+ " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ NAME + " TEXT NOT NULL, "	
			+ IMAGE + " TEXT NOT NULL "	
			+" );"; 

}
