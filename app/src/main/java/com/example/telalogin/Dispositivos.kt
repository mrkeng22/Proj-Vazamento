package com.example.telalogin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class Dispositivos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos)

        // Configurar o BarChart
        val barChart: BarChart = findViewById(R.id.barChart)

        // Criar os dados para o gráfico de barras
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 20f))
        entries.add(BarEntry(3f, 30f))

        val dataSet = BarDataSet(entries, "Label") // Adicione um rótulo aos dados
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate() // Atualiza o gráfico

        // Configurar o ajuste de padding para system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ronaldo)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
