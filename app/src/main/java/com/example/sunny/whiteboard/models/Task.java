package com.example.sunny.whiteboard.models;

import java.util.ArrayList;
import java.util.List;

public class Task {
    public String text;
    public String id;

    public Task() {}

    public Task(String text, String id) {
        this.text = text;
        this.id = id;
    }

    public String getText() { return text; }

    public String getID() { return id; }

    public static ArrayList<String> convertTasks(List<Task> taskList) {
        ArrayList<String> tasks = new ArrayList<>();
        for (Task task: taskList) {
            tasks.add(task.getText());
        }

        return tasks;
    }
}
