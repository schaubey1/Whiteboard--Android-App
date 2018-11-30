package com.example.sunny.whiteboard.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Project implements Parcelable {
    private String id;
    private int imageResource;
    private String name;
    private String description;
    private ArrayList<String> members;

    public Project(String projectID, int imageResource, String name, String description, ArrayList<String> members) {
        this.id = projectID;
        this.imageResource = imageResource;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public Project() {

    }

    protected Project(Parcel in) {
        id = in.readString();
        imageResource = in.readInt();
        name = in.readString();
        description = in.readString();
        members = in.createStringArrayList();
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
        dest.writeString(id);
        dest.writeInt(imageResource);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(members);
    }

    // converts a list of document snapshots to a list of project objects
    public static ArrayList<Project> convertFirebaseProjects(List<DocumentSnapshot> projectList) {
        ArrayList<Project> projects = new ArrayList<>();
        for (DocumentSnapshot currProject : projectList) {
            projects.add(new Project(currProject.getString("id"),0,
                    currProject.getString("name"), currProject.getString("description"),
                    (ArrayList<String>) currProject.get("members")));
        }
        return projects;
    };

    // get methods
    public String getID() { return id; }

    public int getImageResource() { return imageResource; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public ArrayList<String> getMembers() { return members; }
}
