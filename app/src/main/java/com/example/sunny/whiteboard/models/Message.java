package com.example.sunny.whiteboard.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public String id;
    public String text;
    public String fromID;
    public String fromName;
    public ArrayList<String> toID;
    public Long timestamp;

    public Message(String id, String text, String fromID, String fromName, ArrayList<String> toID, Long timestamp) {
        this.id = id;
        this.text = text;
        this.fromID = fromID;
        this.fromName = fromName;
        this.toID = toID;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    // returns a list of Message objects from a list of firebase documents
    public static ArrayList<Message> convertFirebaseMessages(List<DocumentSnapshot> messageList) {
        ArrayList<Message> messages = new ArrayList<>();
        for (DocumentSnapshot currMessage : messageList) {
            messages.add(new Message(currMessage.getString("id"), currMessage.getString("text"),
                    currMessage.getString("fromID"), currMessage.getString("fromName"),
                    (ArrayList<String>)currMessage.get("toID"), currMessage.getLong("timestamp")));
        }
        return messages;
    }

    // get methods
    public String getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getFromID() {
        return fromID;
    }

    public String getFromName() { return fromName; }

    public ArrayList<String> getToID() {
        return toID;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
