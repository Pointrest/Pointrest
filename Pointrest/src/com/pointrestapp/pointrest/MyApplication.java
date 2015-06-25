package com.pointrestapp.pointrest;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MyApplication extends Application  implements
ResultCallback<Status> {

	@Override
	public void onResult(Status arg0) {
		// TODO Auto-generated method stub
		
	}
	public void onCreate() {
	    super.onCreate();
	    Stetho.initialize(
	      Stetho.newInitializerBuilder(this)
	        .enableDumpapp(
	            Stetho.defaultDumperPluginsProvider(this))
	        .enableWebKitInspector(
	            Stetho.defaultInspectorModulesProvider(this))
	        .build());
	}
}	
