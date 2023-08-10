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

public class StepCounterService extends Service implements SensorEventListener {
    private final int SAMPLING_RATE = 100;
    private final int MAX_TIME = 1;
    private int SAMPLING_TIME = 1 / SAMPLING_RATE;
    private int NUM_ELEMENTS = MAX_TIME * SAMPLING_RATE;
    private int WINDOW_SIZE = (int) Math.ceil(0.5 * SAMPLING_RATE);
    private SignalContainer signalContainer = new SignalContainer(NUM_ELEMENTS);
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final double thresh = 10;
    private int stepCount = 0;
    private long lastTime;
    private double lastAccel;

    public class LocalBinder extends Binder {
        StepCounterService getService() {
            return StepCounterService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("StepCounter", "Initializing Sensor");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (accelerometer == null) {
            Log.d("StepCounter", "accelerometer is null");
        } else {
            Log.d("StepCounter", "Registering listener");
            sensorManager.registerListener(this, accelerometer, SAMPLING_TIME);
        }

        lastTime = System.currentTimeMillis();
        lastAccel = 0.0;
        stepCount = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        double currentAccel = Math.sqrt(x * x + y * y + z * z);
        double accelDiff = Math.abs(lastAccel - currentAccel);

        boolean procReady = signalContainer.add(currentAccel);

        if (procReady) {
            int num_steps = signalContainer.findPeaks(WINDOW_SIZE, thresh);
            Log.d("StepCounter", "Num steps: " + String.valueOf(num_steps));
            stepCount += num_steps;
            signalContainer.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getStepCount() {
        return stepCount;
    }
}
