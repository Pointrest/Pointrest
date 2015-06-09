package com.pointrestapp.pointrest;

import java.util.WeakHashMap;

import android.app.Application;
import android.app.Fragment;

public class MyApplication extends Application {
	
	public WeakHashMap<Integer, Fragment> mAdaptedFragments = new WeakHashMap<Integer, Fragment>();
	
}
