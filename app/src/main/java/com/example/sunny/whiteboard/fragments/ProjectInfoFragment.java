package com.example.sunny.whiteboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.UserAdapter;
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
        View view = inflater.inflate(R.layout.info_tab_fragment, container, false);

        // set views
        tvName = view.findViewById(R.id.fragment_info_tv_name);
        tvDescription = view.findViewById(R.id.fragment_info_tv_description);
        fabAddMember = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.fragment_info_recycler_view);

        // setup dependencies
        db = FirebaseFirestore.getInstance();
        project = TabActivity.project;
        currProject = db.document("projects/" + project.getID());

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
                ArrayList<String> emails = (ArrayList<String>) documentSnapshot.get("members");
                userAdapter = new UserAdapter(User.convertEmailToUsers(emails));
                recyclerView.setAdapter(userAdapter);
            }
        });


        /*// add user to project member list
        fabAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String email = etAddMember.getText().toString();
                final String email = "";
                if (!email.isEmpty()) {

                    // get uid of new user and add project to their project list
                    db.collection("users")
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
                                            Toast.makeText(getContext(), "No user found with this email",
                                                    Toast.LENGTH_SHORT).show();
                                        db.document("users/" + uid)
                                                .update("projectList", FieldValue.arrayUnion(project.getName()));
                                    } else
                                        Toast.makeText(getContext(), "User does not exist",
                                                Toast.LENGTH_SHORT).show();
                                }
                            });
                } else
                    Toast.makeText(v.getContext(), "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                //etAddMember.setText("");
            }
        });*/
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
