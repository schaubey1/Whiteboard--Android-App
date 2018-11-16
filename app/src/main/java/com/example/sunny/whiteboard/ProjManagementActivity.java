package com.example.sunny.whiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ProjManagementActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private Button btnNewProject;
    private Button btnHome;

    private FirebaseFirestore db;
    private User user;

    private static final String TAG = "ProjManageActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proj_management);

        // set views
        scrollView = findViewById(R.id.activity_project_scroll_view);
        linearLayout = findViewById(R.id.activity_project_linear_layout);
        btnNewProject = findViewById(R.id.activity_project_btn_new_project);
        btnHome = findViewById(R.id.activity_project_btn_home);

        // retrieve list of projects for user from database - project list from student document
        user = MainActivity.user;
        db = FirebaseFirestore.getInstance();

        // retrieve project list for current user
        MainActivity.currUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // build list of projects
                Object docData = documentSnapshot.get("project_list");
                if (docData != null) {
                    ArrayList<String> list = (ArrayList) docData;
                    for (int i = 0; i < list.size(); i++) {
                        final String projectName = list.get(i);
                        TextView currProject = new TextView(getApplicationContext());
                        currProject.setText(projectName);
                        currProject.setTextSize(22);
                        currProject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ProjectViewActivity.class)
                                        .putExtra("name", projectName);
                                startActivity(intent);
                            }
                        });
                        linearLayout.addView(currProject);
                    }
                }
            }
        });

        // switch to project creation activity
        btnNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to project creation page
                Intent intent = new Intent(v.getContext(), NewProjectActivity.class);
                startActivity(intent);
            }
        });

        // switch user back to home page
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}