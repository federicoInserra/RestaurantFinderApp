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

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button regisrationButton;
    private TextView alreadyAccount;

    //firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        registration();

    }

    private void registration(){
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passwordField);
        regisrationButton = findViewById(R.id.registrationButton);
        alreadyAccount = findViewById(R.id.alreadyRegisterButton);

        regisrationButton.setOnClickListener(new View.OnClickListener() {
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

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim() )
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                    hideProgressBar();
                                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                }
                                else {
                                    Toast.makeText(RegistrationActivity.this, "Impossible to register, try again!", Toast.LENGTH_SHORT).show();
                                    hideProgressBar();
                                }
                            }
                        });
            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    private void hideProgressBar() {
        findViewById(R.id.progressBarRegistration).setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        findViewById(R.id.progressBarRegistration).setVisibility(View.VISIBLE);
    }
}