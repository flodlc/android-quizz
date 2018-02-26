package com.quizz.stat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.quizz.R;
import com.quizz.entities.Stat;
import com.quizz.entities.User;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        getData();
    }

    private void getData() {
        ApiServiceInterface apiService = ApiService.getService();
        Call<Stat> call = apiService.getStats();
        call.enqueue(new Callback<Stat>() {
            @Override
            public void onResponse(Call<Stat> call, Response<Stat> response) {
                if (ApiService.checkCode(StatisticActivity.this, response)) {
                    Stat stat = response.body();
                    displayRates(stat.getMyStats(), getResources().getString(R.string.myAnswers), R.id.chart);
                    displayRates(stat.getGlobalStats(), getResources().getString(R.string.allAnswers), R.id.chart2);
                    displayBarCHart(stat.getUsers());
                }
                hideLoader();
            }

            @Override
            public void onFailure(Call<Stat> call, Throwable t) {
                ApiService.showErrorMessage(StatisticActivity.this);
                hideLoader();
            }
        });
    }

    private List<BarEntry> makeBarEntries(List<User> users) {
        List<BarEntry> entries = new ArrayList<>();

        int nbEntries = 0;
        for (User user : users) {
            if (nbEntries >= getResources().getInteger(R.integer.maxNbOfBestUsers)) {
                break;
            }
            entries.add(new BarEntry(nbEntries, user.getRecord()));
            nbEntries++;
        }
        return entries;
    }

    private List<String> makeLabels(List<User> users) {
        List<String> labels = new ArrayList<>();

        int nbEntries = 0;
        for (User user : users) {
            if (nbEntries >= getResources().getInteger(R.integer.maxNbOfBestUsers)) {
                break;
            }
            labels.add(user.getUsername());
            nbEntries++;
        }
        return labels;
    }

    private void displayBarCHart(final List<User> users) {
        BarChart chart = (BarChart) findViewById(R.id.chart3);

        List<BarEntry> entries = makeBarEntries(users);
        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        set.setDrawValues(true);
        set.setValueTextSize(16);
        set.setColors(new int[]{R.color.colorGreen}, getBaseContext());
        BarData data = new BarData(set);
        data.setDrawValues(true);
        data.setBarWidth(0.6f);
        chart.setDescription(null);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setLabelCount(entries.size());
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextSize(16);

        chart.getAxisLeft().setAxisMinimum(0);
        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(),
                chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));

        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value % 1 == 0) {
                    return UtilFunctions.makeMultilineString(makeLabels(users).get((int) value), 8);
                } else {
                    return "";
                }
            }
        });

        chart.setExtraBottomOffset(35);
        chart.setData(data);
        chart.animateY(800);
        chart.getLegend().setEnabled(false);
        chart.invalidate(); // refresh
    }

    private void displayRates(Double goodRates, String text, int chartId) {
        if (goodRates == null) {
            return;
        }
        PieChart chart = (PieChart) findViewById(chartId);
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(goodRates.floatValue()));
        entries.add(new PieEntry((1f - goodRates.floatValue())));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors((new int[]{R.color.colorButton, R.color.colorRed}), getBaseContext());
        dataSet.setValueTextSize(16);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());

        chart.setDescription(null);
        chart.setTouchEnabled(false);
        chart.setRotationAngle(90 + ((1 - goodRates.floatValue()) * 180));
        chart.setData(pieData);
        chart.setCenterText(text);
        chart.setUsePercentValues(true);
        chart.setCenterTextSize(16);
        chart.setEntryLabelTextSize(16);
        chart.getLegend().setEnabled(false);
        chart.animateX(800);
        chart.invalidate();
    }

    private void hideLoader() {
        findViewById(R.id.loader).setVisibility(View.GONE);
        findViewById(R.id.chartDisplay).setVisibility(View.VISIBLE);
    }
}
