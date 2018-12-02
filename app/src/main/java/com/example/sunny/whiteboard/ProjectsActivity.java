package com.example.sunny.whiteboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sunny.whiteboard.adapters.ProjectRecyclerViewAdapter;
import com.example.sunny.whiteboard.models.ProjModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.sunny.whiteboard.adapters.ProjectRecyclerViewAdapter;
import com.example.sunny.whiteboard.models.ProjModel;
import java.util.ArrayList;
import java.util.List;

public class ProjectsActivity extends AppCompatActivity {

    private static final String TAG = "ProjectsActivity";

    private RecyclerView recyclerView;
    private ProjectRecyclerViewAdapter mAdapter;

    private FirebaseFirestore firestoreDB;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvNoteList);
        firestoreDB = FirebaseFirestore.getInstance();

        loadNotesList();

        firestoreListener = firestoreDB.collection("projects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }

                        List<ProjModel> projectsList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            ProjModel project = doc.toObject(ProjModel.class);
                            project.setId(doc.getId());
                            projectsList.add(project);
                        }

                        mAdapter = new ProjectRecyclerViewAdapter(projectsList, getApplicationContext(), firestoreDB);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firestoreListener.remove();
    }

    private void loadNotesList() {
        firestoreDB.collection("projects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ProjModel> projectsList = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult()) {
                                ProjModel project = doc.toObject(ProjModel.class);
                                project.setId(doc.getId());
                                projectsList.add(project);
                            }

                            mAdapter = new ProjectRecyclerViewAdapter(projectsList, getApplicationContext(), firestoreDB);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            if (item.getItemId() == R.id.addNote) {
                Intent intent = new Intent(this, ProjectActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}