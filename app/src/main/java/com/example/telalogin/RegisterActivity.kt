package com.example.telalogin

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Preencha todos os campos!",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }else{

                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro bem-sucedido
                    // Você pode adicionar lógica adicional aqui, como redirecionar para a próxima tela
                    Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                } else {
                    // Falha no registro
                    Toast.makeText(this, "Falha no registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

