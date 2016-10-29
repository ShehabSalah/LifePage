package com.platformhouse.lifepage.data.user;

import android.net.Uri;
import android.provider.BaseColumns;

import com.platformhouse.lifepage.util.Constants;

/**
 * This Class contain the ContentProvider URI's and Database Column names of the User
 */
public class UserColumns implements BaseColumns {
    public static final String TABLE_NAME = "user";
    //General Content URI
    public static final Uri CONTENT_URI = Constants.CONTENT_URI_BASE.buildUpon().appendPath(TABLE_NAME).build();

    //Table Columns
    public static final String COL_USER_ID      = "user_id";
    public static final String COL_PASSWORD     = "password";
}
