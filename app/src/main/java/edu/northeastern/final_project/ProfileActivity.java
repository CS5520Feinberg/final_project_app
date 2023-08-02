package edu.northeastern.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private Button settingsButton;
    private Button logOutButton;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        settingsButton = findViewById(R.id.SettingsButtonProfile);
        logOutButton = findViewById(R.id.LogOutButtonProfile);
        tabLayout = findViewById(R.id.TabLayoutProfile);
        viewPager = findViewById(R.id.ViewPagerProfile);

        adapter = new ViewPagerProfileAdapter(this);
        viewPager.setAdapter(adapter);

        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {

        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager, true, (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Calories");
                            break;
                        case 1:
                            tab.setText("Nutrition");
                            break;
                    }
                });
        tabLayoutMediator.attach();
    }
}