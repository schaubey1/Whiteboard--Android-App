package com.example.sunny.whiteboard.classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.adapters.ClassAdapter;
import com.example.sunny.whiteboard.messages.MessagesActivity;
import com.example.sunny.whiteboard.models.Class;
import com.example.sunny.whiteboard.models.User;
import com.example.sunny.whiteboard.projects.ProjectsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ClassesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClassAdapter.OnItemClickListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ClassAdapter adapter;

    private FirebaseFirestore db;
    private User user;
    private String userType;

    public static final String CLASS_KEY = "class";
    private static final String TAG = "ClassActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        // set views
        linearLayout = findViewById(R.id.activity_classes_linear_layout);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.activity_classes_fab);

        // set up firebase
        db = FirebaseFirestore.getInstance();
        user = MainActivity.user;

        // get current account type for class filtering
        userType = (user.getAccountType().equals("student") ? "students" : "instructors");

        // setup sidebar/navigation
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);

        // setup recycler view
        recyclerView = findViewById(R.id.activity_classes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // retrieve classes for current user
        db.collection("classes").whereArrayContains(userType, user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        // display class list to screen
                        ArrayList<Class> classes =
                                Class.convertFirebaseProjects(queryDocumentSnapshots.getDocuments());
                        displayClasses(classes);
                    }
                });

        // add a new class for student and instructor
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClassesActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_join_class, null);
                builder.setView(view);

                // display dialog
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText edtCode = view.findViewById(R.id.Code);
                Button enter = view.findViewById(R.id.Enter);

                // attempt to join class with inputted code
                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String code = edtCode.getText().toString();
                        if(!code.isEmpty()) {

                            // find class with matching class code
                            db.collection("classes").whereEqualTo("code", code)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            // add current user to class students list
                                            if (task.getResult().getDocuments().size() > 0) {
                                                DocumentSnapshot selectedClass = task.getResult().getDocuments().get(0);
                                                selectedClass.getReference()
                                                        .update(userType, FieldValue.arrayUnion(user.getEmail()));

                                                // update all existing projects for a class with new instructor
                                                if (userType.equals("instructors")) {
                                                    String className = selectedClass.getString("className");
                                                    db.collection("projects").whereEqualTo("className", className)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.getResult() != null) {
                                                                        // add instructor's email to instructor list of all projects for joined class
                                                                        List<DocumentSnapshot> projects = task.getResult().getDocuments();
                                                                        for (DocumentSnapshot project : projects) {
                                                                            project.getReference().update("instructors", FieldValue.arrayUnion(user.getEmail()));
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }

                                                Toast.makeText(ClassesActivity.this, "Class entry successful!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),
                                                        "No class with this code found", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(ClassesActivity.this, "Please enter a code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    // handles recycler view building to display classes
    private void displayClasses(ArrayList<Class> classes) {
        adapter = new ClassAdapter(classes);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((ClassAdapter.OnItemClickListener) ClassesActivity.this);
    }

    public void onItemClick(Class classClass) {
        // like this just for testing purposes
        Intent intent = new Intent(getApplicationContext(), ProjectsActivity.class);
        //intent.putExtra(MessagesActivity.CLASS_KEY, ClassInfo.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_bar, menu);
        return true;
    }

    // Handle action bar item clicks here.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_projmanagement:
                // Handle the project management action
                Intent j = new Intent(ClassesActivity.this, ProjectsActivity.class);
                startActivity(j);
                break;
            case R.id.nav_classes:
                // Handle the classes action
                Intent i = new Intent(ClassesActivity.this, ClassesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_messages:
                // Handle the project management action
                Intent k = new Intent(ClassesActivity.this, MessagesActivity.class);
                startActivity(k);
                break;
            case R.id.nav_sign_out:
                // handle user sign out
                MainActivity.signOut(this);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}