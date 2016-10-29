package com.platformhouse.lifepage.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * Created by Shehab Salah on 10/4/16.
 */
public class NoteAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private NoteAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new NoteAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
