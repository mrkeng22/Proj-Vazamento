package com.example.telalogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
private lateinit var auth: FirebaseAuth
private lateinit var btnLogout: Button  // Adicionado para o botão de logout

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inicializa o botão de logout
        btnLogout = findViewById(R.id.btnLogout)

        // Define o clique para o botão de logout
        btnLogout.setOnClickListener {
            confirmarLogout()
        }

        val botaoVoltar: ImageButton = findViewById(R.id.btn_back)

        botaoVoltar.setOnClickListener{
            val tela2 = Intent(this, Sistema2::class.java)
            startActivity(tela2)
            finish()
        }
    }

    private fun confirmarLogout() {
        // Cria o AlertDialog para confirmar o logout
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Logout")
        builder.setMessage("Você tem certeza que deseja sair?")

        // Define o botão de confirmação
        builder.setPositiveButton("Sim") { dialog, _ ->
            auth.signOut()  // Faz logout do usuário no Firebase

            // Limpar a preferência de manter o usuário conectado
            val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("keepLoggedIn", false) // Limpa a preferência de login
            editor.apply()

            // Redireciona para a tela principal (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Fecha a activity atual
        }

        // Define o botão de cancelamento
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }



}