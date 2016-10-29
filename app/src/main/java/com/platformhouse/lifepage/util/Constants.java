package com.platformhouse.lifepage.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Calendar;

/*
 * Created by Shehab Salah on 9/20/16.
 */
public class Constants {
    //LifePage base Link
    public static final String BASE_LINK = "http://www.platformhouse.com/lifepage/";
    //Get user notes link
    public static final String USER_NOTES_LINK = "get_user_notes.php?";
    //LifePgae API KEY
    public static final String API_KEY = "0ee45dc23e205b3b3147467c2e583359";
    //Process Type: Login or Sign up
    public static final String LOGIN_PROCESS = "login";
    public static final String SIGNUP_PROCESS = "signup";
    //Default RingTone
    public static final String DEFAULT_RINGTONE = "content://settings/system/ringtone";
    //Providers
    public static final String AUTHORITY = "com.platformhouse.lifepage";
    public static final Uri CONTENT_URI_BASE = Uri.parse("content://" + AUTHORITY);
    //Intent To AlarmReceiver
    public static final String ALARM_TAG="note_id";
    //Loaders
    public static final int NOTE_LOADER = 0;
    //Permission constant to request reading from external storage
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    //Ringtone Request Constant
    public static final int REQUEST_RINGTONE = 1;
    //Image Request Constant
    public static final int REQUEST_IMAGE = 2;
    //List State
    public static Parcelable state = null;
    //New Note Activity (Intent Name)
    public static final String NewIntentName = "noteColumnHolder";
    //Cancel Button TAG in AlarmActivity
    public static final String CANCEL_TAG = "cancel alarm";

    public static void longMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void shortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static String formatTime(int hourOfDay, int minute){
        String time_minute;
        String hour;
        String min;
            if(hourOfDay < 10)
                hour = "0" + Integer.toString(hourOfDay);
            else if(hourOfDay > 12){
                if (hourOfDay - 12 < 10){
                    hour = "0" + Integer.toString(hourOfDay - 12);
                }else hour = Integer.toString(hourOfDay - 12);
            }
            else {
                hour = Integer.toString(hourOfDay);
            }
            if(minute < 10)
                min = "0"+Integer.toString(minute);
            else min = Integer.toString(minute);

            if (hourOfDay >= 12){
                time_minute =  hour + ":" + min + " PM";
            }else
                time_minute = hour + ":" + min + " AM";

        return time_minute;
    }
    public static String ValidateDateTime(int hourOfDay, int minute, int year, int month, int day){
        String time_minute = null;
        String hour;
        String min;
        final Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        int y = c.get(Calendar.YEAR);
        int mo = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        if (y == year && mo == month && d == day){
            if (hourOfDay > h || ( h == hourOfDay && minute > m)){
                if(hourOfDay < 10)
                    hour = "0" + Integer.toString(hourOfDay);
                else if(hourOfDay > 12){
                    if (hourOfDay - 12 < 10){
                        hour = "0" + Integer.toString(hourOfDay - 12);
                    }else hour = Integer.toString(hourOfDay - 12);
                }
                else {
                    hour = Integer.toString(hourOfDay);
                }
                if(minute < 10)
                    min = "0"+Integer.toString(minute);
                else min = Integer.toString(minute);

                if (hourOfDay >= 12){
                    time_minute =  hour + ":" + min + " PM";
                }else
                    time_minute = hour + ":" + min + " AM";

            }else{
                time_minute = "";
            }
        }else if(y < year || mo < month || d < day){
            if(hourOfDay < 10)
                hour = "0" + Integer.toString(hourOfDay);
            else if(hourOfDay > 12){
                if (hourOfDay - 12 < 10){
                    hour = "0" + Integer.toString(hourOfDay - 12);
                }else hour = Integer.toString(hourOfDay - 12);
            }
            else {
                hour = Integer.toString(hourOfDay);
            }
            if(minute < 10)
                min = "0"+Integer.toString(minute);
            else min = Integer.toString(minute);

            if (hourOfDay >= 12){
                time_minute =  hour + ":" + min + " PM";
            }else
                time_minute = hour + ":" + min + " AM";
        }else{
            time_minute = "";
        }

        return time_minute;

    }
}