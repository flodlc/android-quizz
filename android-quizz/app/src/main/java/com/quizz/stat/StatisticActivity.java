package com.quizz.stat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.quizz.R;
import com.quizz.offline.OfflineSelectorActivity;
import com.quizz.online.OnlineSelectorActivity;

import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Florian on 31/01/2018.
 */

public class StatisticActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Bundle b = new Bundle();
        User user = getIntent().getExtras().getParcelable("user");
        b.putParcelable("user", user);

        displayChart();
        displayBarCHart();
    }

    private void displayBarCHart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 150, "test"));
        entries.add(new BarEntry(1, 253, "test"));
        entries.add(new BarEntry(2, 38, "test"));
        entries.add(new BarEntry(3, 145, "test"));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        BarData data = new BarData(set);
        data.setDrawValues(true);

        data.setBarWidth(0.5f);

        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.chart3);

        chart.setDescription(null);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawAxisLine(true);
        chart.getXAxis().setDrawAxisLine(false);

        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setDrawLabels(true);


        final ArrayList<String> xVals = new ArrayList();

        xVals.add("Tom");
        xVals.add("Bob");
        xVals.add("Marc");
        xVals.add("Flo");

        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setLabelCount(4);

        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals.get((int) value);
            }

        });

        chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        chart.getXAxis().setTextSize(15);

        chart.setData(data);
        chart.getLegend().setEnabled(false);
        chart.invalidate(); // refresh
    }

    private void displayChart() {
        List<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(30f, "Juste"));
        entries.add(new PieEntry(22f, "Faux"));
        PieDataSet dataSet = new PieDataSet(entries, ""); // add entries to dataset
        dataSet.setColors((new int[] { R.color.colorButton, R.color.colorRed}), getBaseContext());
        dataSet.setValueTextSize(16);

        List<PieEntry> entries2 = new ArrayList<PieEntry>();
        entries2.add(new PieEntry(5f, "Juste"));
        entries2.add(new PieEntry(2f, "Faux"));
        PieDataSet dataSet2 = new PieDataSet(entries2, ""); // add entries to dataset
        dataSet2.setColors((new int[] { R.color.colorButton, R.color.colorRed}), getBaseContext());
        dataSet2.setValueTextSize(16);


        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());

        PieData pieData2 = new PieData(dataSet2);
        pieData2.setValueFormatter(new PercentFormatter());

        PieChart chart = (PieChart) findViewById(R.id.chart);
        PieChart chart2 = (PieChart) findViewById(R.id.chart2);


        chart.setDescription(null);
        chart.setData(pieData);
        chart.setCenterText("Mes statistiques");
        chart.setUsePercentValues(true);
        chart.setCenterTextSize(16);
        chart.setEntryLabelTextSize(16);
        chart.getLegend().setEnabled(false);
        chart.animateX(800);

        chart2.setData(pieData2);
        chart2.setDescription(null);
        chart2.setCenterText("Statistiques générales");
        chart2.setUsePercentValues(true);
        chart2.setCenterTextSize(16);
        chart2.setEntryLabelTextSize(16);
        chart2.getLegend().setEnabled(false);
        chart2.animateX(800);

        chart.invalidate();
        chart2.invalidate();
    }
}
