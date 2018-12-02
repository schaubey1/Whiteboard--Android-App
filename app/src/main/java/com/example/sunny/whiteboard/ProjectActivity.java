package com.example.sunny.whiteboard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.sunny.whiteboard.models.ProjModel;

import java.util.Map;

public class ProjectActivity extends AppCompatActivity {

    private static final String TAG = "ProjectActivity";

    TextView edtTitle;
    TextView edtContent;
    Button btAdd;

    private FirebaseFirestore firestoreDB;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btAdd = findViewById(R.id.btAdd);

        firestoreDB = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("UpdateProjectId");

            edtTitle.setText(bundle.getString("UpdateProjectTitle"));
            edtContent.setText(bundle.getString("UpdateProjectContent"));
        }

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();

                if (title.length() > 0) {
                    if (id.length() > 0) {
                        updateProject(id, title, content);
                    } else {
                        addProject(title, content);
                    }
                }

                finish();
            }
        });
    }

    private void updateProject(String id, String title, String content) {
        Map<String, Object> project = (new ProjModel(id, title, content)).toMap();

        firestoreDB.collection("projects")
                .document(id)
                .set(project)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Project document update successful!");
                        Toast.makeText(getApplicationContext(), "Project has been updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding Project document", e);
                        Toast.makeText(getApplicationContext(), "Project could not be updated!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProject(String title, String content) {
        Map<String, Object> project = new ProjModel(title, content).toMap();

        firestoreDB.collection("projects")
                .add(project)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Project has been added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding Project document", e);
                        Toast.makeText(getApplicationContext(), "Project could not be added!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
