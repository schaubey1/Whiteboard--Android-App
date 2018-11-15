package com.example.sunny.whiteboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String uid;
    private String name;
    private String email;
    private String accountType;

    public User(String uid, String name, String email, String accountType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.accountType = accountType;
    }

    public String getUID() { return uid; }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountType() { return accountType; }

    // write user to SharedPreferences
    public static void writeUser(Context context, User user) {
        SharedPreferences sh = context.getSharedPreferences("Whiteboard", Context.MODE_PRIVATE);
        SharedPreferences.Editor shEdit = sh.edit();
        shEdit.putString("uid", user.getUID());
        shEdit.putString("name", user.getName());
        shEdit.putString("email", user.getEmail());
        shEdit.putString("accountType", user.getAccountType());
        shEdit.commit();

    }

    // retrieve user from SharedPreferences
    public static User getUser(Context context) {
        SharedPreferences sh = context.getSharedPreferences("Whiteboard", Context.MODE_PRIVATE);
        String uid = sh.getString("uid", "");
        String name = sh.getString("name", "");
        String email = sh.getString("email", "");
        String accountType = sh.getString("accountType", "");
        return new User(uid, name, email, accountType);
    }
}
