package com.platformhouse.lifepage.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class AlarmActivity extends Activity{
    private MediaPlayer mMediaPlayer;
    private final String LOG_TAG = AlarmActivity.class.getSimpleName();
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_receiver);
        cancel = (Button)findViewById(R.id.alarm_cancel_button);
        TextView alarm_note_title = (TextView)findViewById(R.id.alarm_note_title);
        ImageView alarm_note_image = (ImageView)findViewById(R.id.alarm_note_image);
        TextView alarm_note_body = (TextView)findViewById(R.id.alarm_note_body);
        RelativeLayout target = (RelativeLayout)findViewById(R.id.target);
        RelativeLayout path = (RelativeLayout)findViewById(R.id.path);
        target.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(LOG_TAG, "Drag event started");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(LOG_TAG, "Drag event entered into " + v.toString());
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(LOG_TAG, "Drag event exited from " + v.toString());
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.d(LOG_TAG, "Dropped");
                        View view = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) view.getParent();
                        owner.removeView(view);
                        RelativeLayout container = (RelativeLayout) v;
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                        mMediaPlayer.stop();
                        finish();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        cancel.setVisibility(View.VISIBLE);
                        Log.d(LOG_TAG, "Drag ended");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        cancel.setTag(Constants.CANCEL_TAG);



        AlphaAnimation targetAnimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        targetAnimation.setDuration(1000); // duration - half a second
        targetAnimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        targetAnimation.setRepeatCount(1000); // Repeat animation infinitely
        targetAnimation.setRepeatMode(Animation.REVERSE);

        AlphaAnimation pathAnimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        pathAnimation.setDuration(400); // duration - half a second
        pathAnimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        pathAnimation.setRepeatCount(1000); // Repeat animation infinitely
        pathAnimation.setRepeatMode(Animation.REVERSE);

        target.setAnimation(targetAnimation);
        path.setAnimation(pathAnimation);


        Intent intent = getIntent();
        int id = intent.getIntExtra(Constants.ALARM_TAG,0);
        Cursor cursor = getContentResolver().query(NoteColumns.CONTENT_URI,
                null,
                NoteColumns._ID + " = ?",
                new String[]{Integer.toString(id)},
                null);
        if (cursor != null && cursor.moveToFirst()){
            String uriString = cursor.getString(cursor.getColumnIndex(NoteColumns.COL_SONG_PATH));
            alarm_note_title.setText(cursor.getString(cursor.getColumnIndex(NoteColumns.COL_TITLE)));
            Picasso.with(AlarmActivity.this)
                    .load(cursor.getString(cursor.getColumnIndex(NoteColumns.COL_IMAGE_PATH)))
                    .resize(1380,850)
                    .centerCrop()
                    .error(R.mipmap.image_error)
                    .into(alarm_note_image);
            alarm_note_body.setText(cursor.getString(cursor.getColumnIndex(NoteColumns.COL_BODY)));
            Uri uri = null;
            if(uriString != null){
                uri = Uri.parse(uriString);
            }else{
                uri = Uri.parse(Constants.DEFAULT_RINGTONE);
            }
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(this, uri);
                final AudioManager audioManager = (AudioManager)
                        this.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.prepare();
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                }
            } catch (IOException e) {
                System.out.println("OOPS");
            }
            cursor.close();
        }
        cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(null, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            }
        });
    }
}
