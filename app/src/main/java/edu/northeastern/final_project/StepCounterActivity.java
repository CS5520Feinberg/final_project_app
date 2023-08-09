package edu.northeastern.final_project;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
    private int WINDOW_SIZE = 1 * SAMPLING_RATE;
    private SignalContainer signalContainer = new SignalContainer(NUM_ELEMENTS);
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final double thresh = 10;
    private static final int delay = 200; //ms
    private long lastTime;
    private double lastAccel;
    private int stepCount;
    private TextView stepCountTextView;
    private LineChart chart;
    private LineData data;
    private LineDataSet set;

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

        lastTime = System.currentTimeMillis();
        lastAccel = 0.0;
        stepCount = 0;

        stepCountTextView = findViewById(R.id.stepCountTextView);

        chart = findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        data = new LineData();
        set = new LineDataSet(null, "Readings");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLUE);
        data.addDataSet(set);
        chart.setData(data);

        //chart.setVisibleXRange(150, 150);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SAMPLING_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        double currentAccel = Math.sqrt(x * x + y * y + z * z);
        double accelDiff = Math.abs(lastAccel - currentAccel);

        data.addEntry(new Entry(set.getEntryCount(), (float) currentAccel), 0);
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(150);
        chart.moveViewToX(data.getEntryCount());

        boolean procReady = signalContainer.add(currentAccel);

        if (procReady) {
            int num_steps = signalContainer.findPeaks(WINDOW_SIZE, thresh);
            Log.d("Acc", "Num steps: " + String.valueOf(num_steps));
            stepCount += num_steps;
            stepCountTextView.setText("Steps: " + stepCount);
            // if (accelDiff > thresh) {
            //     stepCount++;
            //     stepCountTextView.setText("Steps: " + stepCount);
            // }
            signalContainer.clear();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
