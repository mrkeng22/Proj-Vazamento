package com.example.telalogin;
//gráfico linechart
import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.telalogin.databinding.ActivityDispositivosMenuBinding;
import com.github.mikephil.charting.charts.LineChart;
//fim gráfico linechart

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.github.mikephil.charting.data.*;

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
import com.github.mikephil.charting.utils.ColorTemplate;
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
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> items;
    private ArrayAdapter<String> dispositivosAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_period);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        items = new ArrayAdapter<String>(this, R.layout.activity_list_item);
        chart = findViewById(R.id.chart2);
        TabLayout tabLayout = findViewById(R.id.tabl);

        //============Dias da Semana===========
        labels.add("Domingo");
        labels.add("Segunda-feira");
        labels.add("Terça-feira");
        labels.add("Quarta-feira");
        labels.add("Quinta-feira");
        labels.add("Sexta-feira");
        labels.add("Sábado");

        //============Dias da Semana===========
        entries.add(new Entry(0f, 100f));
        entries.add(new Entry(1f, 200f));
        entries.add(new Entry(2f, 30f));
        entries.add(new Entry(3f, 20f));
        entries.add(new Entry(4f, 500f));
        entries.add(new Entry(5f, 600f));
        entries.add(new Entry(6f, 200f));
        configureGraphics();





        // Adiciona um listener para as abas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {



                switch (tab.getPosition()) {
                    case 0: // Semana
                        entries.clear();
                        labels.clear();
                        //============Dias da Semana===========
                        labels.add("Domingo");
                        labels.add("Segunda-feira");
                        labels.add("Terça-feira");
                        labels.add("Quarta-feira");
                        labels.add("Quinta-feira");
                        labels.add("Sexta-feira");
                        labels.add("Sábado");

                        //============Dias da Semana===========
                        entries.add(new Entry(0f, 100f));
                        entries.add(new Entry(1f, 200f));
                        entries.add(new Entry(2f, 30f));
                        entries.add(new Entry(3f, 20f));
                        entries.add(new Entry(4f, 500f));
                        entries.add(new Entry(5f, 600f));
                        entries.add(new Entry(6f, 200f));


                        break;
                    case 1: //
                        entries.clear();
                        labels.clear();

                        labels.add("Janeiro");
                        labels.add("Fevereiro");
                        labels.add("Março");
                        labels.add("Abril");
                        labels.add("Maio");
                        labels.add("Junho");
                        labels.add("Julho");
                        labels.add("Agosto");
                        labels.add("Setembro");
                        labels.add("Outubro");
                        labels.add("Novembro");
                        labels.add("Dezembro");
                        //============meses===========

                        entries.add(new Entry(0f, 10f));
                        entries.add(new Entry(1f, 2000f));
                        entries.add(new Entry(2f, 500f));
                        entries.add(new Entry(3f, 3000f));
                        entries.add(new Entry(4f, 40f));
                        entries.add(new Entry(5f, 5000f));
                        entries.add(new Entry(6f, 6000f));
                        entries.add(new Entry(7f, 400f));
                        entries.add(new Entry(8f, 8000f));
                        entries.add(new Entry(9f, 900f));
                        entries.add(new Entry(10f, 10f));
                        entries.add(new Entry(11f, 11000f));



                        break;
                    case 2: // Ano
                        entries.clear();
                        labels.clear();
                        labels.add("2023");
                        labels.add("2024");
                        labels.add("2025");
                        labels.add("2026");
                        labels.add("2027");

                        entries.add(new Entry(0f, 1000f));
                        entries.add(new Entry(1f, 2000f));
                        entries.add(new Entry(2f, 3000f));
                        entries.add(new Entry(3f, 4000f));
                        entries.add(new Entry(4f, 5000f));


                        break;

                }
                // Crie o formatter
                IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(labels);

                // Configure o eixo X
                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(formatter);
                xAxis.setGranularity(1f); // Para garantir que todos os rótulos sejam exibidos
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Posiciona os rótulos abaixo do gráfico
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
        chart.setTouchEnabled(true);
        chart.setEnabled(true);
//======================================Zoom in Graphic====================================
        chart.setPinchZoom(true); // Habilita o zoom por gestos
        chart.setScaleXEnabled(true); // Habilita o zoom no eixo X
        chart.setScaleYEnabled(true); // Habilita o zoom no eixo Y
        chart.setAutoScaleMinMaxEnabled(true);


//======================================Zoom in Graphic====================================
//======================================Color in Graphic====================================
        int color = ColorTemplate.JOYFUL_COLORS[0];
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.BLUE);
        dataSet.setValueTextSize(20f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setFillAlpha(100);
//======================================Color in Graphic====================================
        chart.invalidate(); // Atualiza o gráfico[

    }
}
