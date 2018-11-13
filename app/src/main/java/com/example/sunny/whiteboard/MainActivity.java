package com.example.sunny.whiteboard;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    public static User user;

    Button btnProject;
    Button btnMessage;
    Button btnClass;

    private FirebaseAuth mauth;
    private FirebaseFirestore db;

    private static final String TAG = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // pass registered/signed in user from previous activity through intent
        db = FirebaseFirestore.getInstance();
        user = getIntent().getParcelableExtra(RegisterActivity.mAuth.getUid());

        // set views
        btnProject = findViewById(R.id.activity_main_btn_project);
        btnMessage = findViewById(R.id.activity_main_btn_message);
        btnClass = findViewById(R.id.activity_main_btn_class);

        // handle activity navigation
        btnProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProjManagementActivity.class);
                startActivity(intent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessagesActivity.class);
                startActivity(intent);
            }
        });

        btnClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClassesActivity.class);
                startActivity(intent);
            }
        });

        // test database access
        db.collection("users/" + user.getAccountType() + "/" + user.getAccountType() + "s")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
