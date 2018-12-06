package com.example.sunny.whiteboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.Message;
import com.example.sunny.whiteboard.projects.ProjectsActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FirebaseAuth mAuth;
    private ArrayList<Message> messages;

    // ViewHolder for a To-type message
    public static class ToMessageViewHolder extends RecyclerView.ViewHolder {
        //public CircleImageView ivImage;
        public TextView tvMessage;
        public TextView tvName;

        public ToMessageViewHolder(View itemView) {
            super(itemView);
            //ivImage = itemView.findViewById(R.id.view_to_message_image);
            tvMessage = itemView.findViewById(R.id.view_to_message_text);
            tvName = itemView.findViewById(R.id.view_to_message_name);
        }
    }

    // ViewHolder for a From-type message
    public static class FromMessageViewHolder extends RecyclerView.ViewHolder {
        //public CircleImageView ivImage;
        public TextView tvMessage;
        public TextView tvName;

        public FromMessageViewHolder(View itemView) {
            super(itemView);
            //ivImage = itemView.findViewById(R.id.view_from_message_image);
            tvMessage = itemView.findViewById(R.id.view_from_message_text);
            tvName = itemView.findViewById(R.id.view_from_message_name);
        }
    }

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
        this.mAuth = FirebaseAuth.getInstance();
    }

    // creates views for recycler view
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View toView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_to_message, parent, false);
                return new ToMessageViewHolder(toView);

            case 1:
                View fromView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_from_message, parent, false);
                return new FromMessageViewHolder(fromView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // get current message
        Message message = messages.get(position);

        // get layout reference and add information to view
        switch (holder.getItemViewType()) {
            case 0:
                ToMessageViewHolder toMessageViewHolder = (ToMessageViewHolder) holder;
                toMessageViewHolder.tvMessage.setText(message.getText());
                toMessageViewHolder.tvName.setText(message.getFromName());
                //toMessageViewHolder.ivImage.setImageBitmap(bitmap);
                break;

            case 1:
                FromMessageViewHolder fromMessageViewHolder = (FromMessageViewHolder) holder;
                fromMessageViewHolder.tvMessage.setText(message.getText());
                fromMessageViewHolder.tvName.setText(message.getFromName());
                //fromMessageViewHolder.ivImage.setImageBitmap(bitmap);
                break;
        }
    }

    // provides an integer mapping to message type
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (ProjectsActivity.user.getUID().equals(message.getFromID())) {
            // message is being sent by current user(toUser)
            return 0;
        }
        else {
            // message is being sent by someone else(fromUser)
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
