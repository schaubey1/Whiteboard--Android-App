package com.example.sunny.whiteboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sunny.whiteboard.models.User;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewProjectActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtDescription;
    private Button btnCreate;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        // set views
        edtName = findViewById(R.id.activity_new_project_edt_name);
        edtDescription = findViewById(R.id.activity_new_project_edt_description);
        btnCreate = findViewById(R.id.activity_new_project_btn_create);

        user = MainActivity.user;

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = edtName.getText().toString();
                String description = edtDescription.getText().toString();

                // create database entry
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> projectEntry = new HashMap<>();
                ArrayList<String> members = new ArrayList<>();
                members.add(user.getEmail());
                projectEntry.put("name", projectName);
                projectEntry.put("description", description);
                projectEntry.put("members", members);

                // add project to project collection
                db.document("projects/" + projectName)
                        .set(projectEntry);

                // add project to user's project list
                db.document("users/" + user.getAccountType() + "/" + user.getAccountType() + "s/" + user.getUID())
                        .update("project_list", FieldValue.arrayUnion(projectName));

                // switch back to project management
                Intent intent = new Intent(v.getContext(), ProjManagementActivity.class);
                startActivity(intent);
            }
        });
    }
}
