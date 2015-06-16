package com.pointrestapp.pointrest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PuntiSyncService extends Service {
	
    private static PuntiSyncAdapter sSyncAdapter = null;

    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
    	
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new PuntiSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return sSyncAdapter.getSyncAdapterBinder();
    }
}
