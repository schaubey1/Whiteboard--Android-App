package com.example.sunny.whiteboard;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import com.example.sunny.whiteboard.ProjModel;

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved=null;
    ArrayList<ProjModel> projects=new ArrayList<>();

    /*
     PASS DATABASE REFRENCE
     */
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }

    //WRITE IF NOT NULL
    public Boolean save(ProjModel project)
    {
        if(project==null)
        {
            saved=false;

        }else
        {
            try
            {
                db.child("projects").push().setValue(project);
                saved=true;

            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }

        return saved;
    }

    //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot)
    {
        projects.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            ProjModel project=ds.getValue(ProjModel.class);
            projects.add(project);
        }
    }

    //READ BY HOOKING ONTO DATABASE OPERATION CALLBACKS
    public ArrayList<ProjModel> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return projects;
    }

}