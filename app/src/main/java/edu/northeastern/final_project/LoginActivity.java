package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        TextInputLayout emailInputLayout = findViewById(R.id.emailInput_login);
        TextInputEditText emailInput = (TextInputEditText) emailInputLayout.getEditText();
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInput_login);
        TextInputEditText passwordInput = (TextInputEditText) passwordInputLayout.getEditText();
        loginButton = findViewById(R.id.LogInButton_login);
        signUpButton = findViewById(R.id.SignUpButton_login);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (email.isEmpty()|| password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sign In Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signUpButton.setOnClickListener(v -> {
            finish();
        });
    }
}