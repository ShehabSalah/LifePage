package com.platformhouse.lifepage.data.user;

import android.content.ContentValues;
import android.support.annotation.NonNull;

/**
 * This Class take the (User) data and convert it to ContentValues
 */
public class UserContentValues {
    public ContentValues contentValues_NewUser(int user_id, @NonNull String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns.COL_USER_ID,user_id);
        contentValues.put(UserColumns.COL_PASSWORD, password);
        return  contentValues;
    }
}
