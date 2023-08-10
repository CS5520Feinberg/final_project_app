package edu.northeastern.final_project;

import android.content.ComponentName;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StepCounterActivity extends AppCompatActivity {

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    private final int SAMPLING_RATE = 100;
    // Maximum amount of time to save in queue
    // private final int MAX_TIME = 30;
    private final int MAX_TIME = 1;
    // Calculating the time between samples
    private int SAMPLING_TIME = 1 / SAMPLING_RATE;
    // Calculating the number of elements to keep in the queue
    private int NUM_ELEMENTS = MAX_TIME * SAMPLING_RATE;
    // 1-second window for step counting
    private int WINDOW_SIZE = (int) Math.ceil(0.5 * SAMPLING_RATE);
    private int WRITE_DURATION = 10;
    private SignalContainer signalContainer = new SignalContainer(NUM_ELEMENTS);
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final double thresh = 10;
    private static final int delay = 200; //ms
    private int stepCount;
    private DBHandler dbHandler;
    private int lastSteps = 0;
    private Instant lastWriteTime = Instant.now();
    private TextView stepCountTextView;
    private StepCounterService stepCounterService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCounterService.LocalBinder binder = (StepCounterService.LocalBinder) service;
            stepCounterService = binder.getService();
            isBound = true;
            Log.d("StepCounter", "Bounded");
            updateStepCount();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateStepCount();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        Log.d("Acc", "Initializing Sensor");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (accelerometer == null) {
            Log.d("Acc", "accelerometer is null");
        } else {
            Log.d("Acc", "Registering listener");
            sensorManager.registerListener(this, accelerometer, SAMPLING_TIME);
        }

        dbHandler = new DBHandler(StepCounterActivity.this);

        ZonedDateTime gmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = gmt.format(formatter);
        String datePart = formattedTime.split(" ")[0];

        HashMap<String, Integer> dailySteps = dbHandler.getDailySteps();

        stepCount = dailySteps.getOrDefault(datePart, 0);
        lastSteps = stepCount;
        Log.d("Acc", "Initializing stepCount as: " + stepCount);
      
        stepCountTextView = findViewById(R.id.stepCountTextView);
        stepCountTextView.setText("Steps: " + stepCount);

        Intent intent = new Intent(this, StepCounterService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        handler.post(updateRunnable);
        Log.d("StepCounter", "Initializing StepCounterActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateStepCount() {
        if (isBound) {
            int stepCount = stepCounterService.getStepCount();
            stepCountTextView.setText("Steps: " + stepCount);

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        double currentAccel = Math.sqrt(x * x + y * y + z * z);

        data.addEntry(new Entry(set.getEntryCount(), (float) currentAccel), 0);
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(150);
        chart.moveViewToX(data.getEntryCount());

        boolean procReady = signalContainer.add(currentAccel);

        if (procReady) {
            int num_steps = signalContainer.findPeaks(WINDOW_SIZE, thresh);
            stepCount += num_steps;
            stepCountTextView.setText("Steps: " + stepCount);
            signalContainer.clear();

            Instant curInstant = Instant.now();
            Duration timeDiff = Duration.between(lastWriteTime, curInstant);
            int diffSteps = stepCount - lastSteps;

            if (timeDiff.getSeconds() > WRITE_DURATION || diffSteps > 100) {
                dbHandler.addSteps(diffSteps);
                lastWriteTime = Instant.now();
                lastSteps = stepCount;
            }
        }
    }
}
