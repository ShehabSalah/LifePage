package com.platformhouse.lifepage.ui.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.services.AlarmReceiver;
import com.platformhouse.lifepage.ui.adapters.NoteAdapter;
import com.platformhouse.lifepage.util.Constants;
import com.platformhouse.lifepage.util.NoteObserver;

import java.util.ArrayList;

/*
 * Created by Shehab Salah on 9/20/16.
 */
public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = NoteFragment.class.getSimpleName();
    ListView note_list;
    NoteAdapter noteAdapter;
    ArrayList<Object> list;
    TextView empty_hint;
    AlertDialog alertDialog;
    NoteObserver mObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(Constants.NOTE_LOADER, null, this);
        mObserver = new NoteObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                getLoaderManager().restartLoader(Constants.NOTE_LOADER, null, NoteFragment.this);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(Constants.NOTE_LOADER, null, NoteFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG,"Enter on pause");
        Constants.state = note_list.onSaveInstanceState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.note_list, container, false);
        getActivity().setTitle(R.string.note_title);
        note_list = (ListView) mainView.findViewById(R.id.note_list);
        empty_hint = (TextView) mainView.findViewById(R.id.notes_empty);
        note_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showMyDialog(position);
                return true;
            }
        });
        note_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onItemSelected((NoteColumnHolder) list.get(position));
            }
        });
        return mainView;
    }

    private void UpdateAdapter(ArrayList<Object> list) {
        if (list.size() > 0) {
            note_list.setVisibility(View.VISIBLE);
            empty_hint.setVisibility(View.GONE);
            this.list = list;
        }else{
            note_list.setVisibility(View.GONE);
            empty_hint.setVisibility(View.VISIBLE);
        }
        noteAdapter = new NoteAdapter(this.list, getActivity());
        note_list.setAdapter(noteAdapter);
        if(Constants.state != null) {
            Log.d(LOG_TAG, "trying to restore listview state..");
            note_list.onRestoreInstanceState(Constants.state);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                NoteColumns.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Object> dbList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                NoteColumnHolder noteColumnHolder = new NoteColumnHolder(
                        cursor.getInt(cursor.getColumnIndex(NoteColumns._ID)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_BODY)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_IMAGE_PATH)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_DATE)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_TIME)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_SONG_PATH)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_CLOCK_LOGO)),
                        cursor.getString(cursor.getColumnIndex(NoteColumns.COL_DATE_LOGO))
                );
                Log.i(LOG_TAG, Integer.toString(noteColumnHolder.getNote_id()));
                dbList.add(noteColumnHolder);
            } while (cursor.moveToNext());
        }
        UpdateAdapter(dbList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void showMyDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message);
        builder.setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NoteColumnHolder noteColumnHolder = (NoteColumnHolder) list.get(position);
                getActivity().getContentResolver().delete(NoteColumns.CONTENT_URI,
                        NoteColumns._ID + " = ?",new String[]{Integer.toString(noteColumnHolder.getNote_id())});
                Intent my_intent = new Intent(getActivity(), AlarmReceiver.class);
                AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                        position, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pendingIntent);
                getLoaderManager().restartLoader(Constants.NOTE_LOADER, null, NoteFragment.this);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //do nothing
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
    public interface Callback{
        void onItemSelected(NoteColumnHolder noteColumnHolder);
    }
}