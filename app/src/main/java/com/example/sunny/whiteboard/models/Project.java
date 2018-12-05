package com.example.sunny.whiteboard.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Project implements Parcelable {
    private String className;
    private String id;
    private Integer imageResource;
    private String name;
    private String description;
    private boolean approved;
    private ArrayList<String> students;
    private ArrayList<String> instructors;

    public Project(String className, String projectID, int imageResource, String name, String description, boolean approved, ArrayList<String> students, ArrayList<String> instructors) {
        this.className = className;
        this.id = projectID;
        this.imageResource = imageResource;
        this.name = name;
        this.description = description;
        this.approved = approved;
        this.students = students;
        this.instructors = instructors;
    }

    public Project() {
    }

    protected Project(Parcel in) {
        className = in.readString();
        id = in.readString();
        imageResource = in.readInt();
        name = in.readString();
        description = in.readString();
        approved = (in.readByte() != 0);
        students = in.createStringArrayList();
        instructors = in.createStringArrayList();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);
        dest.writeString(id);
        dest.writeInt(imageResource);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (approved ? 1 : 0));;
        dest.writeStringList(students);
        dest.writeStringList(instructors);
    }

    // converts a list of document snapshots to a list of project objects
    public static ArrayList<Project> convertFirebaseProjects(List<DocumentSnapshot> projectList) {
        ArrayList<Project> projects = new ArrayList<>();
        for (DocumentSnapshot currProject : projectList) {
            projects.add(
                    new Project(
                    currProject.getString("className"),
                    currProject.getString("id"),
                    0,
                    currProject.getString("name"),
                    currProject.getString("description"),
                            currProject.getBoolean("approved"),
                    (ArrayList<String>) currProject.get("students"),
                    (ArrayList<String>) currProject.get("instructors")
                    ));
        }
        return projects;
    }

    // get methods
    public String getClassName() { return className; }

    public String getID() { return id; }

    public int getImageResource() { return imageResource; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean getApproved() { return approved; }

    public void setApproved(boolean state) { this.approved = state; }

    public ArrayList<String> getStudents() { return students; }

    public ArrayList<String> getInstructors() { return instructors; }
}
