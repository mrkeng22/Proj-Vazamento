package com.example.telalogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button // Importe a classe Button corretamente

class EsqueciSenha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_esqueci_senha)

        val btnIrParaOutraTela: Button = findViewById(R.id.button2)
        btnIrParaOutraTela.setOnClickListener {
            irParaSegundaTela()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun irParaSegundaTela() {
        val segundaTela = Intent(this, MainActivity::class.java)
        startActivity(segundaTela)
    }

}