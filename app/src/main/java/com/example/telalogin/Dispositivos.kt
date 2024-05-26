package com.example.telalogin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Dispositivos : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var textViewPressao: TextView
    private lateinit var textViewVazao: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos)

        // Recuperar o ID do dispositivo selecionado dos argumentos
        val dispositivoId = intent.getStringExtra("deviceID")

        // Inicializar as visualizações
        textViewPressao = findViewById(R.id.textViewPressao)
        textViewVazao = findViewById(R.id.textViewVazao)

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
                    // Verificar se o dispositivo existe
                    if (dataSnapshot.exists()) {
                        // Recuperar os valores de pressão e vazão do dispositivo
                        val pressao = dataSnapshot.child("pressure").getValue(Double::class.java)
                        val vazao = dataSnapshot.child("flow").getValue(Double::class.java)

                        // Exibir os valores de pressão e vazão na interface do usuário
                        textViewPressao.text = "Pressão: $pressao"
                        textViewVazao.text = "Vazão: $vazao"
                    } else {
                        // O dispositivo não foi encontrado, manipule esse caso conforme necessário
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Trate os erros de carregamento dos dados do dispositivo
                }
            })
        } else {
            // Usuário não autenticado ou ID do dispositivo não recebido, manipule esse caso conforme necessário
        }
    }
}
