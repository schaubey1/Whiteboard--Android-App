package com.example.sunny.whiteboard.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Class implements Parcelable {

    private String className;
    private String code;
    private String id;
    private ArrayList<String> instructors;
    private ArrayList<String> students;

    public Class() {}

    public Class(String className, String code, String id, ArrayList<String> instructors, ArrayList<String> students) {
        this.className = className;
        this.code = code;
        this.id = id;
        this.instructors = instructors;
        this.students = students;
    }

    protected Class(Parcel in) {
        className = in.readString();
        code = in.readString();
        id = in.readString();
        instructors = in.createStringArrayList();
        students = in.createStringArrayList();
    }

    public static final Creator<Class> CREATOR = new Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };

    public String getClassName() {
        return className;
    }

    public String getCode() {
        return code;
    }

    public String getID() { return id; }

    public ArrayList<String> getInstructors() {
        return instructors;
    }

    public ArrayList<String> getStudents() {
        return students;
    }

    // generates a random code for class enrollment(represented as time)
    public static String generateCode() {
        String time = String.valueOf(System.currentTimeMillis());
        return time.substring(0, Math.min(time.length(), 15));
    }

    // converts a list of document snapshots to a list of class objects
    public static ArrayList<Class> convertFirebaseProjects(List<DocumentSnapshot> classList) {
        ArrayList<Class> classes = new ArrayList<>();
        for (DocumentSnapshot currClass : classList) {
            classes.add(
                    new Class(
                            currClass.getString("className"),
                            currClass.getString("code"),
                            currClass.getString("id"),
                            (ArrayList<String>) currClass.get("instructors"),
                            (ArrayList<String>) currClass.get("students")
                    ));
        }
        return classes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(className);
        parcel.writeString(code);
        parcel.writeString(id);
        parcel.writeStringList(instructors);
        parcel.writeStringList(students);
    }
}