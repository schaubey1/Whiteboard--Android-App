package com.example.sunny.whiteboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter {
    private ArrayList<User> users;

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmail;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.user_item_tv_email);
        }
    }

    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_member, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);

        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.tvEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
