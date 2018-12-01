package com.example.sunny.whiteboard;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.example.sunny.whiteboard.adapters.SectionsPageAdapter;
import com.example.sunny.whiteboard.fragments.InfoTabFragment;
import com.example.sunny.whiteboard.fragments.ToDoTabFragment;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Log.d(TAG, "OnCreate: starting.");

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
