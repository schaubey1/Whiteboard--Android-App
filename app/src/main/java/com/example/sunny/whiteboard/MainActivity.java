package com.example.sunny.whiteboard;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnProject;
    private Button btnMessage;
    private Button btnClass;
    private Button btnSignOut;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static User user;
    public static DocumentReference currUserRef;

    private static final String TAG = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check shared preferences for existing account
        if (!accountFound()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        else {

            // initialize firebase backend
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currUserRef = db.document("users/" + user.getAccountType() + "/"
                    + user.getAccountType() + "s/" + user.getUID());

            // set views
            btnProject = findViewById(R.id.activity_main_btn_project);
            btnMessage = findViewById(R.id.activity_main_btn_message);
            btnClass = findViewById(R.id.activity_main_btn_class);
            btnSignOut = findViewById(R.id.activity_main_btn_sign_out);

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

            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
    }

    // checks shared preferences for an existing account stored on the device
    private boolean accountFound() {
        User user = User.getUser(this);
        if (user.getUID() == "" || user.getName() == "" || user.getEmail() == ""
                || user.getAccountType() == "")
            return false;

        MainActivity.user = user;
        return true;
    }

    // signs the current user out of the app - go back to registration screen
    private void signOut() {
        // delete shared preferences
        User.deleteUser(this);
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
