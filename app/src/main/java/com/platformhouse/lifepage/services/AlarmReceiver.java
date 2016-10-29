package com.platformhouse.lifepage.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.platformhouse.lifepage.ui.activities.AlarmActivity;
import com.platformhouse.lifepage.util.Constants;

/*
 * Created by Shehab salah on 9/25/16.
 */
public class AlarmReceiver extends BroadcastReceiver{
    private final String LOG_TAG = AlarmReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG,"I'am in the receiver");
        int id = intent.getIntExtra(Constants.ALARM_TAG,0);
        Log.v(LOG_TAG,"and the note id is "+ Integer.toString(id));
        Intent newIntent = new Intent(context, AlarmActivity.class);
        newIntent.putExtra(Constants.ALARM_TAG,id);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);

    }
}
