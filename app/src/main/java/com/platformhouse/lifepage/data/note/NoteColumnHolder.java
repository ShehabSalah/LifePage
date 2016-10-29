package com.platformhouse.lifepage.data.note;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This Class is responsible on holding the note data
 */
public class NoteColumnHolder implements Parcelable {
    private int note_id;
    private String title;
    private String body;
    private String image_path;
    private String date;
    private String time;
    private String song_path;
    private String alarm_clock_logo;
    private String alarm_date_logo;

    public NoteColumnHolder(int note_id, String title, String body, String image_path, String date,String time, String song_path,
                            String alarm_clock_logo, String alarm_date_logo) {
        this.note_id = note_id;
        this.title = title;
        this.body = body;
        this.image_path = image_path;
        this.date = date;
        this.time = time;
        this.song_path = song_path;
        this.alarm_clock_logo = alarm_clock_logo;
        this.alarm_date_logo = alarm_date_logo;
    }

    // Parcelling part
    public NoteColumnHolder(Parcel in){
        String[] data = new String[9];
        in.readStringArray(data);
        this.note_id    = Integer.parseInt(data[0]);
        this.title      = data[1];
        this.body       = data[2];
        this.image_path = data[3];
        this.date       = data[4];
        this.time       = data[5];
        this.song_path  = data[6];
        this.alarm_clock_logo = data[7];
        this.alarm_date_logo = data[8];

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {Integer.toString(this.note_id),
                this.title,
                this.body,
                this.image_path,
                this.date,
                this.time,
                this.song_path,
                this.alarm_clock_logo,
                this.alarm_date_logo
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NoteColumnHolder createFromParcel(Parcel in) {
            return new NoteColumnHolder(in);
        }
        public NoteColumnHolder[] newArray(int size) {
            return new NoteColumnHolder[size];
        }
    };

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSong_path() {
        return song_path;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    public String getAlarm_clock_logo() {
        return alarm_clock_logo;
    }

    public void setAlarm_clock_logo(String alarm_clock_logo) {
        this.alarm_clock_logo = alarm_clock_logo;
    }

    public String getAlarm_date_logo() {
        return alarm_date_logo;
    }

    public void setAlarm_date_logo(String alarm_date_logo) {
        this.alarm_date_logo = alarm_date_logo;
    }
}
