package com.example.sunny.whiteboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        // INSERTED CODE for side bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // END INSERTED CODE for side bar

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

    // INSERTED CODE for side bar
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            /*case R.id.nav_home:
                // Handle the classes action
                Intent h = new Intent(MessagesActivity.this, MainActivity.class);
                startActivity(h);
                break;*/
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //END INSERTED CODE for side bar


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