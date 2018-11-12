package com.example.sunny.whiteboard;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid;
    private String username;
    private String accountType;

    public User(String uid, String username, String accountType) {
        this.uid = uid;
        this.username = username;
        this.accountType = accountType;
    }

    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        accountType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(accountType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUID() { return uid; }

    public String getUsername() {
        return username;
    }

    public String getAccountType() { return accountType; }
}
