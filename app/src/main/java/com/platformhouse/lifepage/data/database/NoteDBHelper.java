package com.platformhouse.lifepage.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.data.note.NoteColumns;

/*
 * This Class is responsible on creating the Database schema and it's Tables
 */
public class NoteDBHelper extends SQLiteOpenHelper {
    //DATABASE_VERSION is containing the version number of the current DB schema.
    //If the Database schema changed, the DATABASE_VERSION must change also.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "lifepage.db";

    public NoteDBHelper (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create user table
        final String CREATE_USER_TABLE = "CREATE TABLE " + UserColumns.TABLE_NAME + " ( " +
                UserColumns._ID +                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserColumns.COL_USER_ID +        " INTEGER NOT NULL, " +
                UserColumns.COL_PASSWORD +       " TEXT NOT NULL, " +
                "UNIQUE (" + UserColumns.COL_USER_ID + ") ON CONFLICT REPLACE);";

        final String CREATE_NOTE_TABLE = "CREATE TABLE " + NoteColumns.TABLE_NAME + " ( " +
                NoteColumns._ID +               " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteColumns.COL_NOTE_ID +       " INTEGER NULL, " +
                NoteColumns.COL_TITLE +         " TEXT NULL, " +
                NoteColumns.COL_BODY +          " TEXT NULL, " +
                NoteColumns.COL_IMAGE_PATH +    " TEXT NULL, " +
                NoteColumns.COL_DATE +          " TEXT NULL, " +
                NoteColumns.COL_TIME +          " TEXT NULL, " +
                NoteColumns.COL_SONG_PATH +     " TEXT NULL, " +
                NoteColumns.COL_CLOCK_LOGO +    " INTEGER NULL," +
                NoteColumns.COL_DATE_LOGO +     " INTEGER NULL," +
                "UNIQUE (" +NoteColumns.COL_NOTE_ID+") ON CONFLICT REPLACE); ";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop database tables DATABASE_VERSION number is changed
        db.execSQL("DROP TABLE IF EXISTS " + UserColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NoteColumns.TABLE_NAME);
        onCreate(db);
    }
}
