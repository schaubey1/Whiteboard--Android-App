package com.example.sunny.whiteboard.classes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.UserAdapter;
import com.example.sunny.whiteboard.models.Class;
import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class ClassInfoActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvCode;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private DocumentReference classRef;
    private Class currClass;

    private static final String TAG = "ClassInfoActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_info);

        // set views
        tvName = findViewById(R.id.activity_class_info_tv_name);
        tvCode = findViewById(R.id.activity_class_info_tv_code);
        recyclerView = findViewById(R.id.activity_class_info_recycler_view);

        // setup dependencies
        db = FirebaseFirestore.getInstance();
        currClass = getIntent().getParcelableExtra(ClassesActivity.CLASS_KEY);
        classRef = db.collection("classes").document(currClass.getID());

        // setup recycler view
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // display project name, description and current members
        tvName.setText(currClass.getClassName());
        tvCode.setText("Class code: " + currClass.getCode());

        tvCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyCode();
                return true;
            }
        });

        // retrieve updated list of project members
        classRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // displays emails of project members
                ArrayList<String> students = (ArrayList<String>) documentSnapshot.get("students");
                if (students != null && students.size() > 0) {
                    userAdapter = new UserAdapter(User.convertEmailToUsers(students));
                    recyclerView.setAdapter(userAdapter);
                }
            }
        });
    }

    // copies code to clipboard
    private void copyCode() {
        final String code = tvCode.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Class enrollment code", code);
        clipboard.setPrimaryClip(clip);
        Snackbar barCode = Snackbar.make(recyclerView, "Class code copied", Snackbar.LENGTH_SHORT);
        barCode.show();
    }
}
