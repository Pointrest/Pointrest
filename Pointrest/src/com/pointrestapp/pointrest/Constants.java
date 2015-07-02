package com.pointrestapp.pointrest;

public final class Constants {
	
	
	public static final String POINTREST_PREFERENCES = "pointrest_prefs";
	public static class SharedPreferences {
		public static final String RAGGIO = "raggio_shared_pref";
		public static final String LANG = "lang";
		public static final String LAT = "lat";
		public static final String CATEGORY_ID = "category_id";
		public static final String ONLY_FAVOURITE = "only_favourite";
		public static final String SUB_CATEGORY_ID = "sub_category_id";
		public static final String SEARCH_ENABLED = "search_enabled";
	}
	
	public static final String CATEGORY_TYPE = "tabtype";
	public static final String BASE_FENCE_ID = "base_fence_id";
	public static class TabType {
		public static final int TUTTO = -1;
	}
	public static class NotificationBlocked {
		public static final int FALSE = 0; 
		public static final int TRUE = 1; 
	}
	public static class Favourite {
		public static final int FALSE = 0; 
		public static final int TRUE = 1; 
	}
	
	public static final String LOCAL_NOTIFICATION_TAG = "tag";
	public static final float POINT_NOTIFICATION_RADIUS = 100;
	public static final float UPDATE_DB_RADIUS_SCATTO = 3000;
	public static int NOTIFICATION_ID = 0;
	
	public static final String BASE_URL = "http://www.pointerest.somee.com/api/";
	public static final String RAN_FOR_THE_FIRST_TIME = "ran_already";
	public static final String TRIGGER_RADIUS_FENCE_ID = "triggerfenceblah";
	public static final String ACCOUNT = "pointrestaccount";
	
	public static final String GEOFENCE_TRIGGERING_LOCATION_LAT = "triggerLat";
	public static final String GEOFENCE_TRIGGERING_LOCATION_LONG = "triggerLong";
	public static final String ERROR_STATUS = "com.pointrestapp.pointrest.ERRRRRRR";
	
}
