package edu.northeastern.final_project.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.adapter.CustomAdapter;
import edu.northeastern.final_project.backgroundThreadClass.GetContactsThread;

public class TestingRecyclerView extends AppCompatActivity {
    RecyclerView contactsRV;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.testing_layout);

        contactsRV = findViewById(R.id.recylerView);
        contactsRV.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> list = new ArrayList<String>();
        list.add("Pink");
        adapter = new CustomAdapter(list);
        contactsRV.setAdapter(adapter);


    }
}
