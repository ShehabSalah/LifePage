package com.platformhouse.lifepage.sync;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.platformhouse.lifepage.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/*
 * Created by Shehab Salah on 10/5/16.
 */
public abstract class GetUserNotes extends AsyncTask<Integer,Integer,ArrayList<NoteColumnHolder>> {
    public static final String LOG_TAG = GetUserNotes.class.getSimpleName();

    @Override
    protected ArrayList<NoteColumnHolder> doInBackground(Integer... params) {
        int id = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String listJsonStr = null;
        try {
            //API Name
            final String API_KEY_PARAM = "api_key";
            //Email Name
            final String ID_PARAM = "user_id";
            //Build the uri
            Uri buildUri = Uri.parse(Constants.BASE_LINK + Constants.USER_NOTES_LINK).buildUpon()
                    .appendQueryParameter(ID_PARAM, Integer.toString(id))
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
                    ArrayList<NoteColumnHolder> noteList = getNotesFromJsonString(listJsonStr);
                    noteList = wrapNoteList(noteList);
                    return noteList;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected abstract void onProgressUpdate(Integer... values);

    @Override
    protected abstract void onPostExecute(ArrayList<NoteColumnHolder> list);

    private ArrayList<NoteColumnHolder> getNotesFromJsonString(String jsonStr) throws JSONException{
        //Convert the JsonString to JsonObject
        JSONObject jsonObject = new JSONObject(jsonStr);
        //Convert the JsonObject to JsonArray
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        //Get object of Gson() class
        Gson gson = new Gson();
        //ArrayList of Objects to hold the parsing json
        ArrayList<NoteColumnHolder> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            NoteColumnHolder noteColumnHolder =
                    gson.fromJson(jsonObject1.toString(),NoteColumnHolder.class);
            list.add(noteColumnHolder);
        }
        return list;
    }
    private ArrayList<NoteColumnHolder> wrapNoteList(ArrayList<NoteColumnHolder> noteList){
        for (int i = 0; i < noteList.size(); i++){
            if (noteList.get(i).getBody().equals("null")){
                noteList.get(i).setBody(null);
            }
            if (noteList.get(i).getImage_path().contains("null")){
                noteList.get(i).setImage_path(null);
            }else{
                Log.v(LOG_TAG,noteList.get(i).getImage_path());
            }
            if (noteList.get(i).getSong_path().contains("null")){
                noteList.get(i).setSong_path(null);
            }
            if (noteList.get(i).getDate().contains("null")){
                noteList.get(i).setDate(null);
                noteList.get(i).setAlarm_date_logo("1");
            }else{
                noteList.get(i).setAlarm_date_logo(Integer.toString(R.mipmap.calendar));
            }
            if (noteList.get(i).getTime().contains("null")){
                noteList.get(i).setTime(null);
                noteList.get(i).setAlarm_clock_logo("1");
            }else{
                noteList.get(i).setAlarm_clock_logo(Integer.toString(R.mipmap.alarm_clock));
            }
        }
        return noteList;
    }
}
