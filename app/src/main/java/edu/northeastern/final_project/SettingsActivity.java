package edu.northeastern.final_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private Button logoutBtn, doneBtn, deleteAccBtn, changePasswordBtn;
    private Switch notificationSwitch, darkModeSwitch;
    private TextInputEditText weeklyDailyGoal;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutBtn = findViewById(R.id.LogoutButtonSettings);
        doneBtn = findViewById(R.id.doneBtn);
        deleteAccBtn = findViewById(R.id.deleteAccBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        notificationSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        weeklyDailyGoal = findViewById(R.id.weeklyDailyGoalTIET);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("SettingsActivity", "No user found!");
        }

        String uid = currentUser.getUid();
        DBHandler dbHandler = new DBHandler(SettingsActivity.this, uid);
        Integer goal = dbHandler.readWeeklyDailyGoal();
        weeklyDailyGoal.setText(String.valueOf(goal));

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
        });

        doneBtn.setOnClickListener(v -> {
            Integer newGoal = Integer.parseInt(weeklyDailyGoal.getText().toString());
            if (!newGoal.equals(goal)) {
                Toast.makeText(this, "Weekly daily goal updated.", Toast.LENGTH_SHORT).show();
                dbHandler.updateWeeklyGoal(newGoal);
            }
            finish();
        });

        deleteAccBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Please enter your password to confirm deletion.");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                String password = input.getText().toString();
                reAuthenticateAndDelete(password);
            });

            builder.setNegativeButton("Cancel", ((dialog, which) -> {
                dialog.cancel();
            }));

            builder.show();
        });

        changePasswordBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Password");

            final EditText oldPasswordInput = new EditText(this);
            oldPasswordInput.setHint("Old Password");
            oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            final EditText newPasswordInput = new EditText(this);
            newPasswordInput.setHint("New Password");
            newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            final EditText newPasswordInputConfirm = new EditText(this);
            newPasswordInputConfirm.setHint("Re-enter New Password");
            newPasswordInputConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(oldPasswordInput);
            layout.addView(newPasswordInput);
            layout.addView(newPasswordInputConfirm);
            builder.setView(layout);

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String newPasswordConfirm = newPasswordInputConfirm.getText().toString();

                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(SettingsActivity.this, "New password must be different from the old password", Toast.LENGTH_SHORT).show();
                } else if (newPassword.equals(newPasswordConfirm)) {
                    changePassword(oldPassword, newPassword);
                } else {
                    Toast.makeText(SettingsActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", ((dialog, which) -> {
                dialog.cancel();
            }));

            builder.show();
        });

        int darkModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkModeSwitch.setChecked(darkModeFlags == Configuration.UI_MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }));
    }

    private void reAuthenticateAndDelete(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        } else {
                            Toast.makeText(SettingsActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(aVoid -> {
                                        Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SettingsActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(SettingsActivity.this, "Incorrect old password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}