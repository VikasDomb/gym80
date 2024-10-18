package com.tkiet.gym80;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResgistrationActivity extends AppCompatActivity {

    // Declare FirebaseAuth and FirebaseDatabase instances, and views
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private TextInputEditText ptxtFirstName, ptxtLastName, ptxtEmail, ptxtPassword, ptxtCPassword;
    private MaterialButton btnCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resgistration);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        ptxtFirstName = findViewById(R.id.ptxt_firstname);
        ptxtLastName = findViewById(R.id.ptxt_lastname);
        ptxtEmail = findViewById(R.id.ptxt_email);
        ptxtPassword = findViewById(R.id.ptxtpassword);
        ptxtCPassword = findViewById(R.id.ptxt_cpassword);
        btnCreateUser = findViewById(R.id.btn_createuser);

        // Set up register button click listener
        btnCreateUser.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void registerUser() {
        String firstName = ptxtFirstName.getText().toString().trim();
        String lastName = ptxtLastName.getText().toString().trim();
        String email = ptxtEmail.getText().toString().trim();
        String password = ptxtPassword.getText().toString().trim();
        String confirmPassword = ptxtCPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(ResgistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(ResgistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user using Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get the current user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Save user info in the Realtime Database
                            String userId = firebaseUser.getUid();
                            User user = new User(firstName, lastName, email);

                            // Push the user object to Realtime Database under "Users/{userId}"
                            databaseReference.child(userId).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(ResgistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            updateUI(firebaseUser);
                                        } else {
                                            Toast.makeText(ResgistrationActivity.this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(ResgistrationActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Redirect to another activity or update UI
            startActivity(new Intent(ResgistrationActivity.this, MainActivity.class));
            finish();
        }
    }

    // User class to store user data in Firebase
    public static class User {
        public String firstName;
        public String lastName;
        public String email;

        // Default constructor required for Firebase Realtime Database
        public User() {}

        public User(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }
}
