package com.platformhouse.lifepage.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.platformhouse.lifepage.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * This Class Created to Fetch the user data From LifePageServer and Return his ID
 */
public abstract class SyncUser extends AsyncTask<String, Integer, Integer> {
    public static final String LOG_TAG = SyncUser.class.getSimpleName();
    @Override
    protected Integer doInBackground(String... params) {
        return fetchData(params[0], params[1], params[2]);
    }

    @Override
    public abstract void onPreExecute();

    @Override
    protected abstract void onPostExecute(Integer id);

    @Override
    protected abstract void onProgressUpdate(Integer... values);

    public int fetchData(String url, String email, String password) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String listJsonStr = null;
        try {
            //API Name
            final String API_KEY_PARAM = "api_key";
            //Email Name
            final String EMAIL_PARAM = "email";
            //Password Name
            final String PASSWORD_PARAM = "password";
            //Build the uri
            Uri buildUri = Uri.parse(Constants.BASE_LINK + url + ".php?").buildUpon()
                    .appendQueryParameter(EMAIL_PARAM, email)
                    .appendQueryParameter(PASSWORD_PARAM, password)
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();
            Log.v(LOG_TAG,buildUri.toString());
            //Convert the uri to url
            URL _url = new URL(buildUri.toString());
            // Create the request to Life Page Server, and open the connection
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    //append each line from input stream into String Buffer
                    buffer.append(line + "\n");
                }
                if (buffer.length() != 0) {
                    //Convert the StringBuffer to String
                    listJsonStr = buffer.toString();
                    if (listJsonStr.contains("not exists")) {
                        Log.v(LOG_TAG,listJsonStr);
                        return -2;
                    } else if (listJsonStr.contains("exists")) {
                        return -1;
                    } else {
                        return getUserIdFromJson(listJsonStr);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUserIdFromJson(String JsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(JsonStr);
        return jsonObject.getInt("user_id");
    }
}