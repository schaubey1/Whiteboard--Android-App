package com.example.sunny.whiteboard.classes;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.adapters.ClassAdapter;
import com.example.sunny.whiteboard.messages.MessagesActivity;
import com.example.sunny.whiteboard.models.Class;
import com.example.sunny.whiteboard.models.User;
import com.example.sunny.whiteboard.projects.ProjectsActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

        if (user.getAccountType().equals("student"))
            userType = "students";
        else
            userType = "instructors";

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

        if (userType == "students") {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(ClassesActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.dialogue_join_class, null);

                    final EditText code = (EditText) view.findViewById(R.id.Code);

                    Button enter = (Button) view.findViewById(R.id.Enter);

                    enter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!code.getText().toString().isEmpty()) {
                                Toast.makeText(ClassesActivity.this, "Class entry successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ClassesActivity.this, "Please enter in a code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder4.setView(view);
                    AlertDialog dialog4 = builder4.create();
                    dialog4.show();
                }
            });
        }
        if (userType == "instructors") {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(ClassesActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.dialogue_instructor_create_class, null);

                    final EditText className = view.findViewById(R.id.ClassName);

                    Button create = view.findViewById(R.id.Create);

                    create.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!className.getText().toString().isEmpty()) {
                                Toast.makeText(ClassesActivity.this, "Class creation successful!", Toast.LENGTH_SHORT).show();


                                // adding dialogue_instructor_create_code
                                AlertDialog.Builder builder5 = new AlertDialog.Builder(ClassesActivity.this);
                                View V = getLayoutInflater().inflate(R.layout.dialogue_instructor_create_code, null);

                                final TextView code = V.findViewById(R.id.generatedCode);
                                code.setText(Class.generateCode());

                                // Copy button goes after this
                            } else {
                                Toast.makeText(ClassesActivity.this, "Please fill in empty fields", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    builder5.setView(view);
                    AlertDialog dialog5 = builder5.create();
                    dialog5.show();
                }
            });
        }

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