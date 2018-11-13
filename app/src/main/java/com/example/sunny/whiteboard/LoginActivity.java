package com.example.sunny.whiteboard;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    //public boolean loginflag= true;
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

        // get instance of firebase
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

    public void loginsuccess()
    {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }


    // perform user login
    private void signIn() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();


        if (!isFormValid(email, password))
                Toast.makeText(this, "login information is incorrect",
                        Toast.LENGTH_SHORT).show();
        else mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            loginsuccess();
                        }

                        else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            //loginflag = !loginflag;
                        }

                    }


                });
    }

    // check if user has entered required fields
    private boolean isFormValid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "One or more fields missing",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // handle UI changes and activity switching
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
