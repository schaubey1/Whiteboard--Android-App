package com.example.sunny.whiteboard;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

public class ProjAdapter extends AppCompatActivity {

    Context c;
    ArrayList<String> projects;

    public ProjAdapter(Context c, ArrayList<String> projects) {
        this.c = c;
        this.projects = projects;
    }


   // @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v=LayoutInflater.from(c).inflate(R.layout.model,parent,false);
//        return new ViewHolder(v);
//    }

//    /@Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.nameTxt.setText(projects.get(position));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return projects.size();
//    }
}
