package com.example.sunny.whiteboard;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sunny.whiteboard.models.User;
import com.example.sunny.whiteboard.register.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnProject;
    private Button btnMessage;
    private Button btnClass;
    private Button btnSignOut;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

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
            currUserRef = db.document("users/" + user.getUID());

            // set views
            btnProject = findViewById(R.id.activity_main_btn_project);
            btnMessage = findViewById(R.id.activity_main_btn_message);
            btnClass = findViewById(R.id.activity_main_btn_class);
            btnSignOut = findViewById(R.id.activity_main_btn_sign_out);
            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            toolbar = findViewById(R.id.toolbar);
            fab = findViewById(R.id.fab);

            // setup sidebar/toolbar
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
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), NewProjectActivity.class);
                startActivity(intent);*/
                }
            });

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
                    MainActivity.signOut(v.getContext());;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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
                Intent i = new Intent(MainActivity.this, ClassesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_projmanagement:
                // Handle the project management action
                Intent j = new Intent(MainActivity.this, ProjManagementActivity.class);
                startActivity(j);
                break;
            case R.id.nav_messages:
                // Handle the project management action
                Intent k = new Intent(MainActivity.this, MessagesActivity.class);
                startActivity(k);
                break;
            case R.id.nav_sign_out:
                // handle user sign out
                signOut(this);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public static void signOut(Context context) {
        // delete shared preferences
        User.deleteUser(context);
        context.startActivity(new Intent(context, RegisterActivity.class));
    }
}