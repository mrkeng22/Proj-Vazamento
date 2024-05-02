package com.example.telalogin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialize o objeto FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Obtenha as referências para os TextInputLayouts
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)

        val btnIrParaOutraTela: Button = findViewById(R.id.bt_fluxo)
        val btnIrParaRegistro: Button = findViewById(R.id.buttonRegister)

        btnIrParaOutraTela.setOnClickListener {
            // Obtenha os valores inseridos pelo usuário nos campos de email e senha
            val email = emailInputLayout.editText?.text.toString()
            val password = passwordInputLayout.editText?.text.toString()

            // Chame a função loginUser com o email e a senha fornecidos pelo usuário
            loginUser(email, password)
        }

        btnIrParaRegistro.setOnClickListener {
            // Criar um Intent para iniciar a RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Verifique se o usuário já está autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Se o usuário estiver autenticado, vá para a tela principal do sistema
            irParaSegundaTela()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido, direcione para a próxima tela
                    Log.d(TAG, "signInWithCustomToken:success")
                    irParaSegundaTela()
                } else {
                    // Se o login falhar, exiba uma mensagem de erro
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // Exiba um pop-up informando que o email ou a senha estão incorretos
                    Toast.makeText(
                        baseContext, "Email ou senha incorretos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun irParaSegundaTela() {
        val segundaTela = Intent(this, Sistema::class.java)
        startActivity(segundaTela)
        finish() // Encerrar a atividade atual
    }

    fun irParaEsqueciSenha(view: View) {
        val intent = Intent(this, EsqueciSenha::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
