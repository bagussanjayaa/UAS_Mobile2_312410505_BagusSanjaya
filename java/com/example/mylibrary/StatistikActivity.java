package com.example.mylibrary;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class StatistikActivity extends AppCompatActivity {

    BarChart barChart;
    DatabaseHelper db;
    ImageView btnBack;
    TextView tvInsight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_statistik);

        barChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.btnBack);
        tvInsight = findViewById(R.id.tvInsight);

        db = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());

        loadChart();
    }

    private void loadChart() {

        int belum = countStatus("BELUM");
        int sedang = countStatus("SEDANG");
        int selesai = countStatus("SELESAI");

        int total = belum + sedang + selesai;

        // ======================
        // DATA CHART
        // ======================
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, belum));
        entries.add(new BarEntry(1, sedang));
        entries.add(new BarEntry(2, selesai));

        BarDataSet dataSet = new BarDataSet(entries, "");

        dataSet.setColors(new int[]{
                Color.parseColor("#FF6B6B"), // merah soft
                Color.parseColor("#FFA726"), // orange
                Color.parseColor("#66BB6A")  // hijau
        });

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setHighLightAlpha(0);
        dataSet.setBarShadowColor(Color.TRANSPARENT);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        barChart.setData(data);

        // ======================
        // X AXIS
        // ======================
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Belum", "Sedang", "Selesai"}
        ));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // ======================
        // Y AXIS
        // ======================
        YAxis left = barChart.getAxisLeft();
        left.setGranularity(1f);
        left.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);

        // ======================
        // STYLE
        // ======================
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);

        barChart.getLegend().setEnabled(false);

        // animasi
        barChart.animateY(1200, Easing.EaseInOutQuart);

        barChart.invalidate();

        // ======================
        // INSIGHT LOGIC 🔥
        // ======================
        String insight;

        if (total == 0) {
            insight = "😅 Kamu belum punya buku.\nYuk tambahin dulu koleksinya!";
        }
        else if (belum > sedang && belum > selesai) {
            insight = "📚 Buku kamu masih banyak yang belum dibaca.\nYuk mulai luangin waktu buat baca!";
        }
        else if (sedang > belum && sedang > selesai) {
            insight = "👀 Lagi semangat baca nih!\nJangan lupa diselesaikan ya bukunya.";
        }
        else if (selesai > belum && selesai > sedang) {
            insight = "🔥 Keren! Kamu udah banyak menyelesaikan buku.\nPertahankan kebiasaan ini!";
        }
        else {
            insight = "✨ Progress kamu cukup seimbang.\nTetap konsisten ya!";
        }

        tvInsight.setText(insight);

        // animasi text biar smooth
        tvInsight.setAlpha(0f);
        tvInsight.animate().alpha(1f).setDuration(1000);
    }

    private int countStatus(String status) {
        Cursor c = db.getBooksByStatus(status);
        int count = c.getCount();
        c.close();
        return count;
    }
}