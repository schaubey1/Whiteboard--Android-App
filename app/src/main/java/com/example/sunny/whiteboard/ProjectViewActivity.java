package com.example.sunny.whiteboard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ProjectViewActivity extends AppCompatActivity {

    private ScrollView svScroll;
    private LinearLayout lnMemberList;

    private TextView tvName;
    private TextView tvDescription;
    private EditText etAddMember;
    private Button btnAddMember;

    private FirebaseFirestore db;
    DocumentReference currProject;

    private static String TAG = "ProjectViewActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);

        // set views
        svScroll = findViewById(R.id.activity_project_view_sv_scroll);
        lnMemberList = findViewById(R.id.activity_project_view_ln_member_list);

        //
        tvName = findViewById(R.id.activity_project_view_tv_name);
        tvDescription = findViewById(R.id.activity_project_view_tv_description);
        etAddMember = findViewById(R.id.activity_project_view_et_member_email);
        btnAddMember = findViewById(R.id.activity_project_view_btn_add);

        final String projectName = getIntent().getExtras().getString("name");
        db = FirebaseFirestore.getInstance();
        currProject = db.document("projects/" + projectName);

        // set text views to show project information
        currProject.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed", e);
                            return;
                        }

                        // get document data
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String projectDescription = (String) documentSnapshot.get("description");
                            List<String> members = (List<String>) documentSnapshot.get("members");

                            // display project name, description and current members
                            displayProjectMembers(members);
                            tvName.setText(projectName);
                            tvDescription.setText(projectDescription);
                        }
                    }
                });

        // add user to project member list
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etAddMember.getText().toString();
                if (!email.isEmpty()) {

                    // get uid of new user and add project to their project list
                    db.collection("users/student/students")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    if (documents.size() > 0) {
                                        // change project's member list
                                        currProject.update("members", FieldValue.arrayUnion(email));

                                        // change member's project list
                                        DocumentSnapshot newMember = task.getResult().getDocuments().get(0);
                                        String uid = newMember.getString("uid");
                                        if (uid == "" || uid == null)
                                            Toast.makeText(getApplicationContext(), "No user found with this email",
                                                    Toast.LENGTH_SHORT).show();
                                        db.document("users/student/students/" + uid)
                                                .update("project_list", FieldValue.arrayUnion(projectName));
                                    } else
                                        Toast.makeText(getApplicationContext(), "User does not exist",
                                                Toast.LENGTH_SHORT).show();
                                }
                            });
                } else
                    Toast.makeText(v.getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                etAddMember.setText("");
            }
        });
    }

    // displays project members in list
    private void displayProjectMembers(List<String> members) {
        lnMemberList.removeAllViews();
        for (String currMember: members) {
            TextView tvMember = new TextView(getApplicationContext());
            tvMember.setText(currMember);
            lnMemberList.addView(tvMember);
        }
    }
}
