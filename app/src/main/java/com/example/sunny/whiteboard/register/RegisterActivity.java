package com.example.sunny.whiteboard.register;

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

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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
                finish();
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

    // creates account with entered email and password
    private void createAccount() {
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();
        final String accountType = getAccountType();

        // check if account information is valid, then perform user registration
        if (isFormValid(name, email, password)) {
            // save user to SharedPreferences and firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful",
                                        Toast.LENGTH_SHORT).show();

                                String uid = mAuth.getCurrentUser().getUid();
                                User user = new User(uid, name, email, accountType);

                                Log.d(TAG, "createUserWithEmail:success");
                                Log.d(TAG, "Email: " + email + "\nUID: " + uid);

                                // save user and switch to main activity
                                User.writeUser(getApplicationContext(), user);
                                saveUserToFirebase(user);
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
    private void saveUserToFirebase(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userEntry = new HashMap<>();
        userEntry.put("uid", user.getUID());
        userEntry.put("name", user.getName());
        userEntry.put("email", user.getEmail());
        userEntry.put("account_type", user.getAccountType());
        userEntry.put("project_list", null);

        // add user to database
        db.collection("users")
                .document(mAuth.getUid())
                .set(userEntry)
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
        if (idSelected == -1) {
            return "";
        }
        return ((RadioButton)(findViewById(idSelected))).getText().toString();
    }

    // check if user has entered required fields
    private boolean isFormValid(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a name, email and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (getAccountType().isEmpty()) {
            Toast.makeText(this, "Please select an account type",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // update the UI accordingly
    private void updateUI(User user) {
        if (user != null) {
            // pass user information to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}