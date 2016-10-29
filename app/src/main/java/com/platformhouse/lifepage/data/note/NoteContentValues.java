package com.platformhouse.lifepage.data.note;

import android.content.ContentValues;
import android.support.annotation.Nullable;

/**
 * This Class take the (Note) data and convert it to ContentValues
 */
public class NoteContentValues {
    public ContentValues contentValues_NewNote(@Nullable String title, @Nullable String body,
                                               @Nullable String image_path, @Nullable String date,
                                               @Nullable String time, @Nullable String song_path,
                                               @Nullable String clock_logo, @Nullable String date_logo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteColumns.COL_TITLE, title);
        contentValues.put(NoteColumns.COL_BODY,body);
        contentValues.put(NoteColumns.COL_IMAGE_PATH, image_path);
        contentValues.put(NoteColumns.COL_DATE,date);
        contentValues.put(NoteColumns.COL_TIME,time);
        contentValues.put(NoteColumns.COL_SONG_PATH, song_path);
        contentValues.put(NoteColumns.COL_CLOCK_LOGO,clock_logo);
        contentValues.put(NoteColumns.COL_DATE_LOGO, date_logo);
        return  contentValues;
    }
}