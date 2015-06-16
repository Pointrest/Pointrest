package com.pointrestapp.pointrest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PuntiAuthenticatorService extends Service {

    private PuntiAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new PuntiAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
