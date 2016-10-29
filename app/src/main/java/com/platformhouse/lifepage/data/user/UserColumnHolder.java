package com.platformhouse.lifepage.data.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This Class is responsible on holding the user data
 */
public class UserColumnHolder implements Parcelable {
    private int user_id;
    private String password;

    public UserColumnHolder(int user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    // Parcelling part
    public UserColumnHolder(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        this.user_id = Integer.parseInt(data[0]);
        this.password = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{Integer.toString(this.user_id),
                this.password});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserColumnHolder createFromParcel(Parcel in) {
            return new UserColumnHolder(in);
        }

        public UserColumnHolder[] newArray(int size) {
            return new UserColumnHolder[size];
        }
    };

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
