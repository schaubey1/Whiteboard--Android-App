package com.example.sunny.whiteboard;

        import android.graphics.Typeface;
        import android.os.Bundle;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.content.Intent;
        import android.widget.TextView;

        import com.example.sunny.whiteboard.classes.ClassesActivity;
        import com.example.sunny.whiteboard.messages.MessagesActivity;
        import com.example.sunny.whiteboard.models.Project;
        import com.example.sunny.whiteboard.projects.ProjectsActivity;

public class SideBarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    //FloatingActionButton fab;
    ActionBarDrawerToggle toggle;
    TextView textViewUsername;
    TextView textViewEmail;
    Typeface myfont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set Whiteboard font
        myfont = Typeface.createFromAsset(this.getAssets(), "fonts/montserrat_light.ttf");


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Change the account name and email in the header
        View headerView = navigationView.getHeaderView(0);


        // View
        textViewUsername = (TextView) headerView.findViewById(R.id.nav_Name);
        textViewEmail= (TextView) headerView.findViewById(R.id.nav_Email);

        // Set username & email
        textViewUsername.setText(MainActivity.user.getName());
        textViewEmail.setText(MainActivity.user.getEmail());*/


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
            case R.id.nav_classes:
                Intent intent= new Intent(SideBarActivity.this,ClassesActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_projmanagement:
                startActivity(new Intent(SideBarActivity.this, ProjectsActivity.class));
                drawer.closeDrawers();
                return true;
            case R.id.nav_messages:
                startActivity(new Intent(SideBarActivity.this, MessagesActivity.class));
                drawer.closeDrawers();
                return true;
            case R.id.nav_sign_out:
                ProjectsActivity.signOut(this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}