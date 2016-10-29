package com.platformhouse.lifepage.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.platformhouse.lifepage.sync.NoteSyncAdapter;
import com.platformhouse.lifepage.ui.fragments.NoteFragment;

/**
 * This Activity displays the user notes
 * */

public class NoteActivity extends AppCompatActivity implements NoteFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NewNoteActivity.getNewNoteIntent(NoteActivity.this,
                        new NoteColumnHolder(0,null,null,null,null,null,null,"1","1")));
            }
        });

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.note_container, new NoteFragment())
                .commit();

        NoteSyncAdapter.initializeSyncAdapter(getApplicationContext());
    }

    @Override
    public void onItemSelected(NoteColumnHolder noteColumnHolder) {
        startActivity(NewNoteActivity.getNewNoteIntent(this, noteColumnHolder));
    }
}
