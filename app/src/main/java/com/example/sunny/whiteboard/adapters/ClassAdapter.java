package com.example.sunny.whiteboard.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.Class;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassAdapter extends RecyclerView.Adapter {
    private ArrayList<Class> classes;
    private OnItemClickListener clickListener;

    public ClassAdapter(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public interface OnItemClickListener {
        void onItemClick(Class currClass);
        void onLongClick(Class currClass);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { clickListener = listener; }

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivClassProfile;
        public TextView tvClassName;

        public ClassViewHolder(View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.class_item_tv_class_name);

            // pass selected class information to click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int currPosition = getAdapterPosition();
                        if (currPosition != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(classes.get(currPosition));
                        }
                    }
                }

            });

            // pass selected class information to click listener
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (clickListener != null) {
                        int currPosition = getAdapterPosition();
                        if (currPosition != RecyclerView.NO_POSITION) {
                            clickListener.onLongClick(classes.get(currPosition));
                            return true;
                        }
                    }
                    return false;
                }

            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_class, parent, false);
        return new ClassViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Class classClass = classes.get(position);

        // Obtain reference to current list item and set views accordingly
        ClassViewHolder classViewHolder = (ClassViewHolder) holder;
        classViewHolder.tvClassName.setText(classClass.getClassName());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

}