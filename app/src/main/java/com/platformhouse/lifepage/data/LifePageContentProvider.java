package com.platformhouse.lifepage.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.platformhouse.lifepage.data.database.NoteDBHelper;
import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.util.Constants;

/**
 * Created by Shehab Salah on 9/23/16.
 * This class is responsible on separating the underline Data Storage from the Application.
 * This Class also responsible on making the underline Data Storage of the Application shared
 * among all other applications
 */
public class LifePageContentProvider extends ContentProvider {
    private final String LOG_TAG = LifePageContentProvider.class.getSimpleName();
    private SQLiteDatabase db;
    //USER Uri Number
    private static final int TYPE_USER = 0;
    //NOTE Uri Number
    private static final int TYPE_NOTE = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    //Match each previous number with the corresponding URI
    static {
        URI_MATCHER.addURI(Constants.AUTHORITY, UserColumns.TABLE_NAME, TYPE_USER);
        URI_MATCHER.addURI(Constants.AUTHORITY, NoteColumns.TABLE_NAME, TYPE_NOTE);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        NoteDBHelper dbHelper = new NoteDBHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Switch the coming Uri with its corresponding number
        int match = URI_MATCHER.match(uri);
        //Return the cursor result based on the uri number
        switch (match) {
            case TYPE_USER:
                return db.query(
                        UserColumns.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
            case TYPE_NOTE:
                return db.query(
                        NoteColumns.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        NoteColumns._ID+" DESC"
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case TYPE_USER:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + UserColumns.TABLE_NAME;
            case TYPE_NOTE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + NoteColumns.TABLE_NAME;
        }
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        //Switch the coming Uri with its corresponding number
        int match = URI_MATCHER.match(uri);
        //Database row id
        long rowID = 0;
        //This Uri is changed based on the inserted Database Table
        Uri _uri = null;
        switch (match){
            case TYPE_USER:
                rowID = db.insert(UserColumns.TABLE_NAME, "", values);
                _uri = UserColumns.CONTENT_URI;
                break;
            case TYPE_NOTE:
                rowID = db.insert(NoteColumns.TABLE_NAME,"",values);
                _uri = NoteColumns.CONTENT_URI;
                break;
        }
        if (rowID > 0) {
            Log.v(LOG_TAG,Long.toString(rowID));
            Uri _uri2 = ContentUris.withAppendedId(_uri, rowID);
            getContext().getContentResolver().notifyChange(NoteColumns.CONTENT_URI, null, false);
            return _uri;
        }else{
            return  null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        int count = 0;
        switch (match){
            case TYPE_USER:
                count = db.delete(UserColumns.TABLE_NAME, selection, selectionArgs);
                break;
            case TYPE_NOTE:
                count = db.delete(NoteColumns.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(NoteColumns.CONTENT_URI, null, false);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        int count = 0;
        switch (match){
            case TYPE_USER:
                count = db.update(UserColumns.TABLE_NAME,values, selection, selectionArgs);
                break;
            case TYPE_NOTE:
                count = db.update(NoteColumns.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
