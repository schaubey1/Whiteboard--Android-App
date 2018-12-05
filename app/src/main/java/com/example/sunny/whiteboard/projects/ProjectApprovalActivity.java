package com.example.sunny.whiteboard.projects;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.ProjectAdapter;
import com.example.sunny.whiteboard.classes.ClassesActivity;
import com.example.sunny.whiteboard.messages.MessagesActivity;
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

import javax.annotation.Nullable;

public class ProjectApprovalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProjectAdapter.OnItemClickListener {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private User user;
    private String userType;

    public static final String PROJECT_KEY = "project";
    private static final String TAG = "ProjectApprovalLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_approval);

        // set views
        linearLayout = findViewById(R.id.activity_project_approval_linear_layout);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.activity_project_approval_recycler_view);

        // set up firebase
        db = FirebaseFirestore.getInstance();
        user = MainActivity.user;
        userType = MainActivity.userType;

        // setup sidebar/navigation
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle("Projects");
        setSupportActionBar(toolbar);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // retrieve project list for current user
        db.collection("projects").whereArrayContains("instructors", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        /*
                        Need to build a grouped list instead of displaying all elements at once
                         */

                        // build a list of project objects from the queried projects in firebase
                        ArrayList<Project> projects =
                                Project.convertFirebaseProjects(queryDocumentSnapshots.getDocuments());
                        displayProjects(projects);
                    }
                });

    }

    // handles recycler view building to display projects
    private void displayProjects(ArrayList<Project> projects) {
        adapter = new ProjectAdapter(projects);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    // handles single click event
    @Override
    public void onItemClick(Project project) {
        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        intent.putExtra(PROJECT_KEY, project);
        intent.putExtra(MessagesActivity.CLASS_KEY, "ProjectApprovalActivity");
        startActivity(intent);
    }

    // handles long click event
    @Override
    public void onLongClick(Project project) {
        Log.d(TAG, "Long click received");
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
            case R.id.nav_classes:
                // Handle the classes action
                Intent i = new Intent(this, ClassesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_projmanagement:
                if (user.getAccountType().equals("student"))
                    startActivity(new Intent(this, ProjectsActivity.class));
                else
                    startActivity(new Intent(this, ProjectApprovalActivity.class));
                break;
            case R.id.nav_messages:
                // Handle the project management action
                Intent l = new Intent(this, MessagesActivity.class);
                startActivity(l);
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
