package com.example.telalogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telalogin.databinding.ActivityDispositivosMenuBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraphicsPeriod extends AppCompatActivity {
    private LineChart chart;
    private DatabaseReference databaseRef;
    private String dispositivoId;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    List<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    private String deviceId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_period);

        Intent intent = getIntent();
        if (intent != null) {
            dispositivoId = intent.getStringExtra("deviceID");
            if (dispositivoId != null) {
                deviceId = dispositivoId;
                chart = findViewById(R.id.chart2);
                TabLayout tabLayout = findViewById(R.id.tabl);
                ToggleButton toggleButton = findViewById(R.id.toggleButton);

                toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    carregarDadosFirebase(isChecked ? "pressure" : "flow");
                });

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        carregarDadosFirebase(toggleButton.isChecked() ? "pressure" : "flow");
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });
            } else {
                Toast.makeText(this, "Erro: ID do dispositivo não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Erro: Intent não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveDataToFirebase(String tipo, float valor) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("devices/" + deviceId + "/" + tipo);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        String currentTime = timeFormat.format(new java.util.Date());

        Map<String, Object> data = new HashMap<>();
        data.put(currentTime, valor);

        databaseRef.child(currentDate).updateChildren(data)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Data saved successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error saving data", e));
    }

    private void carregarDadosFirebase(String tipo) {
        entries.clear();
        labels.clear();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("devices/" + deviceId + "/" + tipo);
        databaseRef.child("semana").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String dia = data.getKey();
                    Float valor = data.getValue(Float.class);
                    labels.add(dia);
                    entries.add(new Entry(index++, valor != null ? valor : 0));
                }
                configureGraphics();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading data", error.toException());
            }
        });
    }

    private void configureGraphics() {
        LineDataSet dataSet = new LineDataSet(entries, "Valores");
        dataSet.setColor(ColorTemplate.JOYFUL_COLORS[0]);
        dataSet.setValueTextColor(ColorTemplate.COLORFUL_COLORS[1]);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(100);

        LineData data = new LineData(dataSet);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setScaleEnabled(true);
        chart.invalidate();
    }
}
