package com.platformhouse.lifepage.ui.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.SyncUser;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.platformhouse.lifepage.data.note.NoteColumns;
import com.platformhouse.lifepage.data.note.NoteContentValues;
import com.platformhouse.lifepage.data.user.UserColumnHolder;
import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.data.user.UserContentValues;
import com.platformhouse.lifepage.sync.GetUserNotes;
import com.platformhouse.lifepage.ui.activities.NoteActivity;
import com.platformhouse.lifepage.util.Constants;

import java.util.ArrayList;

/*
 * Created by Shehab Salah on 9/19/16.
 */
public class LogInFragment extends android.support.v4.app.Fragment {
    private static final String LOG_TAG = LogInFragment.class.getSimpleName();
    EditText email;
    EditText pass;
    Button login_button;
    ProgressBar progressBar;
    int user_id = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        //hide re enter password from log in screen
        EditText re_password = (EditText) rootView.findViewById(R.id.re_password);
        re_password.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        email = (EditText) rootView.findViewById(R.id.email);
        pass = (EditText) rootView.findViewById(R.id.password);

        login_button = (Button) rootView.findViewById(R.id.login_signup_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.hideKeyboardFrom(getActivity(),v);
                String Email = email.getText().toString();
                String Password = pass.getText().toString();
                if (!Email.equals("") && Email.contains("@") && Email.contains(".com") && Email.length() > 8) {
                    if (!Password.equals("") && Password.length() > 5) {
                        if (Constants.isConnected(getActivity()))
                            login(Email, Password);
                        else
                            Constants.shortMessage(getActivity(), getActivity().getString(R.string.no_connection));
                    } else {
                        Constants.longMessage(getActivity(), getActivity().getString(R.string.password_validation_message));
                    }

                } else {
                    Constants.longMessage(getActivity(), getActivity().getString(R.string.email_validation_message));
                }
            }
        });
        Constants.hideKeyboardFrom(getActivity(),rootView);
        return rootView;
    }

    //This method run the background thread to register the user data on the LifePage Server
    private void login(String mail, final String password) {
        SyncUser syncUser = new SyncUser() {
            @Override
            public void onPreExecute() {
                //hide views
                email.setVisibility(View.INVISIBLE);
                pass.setVisibility(View.INVISIBLE);
                login_button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Integer id) {
                user_id = id;
                if (id > 0) {
                    GetUserNotes getUserNotes = new GetUserNotes() {
                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            progressBar.setProgress(values[0]);
                        }

                        @Override
                        protected void onPostExecute(ArrayList<NoteColumnHolder> list) {
                            insertNoteListIntoDatabase(list);
                            goToNoteActivity(user_id);
                        }
                    };
                    getUserNotes.execute(id);
                } else if (id == -2) {
                    Constants.longMessage(getActivity(), getActivity().getString(R.string.user_not_exists));
                } else {
                    Log.v(LOG_TAG, "Unknown Error "+ id);
                }
                email.setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                login_button.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBar.setProgress(values[0]);
            }
        };
        syncUser.execute(Constants.LOGIN_PROCESS, mail, password);
    }
    private void insertNoteListIntoDatabase(ArrayList<NoteColumnHolder> noteList){
        for (int i = 0; i < noteList.size(); i++){
            getActivity().getContentResolver().insert(NoteColumns.CONTENT_URI,
                    new NoteContentValues().contentValues_NewNote(
                            noteList.get(i).getTitle(),
                            noteList.get(i).getBody(),
                            noteList.get(i).getImage_path(),
                            noteList.get(i).getDate(),
                            noteList.get(i).getTime(),
                            noteList.get(i).getSong_path(),
                            noteList.get(i).getAlarm_clock_logo(),
                            noteList.get(i).getAlarm_date_logo()
                    ));
        }
    }
    public void goToNoteActivity(int id){
        //Get object form class UserContentValues
        UserContentValues userContentValues = new UserContentValues();
        //Convert the user data to contentValues
        ContentValues contentValues =
                userContentValues.contentValues_NewUser(id,pass.getText().toString());
        //Insert the user data in the database
        getActivity().getContentResolver().insert(UserColumns.CONTENT_URI,contentValues);
        //Put the user data in his class
        UserColumnHolder userColumnHolder = new UserColumnHolder(id,pass.getText().toString());
        //Make intent to send the data to the note screen
        Intent intent = new Intent(getActivity(),NoteActivity.class);
        //Create Bundle
        Bundle args = new Bundle();
        //Put the user class as parcelable extra in bundle
        args.putParcelable("user_data",userColumnHolder);
        //Put the bundle as extra in the intent
        intent.putExtra("user",args);
        //Fire the Intent
        startActivity(intent);
        //If the user come back close the activity
        getActivity().finish();
    }
}
