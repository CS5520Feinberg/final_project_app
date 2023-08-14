package edu.northeastern.final_project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Duration;
import java.time.Instant;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SignalContainer signalContainer;
    private int stepCount = 0;
    private DBHandler dbHandler;
    private int lastSteps = 0;
    private Instant lastWriteTime = Instant.now();
    private final int SAMPLING_RATE = 100;
    private final int MAX_TIME = 1;
    private int SAMPLING_TIME = 1 / SAMPLING_RATE;
    // Calculating the number of elements to keep in the queue
    private int NUM_ELEMENTS = MAX_TIME * SAMPLING_RATE;
    // 1-second window for step counting
    private int WINDOW_SIZE = (int) Math.ceil(0.5 * SAMPLING_RATE);
    private int WRITE_DURATION = 10;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final double thresh = 7.5;


    // Test
    private static final int NOTIFICATION_ID = 1;
    static final String ACTION_STEP_COUNT_UPDATED = "edu.northeastern.final_project.ACTION_STEP_COUNT_UPDATED";


    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        StepCounterService getService() {
            return StepCounterService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StepCounter", "onStartCommand called");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        signalContainer = new SignalContainer(NUM_ELEMENTS);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        dbHandler = new DBHandler(StepCounterService.this, uid);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("StepCounter", "Sensor listener set");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        double currentAccel = Math.sqrt(x * x + y * y + z * z);

        boolean procReady = signalContainer.add(currentAccel);
        if (procReady) {
            int num_steps = signalContainer.findPeaks(WINDOW_SIZE, thresh);
            int prevStepCount = stepCount;
            stepCount += num_steps;
            signalContainer.clear();

            if (prevStepCount != stepCount) {
                notifyStepCountChanged();
            }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getStepCount() {
        return stepCount;
    }

    private void notifyStepCountChanged() {
        Intent intent = new Intent(ACTION_STEP_COUNT_UPDATED);
        intent.putExtra("stepCount", stepCount);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
