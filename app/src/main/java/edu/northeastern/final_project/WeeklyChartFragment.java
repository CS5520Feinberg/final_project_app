package edu.northeastern.final_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Random;

public class WeeklyChartFragment extends Fragment {

    private CombinedChart chart;
    final Integer itemcount = 12;

    private Random rand = new Random();

    public WeeklyChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_weekly_chart, container, false);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart = view.findViewById(R.id.weeklyCombinedChart);

        setupChart();
    }

    /*** reference: MPChartAndroid sample code ***/
    private void setupChart() {

        chart.setDrawOrder(new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        XAxis x = chart.getXAxis();
        x.setTextSize(14f);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis y = chart.getAxisLeft();
        y.setEnabled(false);

        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setVisibleXRangeMaximum(10f);
        chart.setVisibleXRangeMinimum(10f);
        chart.getDescription().setEnabled(false);

        CombinedData data = new CombinedData();

        data.setData(getLineData());
        data.setData(getBarData());
        chart.setData(data);
        chart.invalidate();

        Legend legend = chart.getLegend();
        legend.setTextSize(14f);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }

    private LineData getLineData() {

        /*** TODO - change the data by pulling from firebase ***/
        ArrayList<Entry> entries = new ArrayList<Entry>();

        LineData ld = new LineData();

        for (int index = 0; index< itemcount; index++) {
            entries.add(new Entry(index+0.5f, rand.nextInt(15)));
        }

        LineDataSet lset = new LineDataSet(entries, "Target Dataset");
        lset.setColor(Color.RED);
        lset.setLineWidth(2.5f);
        lset.setCircleColor(Color.RED);
        lset.setCircleSize(5f);
        lset.setFillColor(Color.WHITE);
        //lset.setDrawCubic(true);
        lset.setDrawValues(true);
        lset.setValueTextSize(10f);
        lset.setValueTextColor(Color.RED);
        lset.setAxisDependency(YAxis.AxisDependency.LEFT);

        ld.addDataSet(lset);

        Log.d("TAG", "lineData was created");

        return ld;
    }

    private BarData getBarData() {

        /*** TODO - change the data by pulling from firebase ***/
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < itemcount; i++) {
            entries.add(new BarEntry(i, rand.nextInt(15)));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "BarDataSet");

        barDataSet.setColor(Color.parseColor("#285ded"));

        BarData bd = new BarData(barDataSet);
        bd.addDataSet(barDataSet);

        Log.d("TAG", "BarData was created");

        return bd;
    }
}
