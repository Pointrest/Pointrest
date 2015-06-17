package com.pointrestapp.pointrest;

public final class Constants {
	
	public static final String POINTREST_PREFERENCES = "pointrest_prefs";
	public static class SharedPreferences {
		public static final String RAGGIO = "raggio_shared_pref";
		public static final String LANG = "lang";
		public static final String LAT = "lat";
	}
	
	public static final String CATEGORY_TYPE = "tabtype";
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
}
