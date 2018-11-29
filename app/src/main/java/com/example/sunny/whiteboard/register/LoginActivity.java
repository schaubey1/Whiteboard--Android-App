package com.example.sunny.whiteboard.register;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.whiteboard.MainActivity;
import com.example.sunny.whiteboard.ProjManagementActivity;
import com.example.sunny.whiteboard.R;
import com.example.sunny.whiteboard.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvRegister;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set views
        edtEmail = findViewById(R.id.activity_login_edt_email);
        edtPassword = findViewById(R.id.activity_login_edt_password);
        btnLogin = findViewById(R.id.activity_login_btn_login);
        tvRegister = findViewById(R.id.activity_login_tv_register);

        // initialize firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // switch to register activity
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // sign in with given fields
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    // perform user login
    private void signIn() {
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        if (!isFormValid(email, password))
                Toast.makeText(this, "login information is incorrect",
                        Toast.LENGTH_SHORT).show();
        else mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            final String uid = mAuth.getCurrentUser().getUid();

                            // do user lookup to get rest of account info
                            db.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (DocumentSnapshot doc : task.getResult()) {
                                                if (doc.getString("uid").equals(uid)) {
                                                    String name = doc.getString("name");
                                                    String accountType = doc.getString("account_type");
                                                    User user = new User(uid, name, email, accountType);

                                                    // save data to shared preferences and switch to main screen
                                                    User.writeUser(getApplicationContext(), user);
                                                    updateUI(user);
                                                    break;
                                                }
                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "No account found with this information",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    // check if user has entered required fields
    private boolean isFormValid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an email and password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // handle UI changes and activity switching
    private void updateUI(User user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), ProjManagementActivity.class);
            startActivity(intent);
        }
    }
}
