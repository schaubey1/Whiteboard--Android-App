package com.example.sunny.whiteboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunny.whiteboard.R;

public class ProjectToDoFragment extends Fragment {
    private static final String TAG = "ProjectToDoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_tab_fragment, container, false);
        return view;
    }
}
