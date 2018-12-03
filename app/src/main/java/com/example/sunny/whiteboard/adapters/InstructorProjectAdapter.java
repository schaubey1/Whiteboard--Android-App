package com.example.sunny.whiteboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.Project;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InstructorProjectAdapter extends RecyclerView.Adapter {
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
        public TextView tvProjectDesc;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            ivProjectProfile = itemView.findViewById(R.id.instructor_project_item_iv_project_picture);
            tvProjectName = itemView.findViewById(R.id.instructor_project_item_tv_project_name);
            tvProjectDesc = itemView.findViewById(R.id.instructor_project_item_tv_project_description);

            // pass selected project information to click listener
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

    public InstructorProjectAdapter(ArrayList<Project> projects) {
        this.projects = projects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_project, parent, false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Project project = projects.get(position);

        // Obtain reference to current list item and set views accordingly
        ProjectViewHolder projectViewHolder = (ProjectViewHolder) holder;
        projectViewHolder.ivProjectProfile.setImageResource(project.getImageResource());
        projectViewHolder.tvProjectName.setText(project.getName());
        projectViewHolder.tvProjectDesc.setText(project.getDescription());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
