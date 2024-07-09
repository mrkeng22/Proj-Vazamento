package com.example.telalogin

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Dispositivos : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var textViewPressao: TextView
    private lateinit var textViewVazao: TextView
    private lateinit var chart: LineChart
    private val pressureEntries = LinkedList<Entry>()
    private val flowEntries = LinkedList<Entry>()
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000 // 5 segundos
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos)

        // Recuperar o ID do dispositivo selecionado dos argumentos
        val dispositivoId = intent.getStringExtra("deviceID")

        // Inicializar as visualizações
        textViewPressao = findViewById(R.id.textViewPressao)
        textViewVazao = findViewById(R.id.textViewVazao)
        chart = findViewById(R.id.chart)

        // Configurar o gráfico
        configureChart()

        // Inicializar o banco de dados Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && dispositivoId != null) {
            databaseRef = FirebaseDatabase.getInstance().reference
                .child("customers")
                .child(userId)
                .child("devices")
                .child(dispositivoId)

            // Adicionar um listener de valor ao banco de dados para carregar os dados do dispositivo
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Recuperar os valores de pressão e vazão do dispositivo
                        val pressao = dataSnapshot.child("pressure").getValue(Double::class.java)
                        val vazao = dataSnapshot.child("flow").getValue(Double::class.java)

                        // Exibir os valores de pressão e vazão na interface do usuário
                        textViewPressao.text = "Pressão: $pressao"
                        textViewVazao.text = "Vazão: $vazao"

                        // Adicionar os valores ao gráfico
                        val timestamp = System.currentTimeMillis()
                        val formattedTime = formatTime(timestamp)
                        pressao?.let { pressureEntries.add(Entry(formattedTime, it.toFloat())) }
                        vazao?.let { flowEntries.add(Entry(formattedTime, it.toFloat())) }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Trate os erros de carregamento dos dados do dispositivo
                }
            })

            // Iniciar a atualização do gráfico em intervalos regulares
            handler.postDelayed(object : Runnable {
                override fun run() {
                    updateChart()
                    handler.postDelayed(this, updateInterval)
                }
            }, updateInterval)
        }
    }

    private fun configureChart() {
        // Configurar o estilo do gráfico
        chart.axisLeft.textColor = Color.BLUE // Cor dos números no eixo Y
        chart.axisRight.textColor = Color.BLUE // Cor dos números no eixo Y (lado direito)
        chart.xAxis.textColor = Color.BLUE // Cor dos números no eixo X
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Posição dos números no eixo X
        chart.description.isEnabled = false // Desativar a descrição do gráfico
    }

    private fun updateChart() {
        val currentTime = System.currentTimeMillis()
        val timeLimit = currentTime - 30000 // 30 segundos em milissegundos

        // Remover entradas antigas
        while (pressureEntries.isNotEmpty() && pressureEntries.peek().x < formatTime(timeLimit)) {
            pressureEntries.poll()
        }
        while (flowEntries.isNotEmpty() && flowEntries.peek().x < formatTime(timeLimit)) {
            flowEntries.poll()
        }

        val pressureDataSet = LineDataSet(pressureEntries, "Pressão")
        val flowDataSet = LineDataSet(flowEntries, "Vazão")

        // Configurar cores dos dados
        pressureDataSet.color = Color.RED
        pressureDataSet.valueTextColor = Color.RED // Cor dos números dos dados de pressão
        flowDataSet.color = Color.GREEN
        flowDataSet.valueTextColor = Color.GREEN // Cor dos números dos dados de vazão

        val lineData = LineData(pressureDataSet, flowDataSet)
        chart.data = lineData
        chart.invalidate() // Refresh the chart
    }

    private fun formatTime(timeInMillis: Long): Float {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val hours = calendar.get(Calendar.HOUR_OF_DAY).toFloat()
        val minutes = calendar.get(Calendar.MINUTE).toFloat()
        val seconds = calendar.get(Calendar.SECOND).toFloat()
        return hours * 3600 + minutes * 60 + seconds
    }
}
