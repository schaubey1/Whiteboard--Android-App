package com.example.sunny.whiteboard.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.widget.Toast;


import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.TabActivity;
import com.example.sunny.whiteboard.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sunny.whiteboard.TabActivity.project;

public class ProjectToDoFragment extends Fragment {
    FirebaseFirestore db;
    @BindView(R.id.fragment_todo_add_task)
    Button button;
    //@BindView(R.id.fragment_todo_enter_task)
    EditText editText;
    @BindView(R.id.fragment_todo_list)
    ListView listView;
    private List<String> tasksList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_todo, container, false);


        ButterKnife.bind(this, view);
        db = FirebaseFirestore.getInstance();

        db.collection("projects").document(project.getID()).collection("todo").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                tasksList.clear();
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    tasksList.add(snapshot.getString("todolist"));
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_selectable_list_item, tasksList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   int pos, long id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        // TODO Auto-generated method stub
                        builder.setTitle("Delete Task?");
                        //show dialog asking user option to delete or not
                        //On OK click, dataset.remove(position);
                        //adapter.notifyDatasetChanged();

                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                              //delete from db.
                                adapter.notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                        return true;
                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Task");
// Set up the input
                final EditText input = new EditText(getContext());
                builder.setView(input);
// Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String content;
                        content = input.getText().toString();
                        savelist(content);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });
        return view;

    }
    public void savelist  (String arg1)
    {    Map<String, Object> savetodo  = new HashMap<>();

        savetodo.put("todolist",arg1);

        db.collection("projects").document(project.getID()).collection("todo").add(savetodo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                db.collection("projects").document(project.getID()).collection("todo").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        tasksList.clear();
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            tasksList.add(snapshot.getString("todolist"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_selectable_list_item, tasksList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
    }

}




