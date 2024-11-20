package com.example.telalogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telalogin.databinding.ActivityGraphicsPeriodBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GraphicsPeriod extends AppCompatActivity {

    private ActivityGraphicsPeriodBinding binding;
    private LineChart chart;
    private FirebaseDatabase historicalDatabase;
    private String dispositivoId;
    private String deviceId;
    private List<Entry> entries = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Infla o layout com DataBinding
        binding = ActivityGraphicsPeriodBinding.inflate(getLayoutInflater());


        // Configuração do Firebase
        historicalDatabase = FirebaseDatabase.getInstance();

        // Inicialize deviceId com o ID do dispositivo, por exemplo:
        deviceId = "deviceId1";

        Intent intent = getIntent();
        if (intent != null) {
            dispositivoId = intent.getStringExtra("deviceID");
            if (dispositivoId != null) {
                deviceId = dispositivoId;
                chart = binding.chart2;  // Usando o binding para acessar o LineChart
                TabLayout tabLayout = binding.tabl;  // Usando o binding para acessar o TabLayout
                ToggleButton toggleButton = binding.toggleButton;  // Usando o binding para acessar o ToggleButton

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
            Toast.makeText(this, "Erro: Intent não encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void carregarDadosFirebase(String tipo) {
        entries.clear();
        labels.clear();

        DatabaseReference databaseRef = historicalDatabase.getReference("weekly_data/" + deviceId + "/" + tipo);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e("Firebase", "Erro ao carregar dados", error.toException());
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
        chart.invalidate();
    }
}