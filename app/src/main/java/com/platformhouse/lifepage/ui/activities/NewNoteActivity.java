package com.platformhouse.lifepage.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.data.note.NoteContentValues;
import com.platformhouse.lifepage.services.AlarmReceiver;
import com.platformhouse.lifepage.ui.fragments.DatePickerFragment;
import com.platformhouse.lifepage.ui.fragments.TimePickerFragment;
import com.platformhouse.lifepage.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * This Activity responsible on creating new note
 * */

public class NewNoteActivity extends AppCompatActivity {
    int itemID = 0;
    int hours = -1;
    int minutes = -1;
    int years = -1;
    int monthes = -1;
    int days = -1;
    String date;
    Uri chosenRingtone;
    Uri chosenImage;
    TextView display_time;
    TextView display_date;
    LinearLayout ringTone;
    EditText new_note_title;
    EditText new_note_content;
    ImageView new_note_image;
    final String LOG_TAG = NewNoteActivity.class.getSimpleName();
    NoteColumnHolder noteColumnHolder;
    Intent my_intent;
    AlarmManager am;
    PendingIntent pendingIntent;
    TextView ringTitle;
    boolean timePikerClicked;
    public static Intent getNewNoteIntent(Context context, NoteColumnHolder noteColumnHolder) {
        return new Intent(context, NewNoteActivity.class).putExtra(Constants.NewIntentName, noteColumnHolder);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        //Get Extra Intent
        Intent extras = getIntent();
        if (extras != null) {
            if (extras.hasExtra(Constants.NewIntentName)) {
                noteColumnHolder = extras.getParcelableExtra(Constants.NewIntentName);
            }
        }
        Log.v(LOG_TAG,noteColumnHolder.getTitle() + " ");
        LinearLayout time_picker = (LinearLayout)findViewById(R.id.time_picker);
        LinearLayout date_picker = (LinearLayout)findViewById(R.id.date_picker);
        Button cancel_button = (Button)findViewById(R.id.cancel_button);
        Button save_button = (Button)findViewById(R.id.save_button);
        ringTone = (LinearLayout) findViewById(R.id.ringTone);
        display_time = (TextView)findViewById(R.id.display_time);
        display_date = (TextView)findViewById(R.id.display_date);
        new_note_title = (EditText)findViewById(R.id.new_note_title);
        new_note_content = (EditText)findViewById(R.id.new_note_content);
        new_note_image = (ImageView)findViewById(R.id.new_note_image);
        my_intent = new Intent(this, AlarmReceiver.class);
        ringTitle = (TextView)findViewById(R.id.display_ringTitle);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);


        assert ringTone!=null;
        ringTone.setVisibility(View.GONE);

        //Set title based on Extra Intent value
        if (noteColumnHolder.getNote_id() == 0){
            setTitle(getString(R.string.new_note_page_title));
        }else{
            setTitle(getString(R.string.edit_note_title));
            new_note_title.setText(noteColumnHolder.getTitle());
            new_note_content.setText(noteColumnHolder.getBody());
            Picasso.with(this)
                    .load(noteColumnHolder.getImage_path())
                    .resize(1380,850)
                    .centerCrop()
                    .error(R.mipmap.image_error)
                    .into(new_note_image);
            display_date.setText(noteColumnHolder.getDate());
            display_time.setText(noteColumnHolder.getTime());
            if (noteColumnHolder.getSong_path() != null){
                ringTone.setVisibility(View.VISIBLE);
                chosenRingtone = Uri.parse(noteColumnHolder.getSong_path());
                Ringtone ringtone = RingtoneManager.getRingtone(this, chosenRingtone);
                String title = ringtone.getTitle(this);
                ringTitle.setText(title);
            }
            if (noteColumnHolder.getDate() != null){
                String dateFormatted = noteColumnHolder.getDate();
                String[] list = dateFormatted.split("-");
                years = Integer.parseInt(list[0]);
                monthes = Integer.parseInt(list[1]) - 1;
                days = Integer.parseInt(list[2]);
                date = Integer.toString(years)+"-"+Integer.toString(monthes + 1)+"-"+
                        Integer.toString(days);
                Log.v(LOG_TAG,years+" "+monthes+" "+days);
            }
        }

        assert time_picker!=null;
        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Display time
                        hours = hourOfDay;
                        minutes = minute;
                        display_time.setText(Constants.formatTime(hourOfDay,minute));
                        timePikerClicked = true;
                    }
                };
                newFragment.show(getSupportFragmentManager(), getString(R.string.pick_time));
            }
        });

        assert date_picker!=null;
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        //2016-09-27 14:15:00
                        Log.v(LOG_TAG,Integer.toString(month));
                        years = year;
                        monthes = month;
                        days = day;
                        date = Integer.toString(year)+"-"+Integer.toString(month+1)+"-"+
                                Integer.toString(day);
                        display_date.setText(date);
                    }
                };
                newFragment.show(getSupportFragmentManager(), getString(R.string.pick_time));
            }
        });


        assert cancel_button!=null;
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        assert save_button!=null;
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enyError = false;
                String noteTitle = new_note_title.getText().toString();
                Calendar cal = Calendar.getInstance();
                boolean setAlarm = false;
                int noteID = 0;
                if(!noteTitle.equals("") && noteTitle.length() > 2){
                    String noteBody = new_note_content.getText().toString();
                    noteColumnHolder.setTitle(noteTitle);
                    noteColumnHolder.setBody(!noteBody.equals("")?noteBody:null);
                    if (hours != -1 && minutes != -1){
                        if (years != -1 && monthes != -1 && days != -1){
                            enyError = false;
                            String time_min;
                            time_min = Constants.ValidateDateTime(hours,minutes,years,monthes,days);
                            if (!time_min.equals("")){
                                noteColumnHolder.setTime(time_min);
                                noteColumnHolder.setDate(date);
                                noteColumnHolder.setAlarm_clock_logo(Integer.toString(R.mipmap.alarm_clock));
                                noteColumnHolder.setAlarm_date_logo(Integer.toString(R.mipmap.calendar));
                                setAlarm = true;
                                cal.set(Calendar.HOUR_OF_DAY, hours);
                                cal.set(Calendar.MINUTE,minutes);
                                cal.set(Calendar.YEAR,years);
                                cal.set(Calendar.MONTH,monthes);
                                cal.set(Calendar.DAY_OF_MONTH,days);
                                cal.set(Calendar.SECOND,0);
                                enyError = false;
                                Log.v(LOG_TAG,years+" "+monthes+" "+days+" !time_min = --");
                            }else{
                                Constants.longMessage(NewNoteActivity.this,getString(R.string.note_time));
                                enyError = true;
                            }

                        }else{
                            Constants.longMessage(NewNoteActivity.this,getString(R.string.date_empty_validation));
                            enyError = true;
                        }
                    }else{
                       if (timePikerClicked){
                           enyError = true;
                           Constants.longMessage(NewNoteActivity.this,getString(R.string.time_empty_validation));
                       }
                    }
                    if (chosenRingtone != null){
                        noteColumnHolder.setSong_path(chosenRingtone.toString());
                    }
                    if (chosenImage != null){
                        noteColumnHolder.setImage_path(chosenImage.toString());
                    }
                    if (noteColumnHolder.getNote_id() == 0){
                        getContentResolver().insert(NoteColumns.CONTENT_URI,
                                new NoteContentValues().contentValues_NewNote(
                                        noteColumnHolder.getTitle(),
                                        noteColumnHolder.getBody(),
                                        noteColumnHolder.getImage_path(),
                                        noteColumnHolder.getDate(),
                                        noteColumnHolder.getTime(),
                                        noteColumnHolder.getSong_path(),
                                        noteColumnHolder.getAlarm_clock_logo(),
                                        noteColumnHolder.getAlarm_date_logo()
                                ));
                        Cursor cursor = getContentResolver().query(NoteColumns.CONTENT_URI,null,null,null,null);
                        if(cursor != null && cursor.moveToFirst()){
                            noteID = cursor.getInt(cursor.getColumnIndex(NoteColumns._ID));
                            cursor.close();
                        }
                    }else{
                        getContentResolver().update(NoteColumns.CONTENT_URI,
                                new NoteContentValues().contentValues_NewNote(
                                        noteColumnHolder.getTitle(),
                                        noteColumnHolder.getBody(),
                                        noteColumnHolder.getImage_path(),
                                        noteColumnHolder.getDate(),
                                        noteColumnHolder.getTime(),
                                        noteColumnHolder.getSong_path(),
                                        noteColumnHolder.getAlarm_clock_logo(),
                                        noteColumnHolder.getAlarm_date_logo()
                                ),NoteColumns._ID + " = ?",new String[]{Integer.toString(noteColumnHolder.getNote_id())});
                        noteID = noteColumnHolder.getNote_id();
                    }
                    if(noteID != 0){
                        if (setAlarm){
                            Log.v(LOG_TAG,"Alarm will set, AND THE NOTE ID IS " +  Integer.toString(noteID));
                            my_intent.putExtra(Constants.ALARM_TAG,noteID);
                            //Create a new PendingIntent and add it to the AlarmManager
                            pendingIntent = PendingIntent.getBroadcast(NewNoteActivity.this,
                                    noteID, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                        }
                        if (!enyError)
                            finish();
                    }
                }else{
                    Constants.longMessage(NewNoteActivity.this,getString(R.string.note_title_validation));
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(NewNoteActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            switchMenuItem(id);
        }else{
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewNoteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Do nothing...
            } else {
                itemID = id;
                ActivityCompat.requestPermissions(NewNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        return false;
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_RINGTONE)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                if (hours != -1 && minutes != -1 && years != -1 && monthes != -1 && days != -1){
                    this.chosenRingtone = uri;
                    Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                    String title = ringtone.getTitle(this);
                    ringTone.setVisibility(View.VISIBLE);
                    assert ringTitle!=null;
                    ringTitle.setText(title);
                    Log.v(LOG_TAG,chosenRingtone.toString());
                }else{
                    Constants.longMessage(NewNoteActivity.this,"Cannot set alarm ringtone, Please pick a time and date first.");
                }
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_IMAGE && intent != null)
        {
            Uri pickedImage = intent.getData();

            if (pickedImage != null)
            {
                chosenImage = pickedImage;
                Picasso.with(NewNoteActivity.this)
                        .load(pickedImage)
                        .resize(1380,850)
                        .centerCrop()
                        .error(R.mipmap.image_error)
                        .into(new_note_image);
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switchMenuItem(itemID);

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void switchMenuItem(int id){
        switch (id){
            case R.id.action_ringTone:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                this.startActivityForResult(intent, Constants.REQUEST_RINGTONE);
                break;
            case R.id.action_image:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.REQUEST_IMAGE);
                break;
        }
    }
}