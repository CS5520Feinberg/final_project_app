package edu.northeastern.final_project;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;


import android.util.Log;
import android.view.View;


import android.widget.Button;
import android.widget.Toast;
import android.Manifest.permission;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import edu.northeastern.final_project.backgroundThreadClass.UniquePhoneNumberThread;
import edu.northeastern.final_project.validation.GenericStringValidation;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        TextInputLayout nameInputLayout = findViewById(R.id.nameInput);
        TextInputEditText nameInput = (TextInputEditText) nameInputLayout.getEditText();
        TextInputLayout phoneNumberInputLayout = findViewById(R.id.phoneNumberInput);
        TextInputEditText phoneNumberInput = (TextInputEditText) phoneNumberInputLayout.getEditText();
        TextInputLayout emailInputLayout = findViewById(R.id.emailInput);
        TextInputEditText emailInput = (TextInputEditText) emailInputLayout.getEditText();
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInput);
        TextInputEditText passwordInput = (TextInputEditText) passwordInputLayout.getEditText();
        loginButton = findViewById(R.id.LoginButton);
        signUpButton = findViewById(R.id.SignUpButton);

        signUpButton.setOnClickListener(v -> {
                    String email = emailInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    String phoneNumber = phoneNumberInput.getText().toString();
                    String name = nameInput.getText().toString();
                    if (email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //check on phone number
                    String pattern_regex = "^[1-9]{1}[0-9]{9}";
                    Pattern pattern = Pattern.compile(pattern_regex);

                    if (!new GenericStringValidation<Pattern>(pattern).validateString(phoneNumber)) {
                        Toast.makeText(this, "only ten digit phone number is allowed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new UniquePhoneNumberThread(database, this, phoneNumber, mAuth, email, password, name).execute();
                });

            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            });
            if (ContextCompat.checkSelfPermission(this, permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                requestPermissions(new String[]{permission.ACTIVITY_RECOGNITION}, 0);
            }

            int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
            Log.d("MainActivity", "Permission State: " + permissionState + " == " + PackageManager.PERMISSION_DENIED);

            // If the permission is not granted, request it.
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }




