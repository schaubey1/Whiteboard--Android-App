package com.example.sunny.whiteboard.messages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.projects.ProjectsActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.adapters.MessageAdapter;
import com.example.sunny.whiteboard.models.Message;
import com.example.sunny.whiteboard.models.Project;
import com.example.sunny.whiteboard.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ChatLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private EditText edtEditMessage;
    private Button btnSend;

    private String chatType;
    private String projectID;
    private Project project;

    private MessageAdapter messageAdapter;
    private ArrayList<String> receivers;

    private CollectionReference currentChat;
    private FirebaseFirestore db;
    private User user;

    private static final String TAG = "ChatLogActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);

        // get chatType from current tab - group or instructor
        project = getIntent().getParcelableExtra(ProjectsActivity.PROJECT_KEY);
        chatType = "instructor";

        // Add instructors to receiver list. Members list may be outdated - only updated when selecting project chat
        receivers = project.getMembers();
        if (project.getInstructors() != null) {
            for (String instructor : project.getInstructors())
                receivers.add(instructor);
        }

        // initialize firebase backend
        db = FirebaseFirestore.getInstance();
        user = MainActivity.user;
        currentChat = db.collection("messages").document(project.getID()).collection(chatType);

        // set views
        recyclerView = findViewById(R.id.activity_chatlog_recycler_view);
        edtEditMessage = findViewById(R.id.activity_chatlog_edt_message);
        btnSend = findViewById(R.id.activity_chatlog_btn_send);

        // use if possible - improves performance
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // listen for incoming messages
        listenForMessage();

        // send message, update screen
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
    }

    // handles message retrieval from firebase
    private void listenForMessage() {
        currentChat.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // retrieve message list and display on screen
                ArrayList<Message> messages =
                        Message.convertFirebaseMessages(queryDocumentSnapshots.getDocuments());
                adapter = new MessageAdapter(messages);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    // handles message sending
    private void sendMessage() {
        String text = edtEditMessage.getText().toString();
        if (!text.equals("")) {
            currentChat.add(new Message(currentChat.getId(), text, user.getUID(), project.getMembers(),
                    System.currentTimeMillis() / 1000));
            recyclerView.scrollToPosition(adapter.getItemCount());
            edtEditMessage.setText("");
        }
        else
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();

    }
}
