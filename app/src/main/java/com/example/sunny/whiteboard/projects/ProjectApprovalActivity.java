package com.example.sunny.whiteboard.projects;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.adapters.ProjectAdapter;
import com.example.sunny.whiteboard.adapters.UserAdapter;
import com.example.sunny.whiteboard.classes.ClassesActivity;
import com.example.sunny.whiteboard.messages.MessagesActivity;
import com.example.sunny.whiteboard.models.Class;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class ProjectApprovalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private UserAdapter userAdapter;

    private LinearLayout linearLayout;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private User user;

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
        user = ProjectsActivity.user;

        // setup sidebar/navigation
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle("Project Approval");
        setSupportActionBar(toolbar);

        // setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionAdapter = new SectionedRecyclerViewAdapter();


        // display projects in class the instructor is enrolled in
        db.collection("projects").whereArrayContains("instructors", user.getEmail())
                .orderBy("className")
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed", e);
                            return;
                        }

                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            // build list of projects for instructor
                            ArrayList<Project> projects =
                                    Project.convertFirebaseProjects(queryDocumentSnapshots.getDocuments());
                            if (projects.size() > 0) {

                                // split our projects by class
                                int j = 0;
                                Map<String, ArrayList<Project>> map = splitList(projects);
                                for (ArrayList<Project> projectList : map.values()) {
                                    Log.d(TAG, projectList.get(j).getClassName());

                                    // fill section for current class with projects registered for class
                                    if (projectList.size() > 0) {
                                        sectionAdapter.addSection(new ExpandableProjectsSection(
                                                projectList.get(j).getClassName(), projectList));
                                    }
                                    j++;

                                }
                            }
                        }
                        else
                            //sectionAdapter = new SectionedRecyclerViewAdapter();

                        // display expandable project sections
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(sectionAdapter);
                    }
                });
    }

    // returns a set of projects for each class
    private Map<String, ArrayList<Project>> splitList(ArrayList<Project> projects) {
        Map<String, ArrayList<Project>> map = new HashMap<>();
        for (Project project : projects) {
            ArrayList<Project> projectList = map.get(project.getClassName());
            if (projectList == null) {
                projectList = new ArrayList<Project>();
                map.put(project.getClassName(), projectList);
            }
            projectList.add(project);
        }
        return map;
    }

    // adapter for sections
    private class ExpandableProjectsSection extends StatelessSection {
        /**
         * Create a stateless Section object based on {@link SectionParameters}.
         *
         * @param sectionParameters section parameters
         */

        String className;
        ArrayList<Project> projectList;
        boolean expanded = true;

        public ExpandableProjectsSection(String classname, ArrayList<Project> projectList) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.section_project_item)
                    .headerResourceId(R.layout.section_project_header)
                    .build());

            this.className = classname;
            this.projectList = projectList;
        }

        @Override
        public int getContentItemsTotal() {
            return expanded ? projectList.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        // add the current project to the section
        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final Project project = projectList.get(position);

            itemHolder.tvProjectName.setText(project.getName());
            itemHolder.imgItem.setImageResource(R.drawable.whiteboardlogo);

            // set to pending color if not approved yet
            if (!project.getApproved()) {
                itemHolder.rootView.setBackgroundColor(Color.YELLOW);
            }

            // standard click on project
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Toast.makeText(getApplicationContext(),
                            String.format("Clicked on position #%s of Section %s",
                                    sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()),
                                    className),
                            Toast.LENGTH_SHORT).show();*/
                }
            });

            // long click on project
            itemHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Project project = projectList.get(itemHolder.getAdapterPosition() - 1);
                    if (project.getApproved() == false) {
                        displayApprovalPopup(project);
                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        // adds the current header to the list
        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvClassName.setText(className);
            headerHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    headerHolder.imgArrow.setImageResource(
                            expanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
                    );
                    sectionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // view holder for section header(class name)
    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView tvClassName;
        private final ImageView imgArrow;

        HeaderViewHolder(View view) {
            super(view);

            rootView = view;
            tvClassName = view.findViewById(R.id.section_header_class_name);
            imgArrow = view.findViewById(R.id.imgArrow);
        }
    }

    // view holder for section item(project list)
    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView imgItem;
        private final TextView tvProjectName;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            imgItem = view.findViewById(R.id.imgItem);
            tvProjectName = view.findViewById(R.id.section_item_project_name);
        }
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

    // gives option to accept or deny project proposal
    private void displayApprovalPopup(final Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_approve_project, null);
        builder.setView(view);

        // display dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // set views
        final Button btnApprove = view.findViewById(R.id.dialog_approve_project_btn);
        final Button btnDeny = view.findViewById(R.id.dialog_deny_project_btn);

        // handle project approval request
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set approved flag to true
                final Map<String, Object> setApproved = new HashMap<>();
                setApproved.put("approved", true);
                db.collection("projects").document(project.getID())
                        .update(setApproved);
                project.setApproved(true);
                dialog.dismiss();
            }
        });

        // handle project denial request
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("projects").document(project.getID())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult() != null) {
                                    DocumentReference projectRef = task.getResult().getReference();

                                    // delete project from each member's projectList
                                    ArrayList<String> members = (ArrayList<String>) task.getResult().get("students");
                                    for (String memberEmail : members) {
                                        db.collection("users")
                                                .whereEqualTo("email", memberEmail)
                                                .whereArrayContains("projectList", project.getName())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.getResult() != null) {
                                                            List<DocumentSnapshot> users = task.getResult().getDocuments();
                                                            final Map<String, Object> removeProject = new HashMap<>();
                                                            removeProject.put("projectList", FieldValue.arrayRemove(project.getName()));
                                                            users.get(0).getReference().update(removeProject);
                                                        }
                                                    }
                                                });
                                    }

                                    // delete project document
                                    projectRef.delete();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
    }
}
