package com.platformhouse.lifepage.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.util.Constants;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by Shehab Salah on 10/4/16.
 */
public class NoteSyncAdapter extends AbstractThreadedSyncAdapter {
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180  = 3 hours
    //3 hours * 4 = 12 hours
    public static final int SYNC_INTERVAL = (60 * 180) * 4;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private String jsonString;
    private int user_id;
    public NoteSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v("NoteSyncAdapter", "success!");
        uploadUserNotes();
    }
    private void uploadUserNotes(){
        user_id = getUserId();
        int count = 0;
        Cursor cursor = getContext().getContentResolver().query(NoteColumns.CONTENT_URI,null,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            jsonString = "{\"results\":[";
            do {
                jsonString += "{\"title\":" + "\"" + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_TITLE)) + "\",";
                jsonString += "\"body\":" + "\"" + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_BODY)) + "\",";
                jsonString += "\"imagePath\":" + "\"" + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_IMAGE_PATH)) + "\",";
                jsonString += "\"dateTime\":" + "\"" + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_DATE)) + " " + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_TIME)) + "\",";
                jsonString += "\"localSongPath\":" + "\"" + cursor.getString(cursor.getColumnIndex(NoteColumns.COL_SONG_PATH)) + "\"}";
                cursor.moveToNext();
                if (count < cursor.getCount() - 1) {
                    jsonString += ",";
                }
                count++;
            } while (!cursor.isAfterLast());
            cursor.close();
            jsonString += "]}";
            Log.v("JSONSTRING", jsonString);
            StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_LINK + "sync_notes.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Success"))
                                Log.v("upload","Sync Success");
                            Log.i("Entity Response  : ", response);
                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error Response", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("user_id", Integer.toString(user_id));
                    parameters.put("jsonString",jsonString);
                    parameters.put("api_key",Constants.API_KEY);
                    return parameters;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            request.setShouldCache(false);
            requestQueue.add(request);
        }
    }

    private int getUserId(){
        Cursor cursor = getContext().getContentResolver().query(UserColumns.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(UserColumns.COL_USER_ID));
            cursor.close();
            return id;
        }
        return 0;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NoteSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}