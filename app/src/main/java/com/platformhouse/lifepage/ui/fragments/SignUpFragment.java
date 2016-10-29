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
import com.platformhouse.lifepage.data.user.UserColumnHolder;
import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.data.user.UserContentValues;
import com.platformhouse.lifepage.ui.activities.NoteActivity;
import com.platformhouse.lifepage.util.Constants;

/*
 * Created by Shehab Salah on 9/19/16.
 */
public class SignUpFragment extends android.support.v4.app.Fragment {
    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();
    EditText pass;
    EditText email;
    EditText repass;
    ProgressBar progressBar;
    Button sign_up;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        email = (EditText) rootView.findViewById(R.id.email);
        email.setHint(R.string.signup_email_hint);

        pass = (EditText) rootView.findViewById(R.id.password);
        pass.setHint(R.string.signup_passowrd);

        repass = (EditText) rootView.findViewById(R.id.re_password);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        sign_up = (Button) rootView.findViewById(R.id.login_signup_button);
        sign_up.setText(R.string.signup_button);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide the keyboard
                Constants.hideKeyboardFrom(getActivity(),v);
                //Assign the text views values to variables
                String mail = email.getText().toString();
                String password = pass.getText().toString();
                String repassword = repass.getText().toString();
                //Check the email validation
                if (!mail.equals("") && mail.contains("@") && mail.contains(".com") && mail.length() > 8) {
                    //Check the password validation
                    if (!password.equals("") && password.length() > 5) {
                        //Check the password matcher
                        if (password.equals(repassword)) {
                            //Check if there is an internet connection or not!
                            if (Constants.isConnected(getActivity()))
                                sign_up(mail, password);
                            else
                                Constants.shortMessage(getActivity(), getActivity().getString(R.string.no_connection));
                        } else {
                            Constants.longMessage(getActivity(), getActivity().getString(R.string.password_matcher));
                        }
                    } else {
                        Constants.longMessage(getActivity(), getActivity().getString(R.string.password_validation_message));
                    }

                } else {
                    Constants.longMessage(getActivity(), getActivity().getString(R.string.email_validation_message));
                }
            }
        });
        return rootView;
    }
    //This method run the background thread to register the user data on the LifePage Server
    private void sign_up(String mail, String password) {
        SyncUser syncUser = new SyncUser() {
            @Override
            public void onPreExecute() {
                //Hide views and show the progress bar
                pass.setVisibility(View.INVISIBLE);
                email.setVisibility(View.INVISIBLE);
                repass.setVisibility(View.INVISIBLE);
                sign_up.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Integer id) {
                //Show the views and hide the progress bar
                pass.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                repass.setVisibility(View.VISIBLE);
                sign_up.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                //if the registration success
                if (id > 0) {
                    goToNoteActivity(id);
                } else if (id == -1) {
                    Constants.longMessage(getActivity(), getActivity().getString(R.string.user_exists));
                } else {
                    Log.v(LOG_TAG, "Unknown Error");
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                //Make the progress bar loops
                progressBar.setProgress(values[0]);
            }
        };
        //Execute the AsyncTask
        syncUser.execute(Constants.SIGNUP_PROCESS, mail, password);
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