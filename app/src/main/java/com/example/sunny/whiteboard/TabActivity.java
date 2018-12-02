package com.example.sunny.whiteboard;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.sunny.whiteboard.adapters.SectionsPageAdapter;
import com.example.sunny.whiteboard.fragments.GroupChatFragment;
import com.example.sunny.whiteboard.fragments.ProjectInfoFragment;
import com.example.sunny.whiteboard.fragments.InstructorChatFragment;
import com.example.sunny.whiteboard.fragments.ProjectToDoFragment;
import com.example.sunny.whiteboard.messages.MessagesActivity;
import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.projects.ProjectsActivity;

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
        previousActivityName = getIntent().getStringExtra(MessagesActivity.CLASS_KEY);

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
                mSectionsPageAdapter.addFragment(new ProjectInfoFragment(), "Info");
                mSectionsPageAdapter.addFragment(new ProjectToDoFragment(), "To-Do");
                viewPager.setAdapter(mSectionsPageAdapter);
                break;

            case "MessagesActivity":
                mSectionsPageAdapter.addFragment(new GroupChatFragment(), "Project Chat");
                mSectionsPageAdapter.addFragment(new InstructorChatFragment(), "Instructor Chat");
                viewPager.setAdapter(mSectionsPageAdapter);
                break;
        }
    }
}
