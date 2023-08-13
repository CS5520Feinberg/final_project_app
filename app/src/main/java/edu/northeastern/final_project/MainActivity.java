package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest.permission;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

                            // added isGoalReached and default Value is False
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference("users");

                            DatabaseReference userRef = databaseReference.child(user.getUid());
                            userRef.child("isGoalReached").setValue(false);

                            Toast.makeText(MainActivity.this, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        if(ContextCompat.checkSelfPermission(this, permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            //ask for permission
            requestPermissions(new String[]{permission.ACTIVITY_RECOGNITION}, 0);
        }
    }

}