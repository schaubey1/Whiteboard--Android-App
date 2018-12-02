package com.example.sunny.whiteboard.models;

import java.util.ArrayList;

public class Class {

    private String className;
    private String code;
    private ArrayList<String> instructors;
    private ArrayList<String> students;

    public Class() {}

    public Class(String className, String code, ArrayList<String> instructors, ArrayList<String> students) {
        this.className = className;
        this.code = code;
        this.instructors = instructors;
        this.students = students;
    }

    public String getClassName() {
        return className;
    }

    public String getCode() {
        return code;
    }

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
}