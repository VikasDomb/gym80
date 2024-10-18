package com.tkiet.gym80;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Auth instance
    private TextInputEditText inputEmail, inputPassword; // Email and password input fields
    private MaterialButton btnLogin;
    private TextView registerTV, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        registerTV = findViewById(R.id.registerTV);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Set onClick listeners
        btnLogin.setOnClickListener(view -> loginUser());

        // Redirect to Registration activity
        registerTV.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ResgistrationActivity.class));
        });

        // Handle forgot password
        forgotPassword.setOnClickListener(view -> {
            // Add forgot password functionality here
        });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Validate email and password input
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
            return;
        }

        // Authenticate user using Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Redirect to another activity, for example, MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
