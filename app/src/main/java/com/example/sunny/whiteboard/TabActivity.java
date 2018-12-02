package com.example.sunny.whiteboard;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.sunny.whiteboard.adapters.SectionsPageAdapter;
import com.example.sunny.whiteboard.fragments.GroupTabFragment;
import com.example.sunny.whiteboard.fragments.InfoTabFragment;
import com.example.sunny.whiteboard.fragments.InstructorTabFragment;
import com.example.sunny.whiteboard.fragments.ToDoTabFragment;
import com.example.sunny.whiteboard.models.Project;

public class TabActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public static Project project;
    private String previousActivityName;

    private static final String TAG = "TabActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Log.d(TAG, "OnCreate: starting.");

        // retrieve project data from selection
        project = getIntent().getParcelableExtra(ProjectsActivity.PROJECT_KEY);
        previousActivityName = getIntent().getStringExtra(ProjectsActivity.CLASS_KEY);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    // sends user to correct tab activity
    private void setupViewPager(ViewPager viewPager) {
        switch (previousActivityName) {
            case "ProjectsActivity":
                mSectionsPageAdapter.addFragment(new InfoTabFragment(), "Info");
                mSectionsPageAdapter.addFragment(new ToDoTabFragment(), "To-Do");
                viewPager.setAdapter(mSectionsPageAdapter);
                break;

            case "MessagesActivity":
                mSectionsPageAdapter.addFragment(new GroupTabFragment(), "Project Chat");
                mSectionsPageAdapter.addFragment(new InstructorTabFragment(), "Instructor Chat");
                viewPager.setAdapter(mSectionsPageAdapter);
                break;
        }
    }
}
