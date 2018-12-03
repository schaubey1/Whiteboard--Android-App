package com.example.sunny.whiteboard.projects;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AlertDialog;
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
        import android.widget.Spinner;
        import android.widget.Toast;

        import com.example.sunny.whiteboard.classes.ClassesActivity;
        import com.example.sunny.whiteboard.MainActivity;
        import com.example.sunny.whiteboard.messages.MessagesActivity;
        import com.example.sunny.whiteboard.ProjectActivity;
        import com.example.sunny.whiteboard.R;
        import com.example.sunny.whiteboard.TabActivity;
        import com.example.sunny.whiteboard.adapters.ProjectAdapter;
        import com.example.sunny.whiteboard.models.Project;
        import com.example.sunny.whiteboard.models.User;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QuerySnapshot;

        import java.util.ArrayList;

        import javax.annotation.Nullable;

public class ProjectsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProjectAdapter.OnItemClickListener {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private User user;

    public static final String PROJECT_KEY = "project";
    private static final String TAG = "ProjManageActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // set views
        linearLayout = findViewById(R.id.activity_project_linear_layout);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.activity_project_recycler_view);
        fab = findViewById(R.id.activity_project_fab);

        // set up firebase
        db = FirebaseFirestore.getInstance();
        user = MainActivity.user;

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
        db.collection("projects").whereArrayContains("members", user.getEmail())
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
                        displayProjects(projects);
                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ProjectsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialogue_create_project, null);

                final EditText title = (EditText) view.findViewById(R.id.Title);
                final EditText description = (EditText) view.findViewById(R.id.Description);
                final Spinner choose = (Spinner) view.findViewById(R.id.Choose);

                Button create = (Button) view.findViewById(R.id.Create);

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!title.getText().toString().isEmpty() && !description.getText().toString().isEmpty()) {
                            Toast.makeText(ProjectsActivity.this, "Project creation successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProjectsActivity.this, "Please fill in empty fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder2.setView(view);
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
            }
        });
    }

    // handles recycler view building to display projects
    private void displayProjects(ArrayList<Project> projects) {
        adapter = new ProjectAdapter(projects);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(ProjectsActivity.this);
    }

    @Override
    public void onItemClick(Project project) {
        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        intent.putExtra(PROJECT_KEY, project);
        intent.putExtra(MessagesActivity.CLASS_KEY, "ProjectsActivity");
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
                Intent j = new Intent(ProjectsActivity.this, ProjectsActivity.class);
                startActivity(j);
                break;
            case R.id.nav_classes:
                // Handle the classes action
                Intent i = new Intent(ProjectsActivity.this, ClassesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_messages:
                // Handle the project management action
                Intent k = new Intent(ProjectsActivity.this, MessagesActivity.class);
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