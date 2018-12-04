package com.example.sunny.whiteboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.sunny.whiteboard.TabActivity.project;

public class ProjectToDoFragment extends Fragment {
    EditText editText;
    TextView tvTextView;
    private FirebaseFirestore db;
    private static final String TAG = "ProjectToDoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        final Button button=view.findViewById(R.id.savebtn);
        editText = view.findViewById(R.id.editText);

        Project project = TabActivity.project;
        db = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String content = editText.getText().toString();
                savelist(content);
            }
        });

        return view;

    }

    public void savelist  (String arg)
    {    Map<String, Object> savetodo  = new HashMap<>();

        savetodo.put("todolist",arg);

        db.collection("projects").document(project.getID()).collection("todo").add(savetodo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });
    }
}




