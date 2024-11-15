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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraphicsPeriod extends AppCompatActivity {
    private LineChart chart;
    private FirebaseDatabase historicalDatabase; // Banco de dados para dados históricos e semanais
    private String dispositivoId;
    private String deviceId;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    List<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_period);

        // Configurar o segundo Firebase para dados históricos
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:624964546444:android:e319f0d8fde02a6acab0a9") // Application ID do segundo projeto
                .setApiKey("AIzaSyCZ41JSMFsOIndDUzt2GKoE3rvMcqEAw9I") // API Key do segundo projeto
                .setDatabaseUrl("https://console.firebase.google.com/project/data-hydro/settings/general/android:com.hydro.com?hl=pt-br") // URL do banco de dados do segundo projeto
                .build();

        FirebaseApp historicalApp;
        try {
            historicalApp = FirebaseApp.initializeApp(this, options, "historicalFirebase");
        } catch (IllegalStateException e) {
            historicalApp = FirebaseApp.getInstance("historicalFirebase");
        }
        historicalDatabase = FirebaseDatabase.getInstance(historicalApp);

        // Inicialize deviceId com o ID do dispositivo, por exemplo:
        deviceId = "deviceId1";

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

    // Método para salvar dados históricos por ano e mês e resetar dados semanais
    private void saveDataToHistoricalFirebase(String tipo, float valor) {
        // Obter ano, mês e dia da semana atuais
        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String currentDayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());

        // Salvar dados mensais e anuais acumulados
        DatabaseReference monthRef = historicalDatabase.getReference("historical_data/" + deviceId + "/" + tipo + "/" + currentYear + "/" + currentMonth);
        monthRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Float total = mutableData.child("total").getValue(Float.class);
                Integer entries = mutableData.child("entries").getValue(Integer.class);
                if (total == null) total = 0f;
                if (entries == null) entries = 0;

                total += valor;
                entries += 1;

                mutableData.child("total").setValue(total);
                mutableData.child("entries").setValue(entries);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.d("Firebase", "Monthly historical data updated successfully");
                } else {
                    Log.e("Firebase", "Error updating monthly historical data", databaseError.toException());
                }
            }
        });

        // Salvar dados semanais
        DatabaseReference weeklyRef = historicalDatabase.getReference("weekly_data/" + deviceId + "/" + tipo + "/" + currentDayOfWeek);
        weeklyRef.setValue(valor)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Weekly data saved successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error saving weekly data", e));

        // Resetar dados semanais se hoje for segunda-feira
        if (currentDayOfWeek.equals("Segunda")) {
            clearWeeklyData(tipo);
        }
    }

    // Método para limpar dados semanais ao iniciar um novo ciclo de semana
    private void clearWeeklyData(String tipo) {
        DatabaseReference weeklyRef = historicalDatabase.getReference("weekly_data/" + deviceId + "/" + tipo);
        weeklyRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Weekly data cleared successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error clearing weekly data", e));
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
