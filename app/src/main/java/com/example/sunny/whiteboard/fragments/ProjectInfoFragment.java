package com.example.sunny.whiteboard.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.UserAdapter;
import com.example.sunny.whiteboard.classes.ClassesActivity;
import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProjectInfoFragment extends Fragment {

    private TextView tvName;
    private TextView tvDescription;
    private FloatingActionButton fabAddMember;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private DocumentReference currProject;
    private Project project;

    private static final String TAG = "ProjectInfoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_tab, container, false);

        // set views
        tvName = view.findViewById(R.id.fragment_info_tv_name);
        tvDescription = view.findViewById(R.id.fragment_info_tv_description);
        fabAddMember = view.findViewById(R.id.activity_project_fab_add_member);
        recyclerView = view.findViewById(R.id.fragment_info_recycler_view);

        // setup dependencies
        db = FirebaseFirestore.getInstance();
        project = TabActivity.project;
        currProject = db.collection("projects").document(project.getID());

        // setup recycler view
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // display project name, description and current members
        tvName.setText(project.getName());
        tvDescription.setText(project.getDescription());

        // retrieve updated list of project members
        currProject.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // displays emails of project members
                ArrayList<String> emails = (ArrayList<String>) documentSnapshot.get("students");
                userAdapter = new UserAdapter(User.convertEmailToUsers(emails));
                recyclerView.setAdapter(userAdapter);
            }
        });


        // add user to project member list
        fabAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
                builder.setView(view);

                // display dialog
                final AlertDialog dialog = builder.create();
                dialog.show();

                // set views
                final EditText edtAddMember = view.findViewById(R.id.email);
                Button btnAdd = view.findViewById(R.id.Add);

                // setting up popup to add user to project
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = edtAddMember.getText().toString();
                        if (!email.isEmpty()) {
                            // get uid of new user and add project to their project list
                            db.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            final List<DocumentSnapshot> users = task.getResult().getDocuments();
                                            if (users.size() > 0) {
                                                // check if user is in same class as project being added to and is student
                                                if (users.get(0).getString("accountType").equals("student")) {
                                                db.collection("classes").whereEqualTo("className", project.getClassName())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.getResult() != null) {
                                                                    DocumentSnapshot classDoc = task.getResult().getDocuments().get(0);
                                                                    ArrayList<String> students = (ArrayList<String>) classDoc.get("students");
                                                                    if (students.contains(email)) {
                                                                        // change project's member list
                                                                        currProject.update("members", FieldValue.arrayUnion(email));

                                                                        // change member's project list
                                                                        DocumentSnapshot newMember = users.get(0);
                                                                        String uid = newMember.getString("uid");
                                                                        db.collection("users").document(uid)
                                                                                .update("projectList", FieldValue.arrayUnion(project.getName()));

                                                                        Toast.makeText(getContext(), "User added to project successfully",
                                                                                Toast.LENGTH_SHORT).show();
                                                                        dialog.dismiss();
                                                                    } else
                                                                        Toast.makeText(getContext(),
                                                                                "Student is not in the same class as this project", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                } else {
                                                    Toast.makeText(getContext(), "User is not a student",
                                                            Toast.LENGTH_SHORT).show();
                                                    edtAddMember.setText("");
                                                }
                                            } else
                                                Toast.makeText(getContext(), "User does not exist",
                                                        Toast.LENGTH_SHORT).show();
                                            edtAddMember.setText("");
                                        }

                                    });

                        } else {
                            Toast.makeText(v.getContext(), "Please enter an email",
                                    Toast.LENGTH_SHORT).show();
                            edtAddMember.setText("");
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
