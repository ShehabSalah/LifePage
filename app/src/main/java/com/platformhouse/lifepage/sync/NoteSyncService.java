package com.platformhouse.lifepage.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * Created by Shehab Salah on 10/4/16.
 */
public class NoteSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NoteSyncAdapter mMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (mMoviesSyncAdapter == null) {
                mMoviesSyncAdapter = new NoteSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
