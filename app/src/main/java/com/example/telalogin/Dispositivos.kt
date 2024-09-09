package com.example.telalogin

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Dispositivos : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var textViewPressao: TextView
    private lateinit var textViewVazao: TextView
    private lateinit var textViewBateria: TextView
    private lateinit var chart: LineChart
    private lateinit var btnValv: Button
    private lateinit var btnColetarDados: Button
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
        textViewBateria = findViewById(R.id.textViewBateria) // Inicializa a TextView da bateria
        chart = findViewById(R.id.chart)
        btnValv = findViewById(R.id.btnValv)
        btnColetarDados = findViewById(R.id.btnColetarDados) // Inicializar o botão de coleta de dados

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
                        // Recuperar os valores de pressão, vazão e bateria do dispositivo
                        val pressao = dataSnapshot.child("pressure").getValue(Double::class.java)
                        val vazao = dataSnapshot.child("flow").getValue(Double::class.java)
                        val bateria = dataSnapshot.child("batteryLevel").getValue(Double::class.java)

                        // Exibir os valores na interface do usuário, tratando valores nulos
                        textViewPressao.text = "Pressão: ${pressao ?: "N/A"} MPa"
                        textViewVazao.text = "Vazão: ${vazao ?: "N/A"} L/s"
                        textViewBateria.text = "Bateria: ${bateria ?: "N/A"}%"

                        // Adicionar os valores ao gráfico, apenas se não forem nulos
                        val timestamp = System.currentTimeMillis()
                        val formattedTime = formatTime(timestamp)
                        pressao?.let { pressureEntries.add(Entry(formattedTime, it.toFloat())) }
                        vazao?.let { flowEntries.add(Entry(formattedTime, it.toFloat())) }

                        // Atualizar o gráfico
                        updateChart()
                    } else {
                        Log.d("Firebase", "Nenhum dado encontrado no caminho especificado.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Erro ao ler dados: ${databaseError.message}")
                    Toast.makeText(this@Dispositivos, "Erro ao carregar dados do Firebase", Toast.LENGTH_SHORT).show()
                }
            })

            // Iniciar a atualização do gráfico em intervalos regulares
            startChartUpdate()

            // Configurar o botão para setar o estado no Firebase
            btnValv.setOnClickListener {
                toggleState()
            }

            // Configurar o botão para coletar dados do Firebase
            btnColetarDados.setOnClickListener {
                coletarDadosDoFirebase()
            }

        } else {
            Log.e("Firebase", "userId ou dispositivoId é nulo.")
            Toast.makeText(this, "Erro ao carregar o dispositivo. Por favor, tente novamente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureChart() {
        // Configurar o estilo do gráfico
        chart.axisLeft.textColor = Color.BLACK // Cor dos números no eixo Y
        chart.axisLeft.setDrawGridLines(false) // Remover linhas de grade no eixo Y
        chart.axisRight.isEnabled = false // Desativar o eixo Y à direita
        chart.xAxis.textColor = Color.BLACK // Cor dos números no eixo X
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Posição dos números no eixo X
        chart.xAxis.setDrawGridLines(false) // Remover linhas de grade no eixo X
        chart.description.isEnabled = false // Desativar a descrição do gráfico
        chart.legend.isEnabled = true // Mostrar legenda do gráfico
        chart.setTouchEnabled(true) // Habilitar toque no gráfico
        chart.isDragEnabled = true // Habilitar arrastar
        chart.setScaleEnabled(true) // Habilitar zoom

        // Adicionando animação
        chart.animateX(1500, Easing.EaseInOutQuart)

        // Configurar o formato do eixo X como horário
        val xAxis = chart.xAxis
        xAxis.valueFormatter = TimeAxisValueFormatter()
        xAxis.granularity = 1f // Ajustar conforme necessário
        xAxis.setDrawLabels(true) // Garantir que os rótulos estejam desenhados
    }

    private fun startChartUpdate() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateChart()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
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

        // Configurações de estilo para o conjunto de dados de pressão
        pressureDataSet.color = Color.RED
        pressureDataSet.valueTextColor = Color.RED
        pressureDataSet.lineWidth = 2f // Espessura da linha
        pressureDataSet.circleRadius = 5f // Tamanho do círculo nos pontos
        pressureDataSet.setCircleColor(Color.RED) // Cor do círculo nos pontos
        pressureDataSet.setDrawCircles(true) // Mostrar círculos nos pontos
        pressureDataSet.setDrawValues(false) // Não mostrar os valores dos pontos

        // Configurações de estilo para o conjunto de dados de vazão
        flowDataSet.color = Color.GREEN
        flowDataSet.valueTextColor = Color.GREEN
        flowDataSet.lineWidth = 2f // Espessura da linha
        flowDataSet.circleRadius = 5f // Tamanho do círculo nos pontos
        flowDataSet.setCircleColor(Color.GREEN) // Cor do círculo nos pontos
        flowDataSet.setDrawCircles(true) // Mostrar círculos nos pontos
        flowDataSet.setDrawValues(false) // Não mostrar os valores dos pontos

        val lineData = LineData(pressureDataSet, flowDataSet)
        chart.data = lineData
        chart.invalidate() // Atualizar o gráfico

        // Mover a visualização para os novos dados
        if (pressureEntries.isNotEmpty() || flowEntries.isNotEmpty()) {
            chart.moveViewToX(chart.data!!.xMax)
        }

        Log.d("Chart", "Atualização do gráfico completa.")
    }

    private fun formatTime(timeInMillis: Long): Float {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        return (hours * 3600 + minutes * 60 + seconds).toFloat()
    }

    private fun toggleState() {
        databaseRef.child("state").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentState = snapshot.getValue(Boolean::class.java) ?: false
                val newState = !currentState
                databaseRef.child("state").setValue(newState).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val message = if (newState) "Fechou a válvula" else "Abriu a válvula"
                        Toast.makeText(this@Dispositivos, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Dispositivos, "Erro ao atualizar o estado", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erro ao ler estado: ${error.message}")
            }
        })
    }

    // Método para coletar dados do Firebase e atualizar o gráfico
    private fun coletarDadosDoFirebase() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recuperar os valores de pressão, vazão e bateria do dispositivo
                    val pressao = dataSnapshot.child("pressure").getValue(Double::class.java)
                    val vazao = dataSnapshot.child("flow").getValue(Double::class.java)

                    // Exibir os valores na interface do usuário, tratando valores nulos
                    textViewPressao.text = "Pressão: ${pressao ?: "N/A"}"
                    textViewVazao.text = "Vazão: ${vazao ?: "N/A"}"


                    // Adicionar os valores ao gráfico, apenas se não forem nulos
                    val timestamp = System.currentTimeMillis()
                    val formattedTime = formatTime(timestamp)
                    pressao?.let { pressureEntries.add(Entry(formattedTime, it.toFloat())) }
                    vazao?.let { flowEntries.add(Entry(formattedTime, it.toFloat())) }

                    // Atualizar o gráfico
                    updateChart()

                    Toast.makeText(this@Dispositivos, "Dados coletados com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Dispositivos, "Nenhum dado encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Dispositivos, "Erro ao coletar dados: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Classe para formatar o eixo X como horário
    private class TimeAxisValueFormatter : ValueFormatter() {
        private val mFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val hours = (value / 3600).toInt()
            val minutes = ((value % 3600) / 60).toInt()
            val seconds = (value % 60).toInt()
            return mFormat.format(Date(0, 0, 0, hours, minutes, seconds))
        }
    }
}