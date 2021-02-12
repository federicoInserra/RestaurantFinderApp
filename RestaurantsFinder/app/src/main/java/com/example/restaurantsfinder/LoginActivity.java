package com.example.restaurantsfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView noAccount;

    //firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        login();
    }

    private void login(){
        email = findViewById(R.id.emailLoginField);
        password = findViewById(R.id.passwordLoginField);
        loginButton = findViewById(R.id.loginButton);
        noAccount = findViewById(R.id.noAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString().trim())){
                    email.setError("email required");
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString().trim())){
                    password.setError("password required");
                    return;
                }

                showProgressBar();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_SHORT).show();
                                    hideProgressBar();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Impossible to register, try again!", Toast.LENGTH_SHORT).show();
                                    hideProgressBar();
                                }
                            }
                        });

            }
        });


        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }

    private void hideProgressBar() {
        findViewById(R.id.progressBarLogin).setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        findViewById(R.id.progressBarLogin).setVisibility(View.VISIBLE);
    }
}