package edu.northeastern.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.northeastern.final_project.R;

public class SocialMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle SavedInstancesState) {
        super.onCreate(SavedInstancesState);
        setContentView(R.layout.social_media_activity);
       FloatingActionButton floatingButton =  findViewById(R.id.floatingActionButton);
       floatingButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(SocialMediaActivity.this, AddFriendsActivity.class);
               startActivity(intent);
           }
       });
    }
}
