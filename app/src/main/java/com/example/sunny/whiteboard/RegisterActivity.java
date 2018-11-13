package com.example.sunny.whiteboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView tvSignIn;

    private RadioGroup rgAccountType;
    private RadioButton rbStudent;
    private RadioButton rbInstructor;
    private Button btnRegister;

    public static FirebaseAuth mAuth;

    private static final String TAG = "RegisterActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set views
        edtName = findViewById(R.id.activity_register_edt_name);
        edtEmail = findViewById(R.id.activity_register_edt_email);
        edtPassword = findViewById(R.id.activity_register_edt_password);
        tvSignIn = findViewById(R.id.activity_register_tv_signin);

        // set buttons
        rgAccountType = findViewById(R.id.activity_register_rg_group);
        rbStudent = findViewById(R.id.activity_register_rdb_student);
        rbInstructor = findViewById(R.id.activity_register_rdb_instructor);
        btnRegister = findViewById(R.id.activity_register_btn_register);

        // create instance of firebase authenticator
        mAuth = FirebaseAuth.getInstance();

        // send user to login page
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        // handle user registration
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }


    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/

    // creates account with entered email and password
    private void createAccount() {
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        // check if account information is valid, then perform user registration
        if (!isFormValid(name, email, password))
            Toast.makeText(this, "please check username/email/password",
                    Toast.LENGTH_SHORT).show();
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful",
                                        Toast.LENGTH_SHORT).show();
                                String uid = mAuth.getCurrentUser().getUid();
                                String accountType = getAccountType();
                                User user = new User(uid, name, email, accountType);

                                Log.d(TAG, "createUserWithEmail:success");
                                Log.d(TAG, "Email: " + email + "\nUID: " + uid);

                                // save user and switch to main activity
                                saveUser(user);
                                updateUI(user);
                            } else {
                                // check if email is already registered
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this,
                                            "User is already registered with this email",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // check if password strength is ok
                                if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                                    Toast.makeText(getApplicationContext(), "password is not valid",
                                            Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // handles user entry to firebase database
    private void saveUser(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userEntry = new HashMap<>();
        userEntry.put("uid", user.getUID());
        userEntry.put("name", user.getName());
        userEntry.put("email", user.getEmail());
        userEntry.put("account_type", user.getAccountType());

        // add user to database
        db.collection("users/" + user.getAccountType() + "/" + user.getAccountType() + "s")
                .add(userEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        // add user to database
        db.collection("users/" + user.getAccountType() + "/" + user.getAccountType() + "s")
                .document(mAuth.getUid()).set(userEntry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document created with id: " + mAuth.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });;
    }

    // retrieves the selected account type from the radio group
    private String getAccountType() {
        int idSelected = rgAccountType.getCheckedRadioButtonId();
        return ((RadioButton)(findViewById(idSelected))).getText().toString();
    }

    // check if user has entered required fields
    private boolean isFormValid(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "One or more fields missing", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // upate the UI accordingly
    private void updateUI(User user) {
        if (user != null) {
            // pass user information to main activity
            Intent intent = new Intent(this, MainActivity.class)
                    .putExtra(mAuth.getUid(), user);
            startActivity(intent);
        }
    }
}
