package com.example.telalogin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var stayLoggedInCheckBox: CheckBox
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        stayLoggedInCheckBox = findViewById(R.id.stayLoggedInCheckBox)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val txtEsqueciSenha: TextView = findViewById(R.id.textViewForgotPassword)

        // Verificar se o usuário está conectado e se escolheu ficar conectado
        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("keepLoggedIn", false)) {
            irParaSegundaTela()
        }

        btnLogin.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString().trim()
            val password = passwordInputLayout.editText?.text.toString().trim()

            try {
                // Verifica se o email ou a senha estão vazios
                if (email.isEmpty() || password.isEmpty()) {
                    // Exibe uma mensagem Toast indicando para preencher os campos
                    Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                } else {
                    // Chama o método de login se ambos os campos estiverem preenchidos
                    loginUser(email, password)
                }
            } catch (e: Exception) {
                // Trata qualquer exceção que possa ocorrer durante o login
                Toast.makeText(this, "Ocorreu um erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


        txtEsqueciSenha.setOnClickListener {
            irParaEsqueciSenha()
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        emailInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            // Adicione sua lógica de validação aqui
        }

        passwordInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            // Adicione sua lógica de validação aqui
        }
    }


    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido, salvar estado de login e preferência do usuário
                    Log.d(TAG, "signInWithEmail:success")

                    // Salvar a preferência de manter o usuário conectado
                    val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("keepLoggedIn", stayLoggedInCheckBox.isChecked)
                    editor.apply()

                    irParaSegundaTela()
                } else {
                    // Se o login falhar, exiba uma mensagem de erro
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        "Email ou senha Inválidos!",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
    }

    private fun irParaSegundaTela() {
        val segundaTela = Intent(this, Sistema2::class.java)
        startActivity(segundaTela)
        finish() // Fechar MainActivity para que não volte ao pressionar o botão "voltar"
    }

    private fun irParaEsqueciSenha() {
        val segundaTela = Intent(this, EsqueciSenha::class.java)
        startActivity(segundaTela)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
