package com.example.telalogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.view.View

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    val database = Firebase.database
    val myRef = database.getReference("message")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnIrParaOutraTela: Button = findViewById(R.id.bt_fluxo)
        btnIrParaOutraTela.setOnClickListener {
            myRef.setValue("Hello, World!")
            irParaSegundaTela()

        }


    }









    private fun irParaSegundaTela() {
        val segundaTela = Intent(this, EsqueciSenha::class.java)
        startActivity(segundaTela)
    }

    fun irParaEsqueciSenha(view: View) {
        val intent = Intent(this, EsqueciSenha::class.java)
        startActivity(intent)
    }
}
