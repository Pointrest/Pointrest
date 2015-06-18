package com.pointrestapp.pointrest;

public final class Constants {
	
	public static final String POINTREST_PREFERENCES = "pointrest_prefs";
	public static class SharedPreferences {
		public static final String RAGGIO = "raggio_shared_pref";
		public static final String LANG = "lang";
		public static final String LAT = "lat";
	}
	
	public static final String CATEGORY_TYPE = "tabtype";
	public static final String BASE_FENCE_ID = "base_fence_id";
	public static class TabType {
		public static int POI;
		public static final int TUTTO = -1;
		public static int AC;
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
	public static int NOTIFICATION_ID = 0;
	
	public static final String BASE_URL = "http://www.pointerest.somee.com/api/";
}
