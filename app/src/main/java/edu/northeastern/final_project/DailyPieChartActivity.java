package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DailyPieChartActivity extends AppCompatActivity {

    private PieChart pieChart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_pie_chart);
        pieChart = findViewById(R.id.pieChart_daily);

        initPieChart();
        showPieChart();
    }


    private void initPieChart() {
        //using percentages as values instead of amount
        pieChart.setUsePercentValues(true);

        //remove the description label on the lower left corner
        pieChart.getDescription().setEnabled(false);

        //enable the user to rotate the chart
        pieChart.setRotationEnabled(true);

        //add friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f);

        //set the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0);

        //highlight the entry when tapped
        pieChart.setHighlightPerTapEnabled(true);

        //add animation so the entries pop up from 0 degree
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        //set the color of the hole in the middle
        pieChart.setHoleColor(Color.parseColor("#FFFFFF"));
    }

    private void showPieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();

        /*** TODO: change the data into firebase type data ***/
        typeAmountMap.put("Carbs", 1000);
        typeAmountMap.put("Protein", 500);
        typeAmountMap.put("Macros", 350);
        typeAmountMap.put("Fibers", 125);

        //initialing color
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#d63c31")); // carbs
        colors.add(Color.parseColor("#285ded")); // protein
        colors.add(Color.parseColor("#9128ed")); // macros
        colors.add(Color.parseColor("#79db37")); // fibers

        //input data and fit data int to pie chart entry
        for (String type: typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //show the value of the entries, default true if not set
        pieData.setDrawValues(true);

        pieChart.setData(pieData);
        pieChart.invalidate();

    }
}