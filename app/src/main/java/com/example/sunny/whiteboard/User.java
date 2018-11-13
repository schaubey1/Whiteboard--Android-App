package com.example.sunny.whiteboard;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid;
    private String name;
    private String email;
    private String accountType;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(email);
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

    public User(String uid, String name, String email, String accountType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.accountType = accountType;
    }

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        accountType = in.readString();
    }

    public String getUID() { return uid; }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountType() { return accountType; }
}
