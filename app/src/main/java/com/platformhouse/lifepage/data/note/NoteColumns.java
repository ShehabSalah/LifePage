package com.platformhouse.lifepage.data.note;

import android.net.Uri;
import android.provider.BaseColumns;

import com.platformhouse.lifepage.util.Constants;

/**
 * This Class contain the ContentProvider URI's and Database Column names of the NOTE
 */
public class NoteColumns implements BaseColumns {
    public static final String TABLE_NAME = "note";
    //General Content URI
    public static final Uri CONTENT_URI = Constants.CONTENT_URI_BASE.buildUpon().appendPath(TABLE_NAME).build();

    //Table Columns
    public static final String COL_NOTE_ID      = "note_id";
    public static final String COL_TITLE        = "title";
    public static final String COL_BODY         = "body";
    public static final String COL_IMAGE_PATH   = "image_path";
    public static final String COL_DATE         = "date";
    public static final String COL_TIME         = "time";
    public static final String COL_SONG_PATH    = "song_path";
    public static final String COL_CLOCK_LOGO   = "clock_logo";
    public static final String COL_DATE_LOGO    ="date_logo";
}
