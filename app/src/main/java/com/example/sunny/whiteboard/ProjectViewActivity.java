package com.example.sunny.whiteboard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectViewActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvDescription;
    private EditText etAddMember;
    private Button btnAddMember;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);

        // set views
        tvName = findViewById(R.id.activity_project_view_tv_name);
        tvDescription = findViewById(R.id.activity_project_view_tv_description);
        etAddMember = findViewById(R.id.activity_project_view_et_member_email);
        btnAddMember = findViewById(R.id.activity_project_view_btn_add);

        db = FirebaseFirestore.getInstance();
        final String projectName = getIntent().getExtras().getString("name");
        Log.d("ProjectViewActivityLog", projectName);

        db.document("projects/" + projectName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String projectDescription = (String) task.getResult().get("description");
                        //ArrayList<String> members = (ArrayList<String>) task.getResult().get("members");

                        tvName.setText(projectName);
                        tvDescription.setText(projectDescription);
                    }
                });

        // handle member add
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etAddMember.getText().toString();
                if (!email.isEmpty()) {
                    // add member to project members list
                    db.document("projects/" + projectName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> members = (ArrayList<String>) task.getResult().get("members");
                                    members.add(email);
                                    db.document("projects/" + projectName)
                                            .update("members", members);
                                }
                            });

                    // add project to new member's project list
                    // get current projects for added member, then add new project
                    /*db.get()
                    ArrayList<String> projects = new ArrayList<>();
                    projects.add(projectName);
                    db.document("users/student/students/" + getUid())
                            .update("project_list", projects);*/
                }

                etAddMember.setText("");
            }
        });
    }
}
