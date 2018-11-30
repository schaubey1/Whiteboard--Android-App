package com.example.sunny.whiteboard;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sunny.whiteboard.models.User;
import com.example.sunny.whiteboard.register.RegisterActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ProjManagementActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ScrollView scrollView;
    private LinearLayout linearLayout;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private FirebaseFirestore db;
    private User user;

    private static final String TAG = "ProjManageActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proj_management);

        // set views
        scrollView = findViewById(R.id.activity_project_scroll_view);
        linearLayout = findViewById(R.id.activity_project_linear_layout);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);

        // setup sidebar/navigation
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);

        // handle floating action button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInputDialog();
            }
        });
    }

    private void displayInputDialog()
    {
        Dialog d=new Dialog(this);
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.input_dialog);

        nameEditTxt= (EditText) d.findViewById(R.id.nameEditText);
        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);

                // build list of projects
                Object docData = documentSnapshot.get("projectList");
                if (docData != null) {
                    ArrayList<String> list = (ArrayList) docData;
                    for (int i = 0; i < list.size(); i++) {
                        final String projectName = list.get(i);
                        TextView currProject = new TextView(getApplicationContext());
                        currProject.setText(projectName);
                        currProject.setTextSize(22);
                        currProject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ProjectViewActivity.class)
                                        .putExtra("name", projectName);
                                startActivity(intent);
                            }
                        });
                        linearLayout.addView(currProject);
                    }
                }
            }
        });

        d.show();
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
                Intent j = new Intent(ProjManagementActivity.this, ProjManagementActivity.class);
                startActivity(j);
                break;
            case R.id.nav_classes:
                // Handle the classes action
                Intent i = new Intent(ProjManagementActivity.this, ClassesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_messages:
                // Handle the project management action
                Intent k = new Intent(ProjManagementActivity.this, MessagesActivity.class);
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

    // signs the current user out of the app - go back to registration screen

}