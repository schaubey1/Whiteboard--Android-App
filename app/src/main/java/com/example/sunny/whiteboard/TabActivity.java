package com.example.sunny.whiteboard;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.sunny.whiteboard.adapters.SectionsPageAdapter;
import com.example.sunny.whiteboard.fragments.InfoTabFragment;
import com.example.sunny.whiteboard.fragments.ToDoTabFragment;
import com.example.sunny.whiteboard.models.Project;

public class TabActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public static Project project;

    private static final String TAG = "TabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Log.d(TAG, "OnCreate: starting.");

        // retrieve project data from selection
        project = getIntent().getParcelableExtra(ProjectsActivity.PROJECT_KEY);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPageAdapter.addFragment(new InfoTabFragment(), "Info");
        mSectionsPageAdapter.addFragment(new ToDoTabFragment(), "To-Do");
        viewPager.setAdapter(mSectionsPageAdapter);
    }
}
