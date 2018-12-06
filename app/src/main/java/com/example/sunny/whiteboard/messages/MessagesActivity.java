package com.example.sunny.whiteboard.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.example.sunny.whiteboard.classes.ClassesActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.ProjectAdapter;
import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.models.User;
import com.example.sunny.whiteboard.projects.ProjectApprovalActivity;
import com.example.sunny.whiteboard.projects.ProjectsActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MessagesActivity extends AppCompatActivity
        implements ProjectAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    public static User user;
    private String userType;

    public static final String PROJECT_KEY = "project";
    public static final String CLASS_KEY = "class";
    private static final String TAG = "MessagesActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // set views
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.activity_messages_recycler_view);

        // setup sidebar/toolbar
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle("Messages");
        setSupportActionBar(toolbar);

        // initialize firebase backend
        db = FirebaseFirestore.getInstance();
        user = ProjectsActivity.user;

        // retrieve correct list of projects
        if (user.getAccountType().equals("student"))
            userType = "students";
        else
            userType = "instructors";

        // initialize Recycler View dependencies
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // retrieve project list for current user
        db.collection("projects").whereArrayContains(userType, user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        // build a list of project objects from the queried projects in firebase
                        ArrayList<Project> projects =
                                Project.convertFirebaseProjects(queryDocumentSnapshots.getDocuments());
                        if (projects != null && projects.size() > 0)
                            displayProjects(projects);
                    }
                });
    }

    // handles recycler view building to display projects
    private void displayProjects(ArrayList<Project> projects) {
        adapter = new ProjectAdapter(projects);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(MessagesActivity.this);
    }

    // go to project conversation with project data
    @Override
    public void onItemClick(Project project) {
        if (user.getAccountType().equals("student")) {
            Intent intent = new Intent(getApplicationContext(), TabActivity.class);
            intent.putExtra(ProjectsActivity.PROJECT_KEY, project);
            intent.putExtra(CLASS_KEY, "MessagesActivity");
            startActivity(intent);
        } else {
            // user is an instructor - send to activity with only ta chat
            Intent intent = new Intent(getApplicationContext(), ChatLogActivity.class);
            intent.putExtra(PROJECT_KEY, project);
            startActivity(intent);
        }
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
                ProjectsActivity.signOut(this);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
