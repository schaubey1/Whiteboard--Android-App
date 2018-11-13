package com.example.sunny.whiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ProjManagementActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private Button btnNewProject;

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

        // retrieve list of projects for user from database - project list from student document
        user = MainActivity.user;
        db = FirebaseFirestore.getInstance();
        db.document("users/" + user.getAccountType() + "/" + user.getAccountType() + "s/" + user.getUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Object obj = task.getResult().get("project_list");
                        if (obj != null) {
                            ArrayList<String> list = ((ArrayList)task.getResult().get("project_list"));
                            for (int i = 0; i < list.size(); i++) {
                                final String projectName = list.get(i);
                                TextView currProject = new TextView(getApplicationContext());
                                currProject.setText(projectName);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed reading document");
                    }
                });

        // handles new project creation
        btnNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to project creation page
                Intent intent = new Intent(v.getContext(), NewProjectActivity.class);
                startActivity(intent);
            }
        });
    }
}
