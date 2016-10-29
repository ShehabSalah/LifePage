package com.platformhouse.lifepage.util;

import android.database.ContentObserver;
import android.net.Uri;

/*
 * Created by Shehab Salah on 9/24/16.
 */
public abstract class NoteObserver extends ContentObserver {
    public NoteObserver(android.os.Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public abstract void onChange(boolean selfChange, Uri uri);
}