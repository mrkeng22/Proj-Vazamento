package com.example.telalogin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EsqueciSenha : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_esqueci_senha)

        auth = FirebaseAuth.getInstance()

        val emailInputLayout = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailInputLayout)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val btnBack = findViewById<Button>(R.id.button2)
        btnBack.setOnClickListener {

            IrparaTelaMain()
        }


        btnResetPassword.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString()

            if (email.isNotEmpty()) {
                enviarEmailRedefinicaoSenha(email)
            } else {
                Toast.makeText(this, "Por favor, insira um email válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarEmailRedefinicaoSenha(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email de redefinição de senha enviado para $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Falha ao enviar email de redefinição de senha.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun IrparaTelaMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
