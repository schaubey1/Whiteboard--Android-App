package com.example.sunny.whiteboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NewProjectActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtDescription;
    private Button btnCreate;

    private CollectionReference projectRef;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        // set views
        edtName = findViewById(R.id.activity_new_project_edt_name);
        edtDescription = findViewById(R.id.activity_new_project_edt_description);
        btnCreate = findViewById(R.id.activity_new_project_btn_create);

        // setup firebase
        db = FirebaseFirestore.getInstance();
        user = MainActivity.user;
        projectRef = db.collection("projects");

        // create new project
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get updated project information
                String projectName = edtName.getText().toString();
                String description = edtDescription.getText().toString();
                ArrayList<String> members = new ArrayList<>();
                members.add(user.getEmail());

                // add project to project collection
                projectRef.add(new Project(null, 0, projectName, description, members))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.update("id", documentReference.getId());
                            }
                        });

                // add project to user's project list
                MainActivity.userRef.update("projectList", FieldValue.arrayUnion(projectName));

                // switch back to project management
                Intent intent = new Intent(v.getContext(), ProjectsActivity.class);
                startActivity(intent);
            }
        });
    }
}
