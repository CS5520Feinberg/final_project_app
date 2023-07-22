package edu.northeastern.final_project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Queue;

public class StepCounter  {

    // Samping rate in Hz
    private final int SAMPLING_RATE = 100;
    // Maximum amount of time to save in queue
    // private final int MAX_TIME = 30;
    private final int MAX_TIME = 1;
    // Calculating the time between samples
    private int SAMPLING_TIME = 1 / SAMPLING_RATE;
    // Calculating the number of elements to keep in the queue
    private int NUM_ELEMENTS = MAX_TIME * SAMPLING_RATE;
    private SignalContainer signalContainer = new SignalContainer(NUM_ELEMENTS);
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("Acc", "sensorChanged");
            float[] values = event.values;
            double value = -1;

            if (values.length > 0) {
                value = (double) values[0];
                boolean procReady = signalContainer.add(value);
                Log.d("Acc", "procReady: " + procReady);

                if (procReady) {
                    Log.e("Acc", "Ready to Process!");
                    signalContainer.clear();
                }
            }


            Log.d("Acc", "Values: " + value);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            return;
        }
    };
    private SensorManager sensorManager;

    public StepCounter(Context applicationContext) {
        Log.d("Acc", "Initializing Sensor");
        sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (stepSensor == null) {
            Log.d("Acc", "stepSensor is null");
        } else {
            Log.d("Acc", "Registering listener");
            sensorManager.registerListener(sensorEventListener, stepSensor, SAMPLING_TIME);
        }
    }

}
