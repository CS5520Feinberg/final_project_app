package edu.northeastern.final_project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.util.concurrent.TimeUnit;


import edu.northeastern.final_project.activity.SocialMediaActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button settingsButton;
    private Button addDailyIntakeBtn;
    private ImageButton stepCounterShortcutBtn;
    private ImageButton recipeGeneratorShortcutBtn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        settingsButton = findViewById(R.id.SettingsButtonProfile);
        addDailyIntakeBtn = findViewById(R.id.AddDailyIntakeBtn);
        stepCounterShortcutBtn = findViewById(R.id.stepCounterShortcut);
        recipeGeneratorShortcutBtn = findViewById(R.id.recipeGeneratorShortcut);
        tabLayout = findViewById(R.id.TabLayoutProfile);
        viewPager = findViewById(R.id.ViewPagerProfile);

        adapter = new ViewPagerProfileAdapter(this);
        viewPager.setAdapter(adapter);

        addDailyIntakeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DailyIntakeActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        stepCounterShortcutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepCounterActivity.class);
            startActivity(intent);
        });

        recipeGeneratorShortcutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeGeneratorActivity.class);
            startActivity(intent);
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

        scheduler();
    }

    private void scheduler() {
        PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 80, TimeUnit.SECONDS).build();
        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
        Log.d("Schedule", "Worker scheduled");
    }

    public void launch_social_media(View view) {
        Intent intent = new Intent(ProfileActivity.this, SocialMediaActivity.class);
        startActivity(intent);
    }
}