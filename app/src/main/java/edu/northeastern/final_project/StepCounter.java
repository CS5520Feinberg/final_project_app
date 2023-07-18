package edu.northeastern.final_project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounter  {
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("Acc", "sensorChanged");
            float[] values = event.values;
            int value = -1;

            if (values.length > 0) {
                value = (int) values[0];
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
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            Log.d("Acc", "stepSensor is null");
        } else {
            Log.d("Acc", "Registering listener");
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

}
