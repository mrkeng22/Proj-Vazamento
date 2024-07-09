package com.example.telalogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val txtEsqueciSenha: TextView = findViewById(R.id.textViewForgotPassword)

        btnLogin.setOnClickListener {
            val email = emailInputLayout.editText?.text.toString()
            val password = passwordInputLayout.editText?.text.toString()

            loginUser(email, password)
        }
        txtEsqueciSenha.setOnClickListener{

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
//dasndoianf
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido, direcione para a próxima tela
                    Log.d(TAG, "signInWithCustomToken:success")
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
                    // Exiba um pop-up informando que o email ou a senha estão incorretos
                    // Aqui você pode exibir uma mensagem de erro na interface do usuário
                }
            }
    }

    private fun irParaSegundaTela() {
        val segundaTela = Intent(this, Sistema2::class.java)
        startActivity(segundaTela)

    }
    private fun irParaEsqueciSenha() {
        val segundaTela = Intent(this, EsqueciSenha::class.java)
        startActivity(segundaTela)

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
