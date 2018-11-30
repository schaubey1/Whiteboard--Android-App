package com.example.sunny.whiteboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunny.whiteboard.models.Project;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectAdapter extends RecyclerView.Adapter {
    private ArrayList<Project> projects;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(Project project);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivProjectProfile;
        public TextView tvProjectName;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            ivProjectProfile = itemView.findViewById(R.id.project_item_iv_project_picture);
            tvProjectName = itemView.findViewById(R.id.project_item_tv_project_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int currPosition = getAdapterPosition();
                        if (currPosition != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(projects.get(currPosition));
                        }
                    }
                }
            });
        }
    }

    public ProjectAdapter(ArrayList<Project> projects) {
        this.projects = projects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_item, parent, false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Project project = projects.get(position);

        // Obtain reference to current list item and set views accordingly
        ProjectViewHolder projectViewHolder = (ProjectViewHolder) holder;
        projectViewHolder.ivProjectProfile.setImageResource(project.getImageResource());
        projectViewHolder.tvProjectName.setText(project.getName());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
