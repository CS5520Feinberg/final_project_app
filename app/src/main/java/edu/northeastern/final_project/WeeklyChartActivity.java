package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.google.firebase.database.collection.LLRBNode;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeeklyChartActivity extends AppCompatActivity {

    private CombinedChart chart;
    final Integer itemcount = 12;

    private Random rand = new Random();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_chart);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        chart = findViewById(R.id.weeklyCombinedChart);

        setupChart();

    }

    /*** reference: https://stackoverflow.com/questions/33240002/mpandroidchart-combined-chart-bar-line-with-space***/
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

        /*** TODO - change the data by pulling from firebase***/
        ArrayList<Entry> entries = new ArrayList<Entry>();
        //ArrayList<String> labels = new ArrayList<String>();

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
        //ArrayList<String> labels = new ArrayList<String>();
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