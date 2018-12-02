package com.example.sunny.whiteboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.ProjectActivity;
import com.example.sunny.whiteboard.models.ProjModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.sunny.whiteboard.ProjectsActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.ProjModel;

import java.util.List;

public class ProjectRecyclerViewAdapter extends RecyclerView.Adapter<ProjectRecyclerViewAdapter.ViewHolder> {

    private List<ProjModel> projectsList;
    private Context context;
    private FirebaseFirestore firestoreDB;

    public ProjectRecyclerViewAdapter(List<ProjModel> projectsList, Context context, FirebaseFirestore firestoreDB) {
        this.projectsList = projectsList;
        this.context = context;
        this.firestoreDB = firestoreDB;
    }

    @Override
    public ProjectRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);

        return new ProjectRecyclerViewAdapter.ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(ProjectRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPosition = position;
        final ProjModel project = projectsList.get(itemPosition);

        holder.title.setText(project.getTitle());
        holder.content.setText(project.getContent());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProject(project);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProject(project.getId(), itemPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView edit;
        ImageView delete;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvTitle);
            content = view.findViewById(R.id.tvContent);

            edit = view.findViewById(R.id.ivEdit);
            delete = view.findViewById(R.id.ivDelete);
        }
    }

    private void updateProject(ProjModel project) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UpdateProjectId", project.getId());
        intent.putExtra("UpdateProjectTitle", project.getTitle());
        intent.putExtra("UpdateProjectContent", project.getContent());
        context.startActivity(intent);
    }

    private void deleteProject(String id, final int position) {
        firestoreDB.collection("projects")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        projectsList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, projectsList.size());
                        Toast.makeText(context, "Project has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}