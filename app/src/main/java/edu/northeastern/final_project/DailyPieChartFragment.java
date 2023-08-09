package edu.northeastern.final_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DailyPieChartFragment extends Fragment {

    private PieChart pieChart;
    private DBHandler dbHandler;
    private ArrayList<Intake> dailyIntake;
    private HashMap<String, Float> dailyMacros;

    public DailyPieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_daily_pie_chart, container, false);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart_daily);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextSize(10f);
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    String label = ((PieEntry) e).getLabel();
                    Float value = dailyMacros.get(label.toLowerCase());
                    if (value != null) {
                        if (label.equals("Calories")) {
                            pieChart.setCenterText(label + ": " + value + " kcal");
                        } else {
                            pieChart.setCenterText(label + ": " + value + " g");
                        }
                        pieChart.invalidate();
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                pieChart.setCenterText("");
                pieChart.invalidate();
            }
        });

        initPieChart();

        dbHandler = new DBHandler(getContext());

        refreshPieChart();
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshPieChart();
    }

    private void refreshPieChart() {
        initPieChart();

        dailyIntake = dbHandler.readDailyIntake();
        dailyMacros = dbHandler.getDailyMacros(dailyIntake);
        showPieChart(dailyMacros);
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

    private void showPieChart(HashMap<String, Float> macrosMap) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Float> typeAmountMap = new HashMap<>();

        /*** TODO: change the data into firebase type data ***/
        typeAmountMap.put("Carbs", macrosMap.get("carbs"));
        typeAmountMap.put("Protein", macrosMap.get("protein"));
        typeAmountMap.put("Fats", macrosMap.get("fats"));
        typeAmountMap.put("Calories", macrosMap.get("calories"));

        //Log.d("DailyPieChartFragment", "carbs: " + typeAmountMap.get("Carbs"));
        //Log.d("DailyPieChartFragment", "protein: " + typeAmountMap.get("Protein"));
        //Log.d("DailyPieChartFragment", "fats: " + typeAmountMap.get("Fat"));
        //Log.d("DailyPieChartFragment", "calories: " + typeAmountMap.get("Calories"));

        //initialing color
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#fd0085")); // carbs
        colors.add(Color.parseColor("#ffbf7b")); // fats
        colors.add(Color.parseColor("#9128ed")); // protein
        colors.add(Color.parseColor("#F44336")); // cals

        //input data and fit data int to pie chart entry
        for (String type: typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type), type));
            //Log.d("DailyPieChartFragment", String.valueOf(typeAmountMap.get(type).floatValue()));
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
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }
}
