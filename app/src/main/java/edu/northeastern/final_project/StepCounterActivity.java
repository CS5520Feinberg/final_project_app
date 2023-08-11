package edu.northeastern.final_project;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class StepCounterActivity extends AppCompatActivity {

    private StepCounterService stepCounterService;
    private boolean bound = false;
    private TextView stepCountTextView;

    private BroadcastReceiver stepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int stepCount = intent.getIntExtra("stepCount", 0);
            updateStepCount(stepCount);
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCounterService.LocalBinder binder = (StepCounterService.LocalBinder) service;
            stepCounterService = binder.getService();
            bound = true;
            updateStepCount(stepCounterService.getStepCount());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountTextView = findViewById(R.id.stepCountTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(stepCountReceiver,
                new IntentFilter(StepCounterService.ACTION_STEP_COUNT_UPDATED));
        Intent intent = new Intent(this, StepCounterService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepCountReceiver);
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateStepCount(int steps) {
        if (bound) {
            runOnUiThread(() -> {
                Log.d("StepCounter", "Steps: " + stepCounterService.getStepCount());
                stepCountTextView.setText("Steps: " + steps);
            });
        }
    }
}
