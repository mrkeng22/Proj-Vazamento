package com.example.telalogin;
//gráfico linechart
import android.annotation.SuppressLint;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
//fim gráfico linechart
import java.util.List;
import java.util.ArrayList;
import com.github.mikephil.charting.data.Entry;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;



public class GraphicsPeriod extends AppCompatActivity implements View.OnClickListener{
    private GraphicsValues valoresxy;
    ArrayList<Entry> valores;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics_period);
        Button buttonSemana = findViewById(R.id.Semana);
        Button buttonMes = findViewById(R.id.Mes);
        Button buttonAno = findViewById(R.id.Ano);
        //definir o mesmo onclick
        buttonSemana.setOnClickListener(this);
        buttonMes.setOnClickListener(this);
        buttonAno.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Semana) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();//aqui sera adicionado a lista dos valores para o grafico
        } else if (v.getId() == R.id.Mes) {
            Toast.makeText(this, "lista de valores de x e y referente ao mes", Toast.LENGTH_SHORT).show();//aqui sera adicionado a lista dos valores para o grafico
        } else if (v.getId() == R.id.Ano) {
            Toast.makeText(this, "lista dos valores de x e y referendte ao ano", Toast.LENGTH_SHORT).show();//aqui sera adicionado a lista dos valores para o grafico
        }
    }
}

