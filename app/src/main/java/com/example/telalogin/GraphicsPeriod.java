package com.example.telalogin;
//gráfico linechart
import android.os.Bundle;

import com.example.telalogin.databinding.ActivityDispositivosMenuBinding;
import com.github.mikephil.charting.charts.LineChart;
//fim gráfico linechart

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import java.util.List;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
//Widget
import android.widget.TableLayout;


//gráfico linechart
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
//fim gráfico linechart

import java.util.ArrayList;
import android.widget.Toast;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.firebase.database.snapshot.Index;

import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import java.util.List;
//Widget
import android.widget.TableLayout;
public class GraphicsPeriod extends AppCompatActivity {

    private LineChart chart;
    private ActivityDispositivosMenuBinding binding;
    List<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    private ArrayAdapter<String> dispositivosAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_period);

        chart = findViewById(R.id.chart2);
        TabLayout tabLayout = findViewById(R.id.tabl);


        TabLayout semana = findViewById(R.id.Semana);



        // Adiciona um listener para as abas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Semana
                        entries.add(new Entry(0, 1));
                        entries.add(new Entry(1, 2));
                        entries.add(new Entry(2, 0));
                        break;
                    case 1: // Mês
                        labels.add("Janeiro");
                        labels.add("Fevereiro");
                        labels.add("Março");
                        entries.add(new Entry(0f, 10f)); // "Label1"
                        entries.add(new Entry(1f, 20f)); // "Label2"
                        entries.add(new Entry(2f, 15f));
                        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(labels);
                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(formatter);
                        break;
                    case 2: // Ano
                        entries.add(new Entry(2024, 1));

                        break;
                }
                configureGraphics();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Não faça nada aqui
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Não faça nada aqui
            }
        });
    }

    public void configureGraphics() {


        LineDataSet dataSet = new LineDataSet(entries, "valores");
        dataSet.setColor(Color.BLUE); // Personalize a cor da linha
        dataSet.setValueTextColor(Color.BLACK); // Cor dos valores

        LineData data = new LineData(dataSet);
        chart.setData(data);

        // Configurar eixo X e Y
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);  // Desabilita o eixo direito

        chart.invalidate(); // Atualiza o gráfico
    }
}
