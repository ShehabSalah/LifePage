package com.platformhouse.lifepage.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.user.UserColumnHolder;
import com.platformhouse.lifepage.data.user.UserColumns;
import com.platformhouse.lifepage.ui.fragments.LogInFragment;
import com.platformhouse.lifepage.ui.fragments.SignUpFragment;

/**
 * This Activity displays the Login and Sign Up fragments using (Android Default Tabbed Activity)
 * */

public class MainActivity extends AppCompatActivity {
    LogInFragment logInFragment;
    SignUpFragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new NoteDBHelper(MainActivity.this);
        setContentView(R.layout.activity_main);

        Cursor cursor = getContentResolver().query(UserColumns.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()){
            UserColumnHolder userColumnHolder = new UserColumnHolder(
                    cursor.getInt(cursor.getColumnIndex(UserColumns.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(UserColumns.COL_PASSWORD)));
            cursor.close();
            //Make intent to send the data to the note screen
            Intent intent = new Intent(MainActivity.this,NoteActivity.class);
            //Create Bundle
            Bundle args = new Bundle();
            //Put the user class as parcelable extra in bundle
            args.putParcelable("user_data",userColumnHolder);
            //Put the bundle as extra in the intent
            intent.putExtra("user",args);
            //Fire the Intent
            startActivity(intent);
            //If the user come back close the activity
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set up the TabLayout wich will host the (Page Title)
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);
    }

    //FragmentPager Method take a pageType and Return an object of Fragment based on this type
    public Fragment FragmentPager(int pageType){
        switch (pageType){
            case 1:
                if(logInFragment == null)
                    logInFragment = new LogInFragment();
                return logInFragment;
            case 2:
                if (signUpFragment == null)
                    signUpFragment = new SignUpFragment();
                return signUpFragment;

        }
        return null;
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return FragmentPager(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOG IN";
                case 1:
                    return "SIGN UP";
            }
            return null;
        }
    }
}
