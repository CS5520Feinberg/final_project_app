package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        TextInputLayout nameInputLayout = findViewById(R.id.nameInput);
        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        TextInputLayout emailInputLayout = findViewById(R.id.emailInput);
        TextInputEditText emailInput = (TextInputEditText) emailInputLayout.getEditText();
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInput);
        TextInputEditText passwordInput = (TextInputEditText) passwordInputLayout.getEditText();
        loginButton = findViewById(R.id.LoginButton);
        signUpButton = findViewById(R.id.SignUpButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (email.isEmpty()|| password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        FDAKeywordQuery kwQuery = new FDAKeywordQuery("kangaroo meat");
        JsonObject jsonResponse = kwQuery.search();

        Log.d("FDAKeywordQuery", String.valueOf(jsonResponse.get("foods")));
    }

}