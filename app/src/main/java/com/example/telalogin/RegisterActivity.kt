package com.example.telalogin

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Preencha todos os campos!",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro bem-sucedido
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        addUserToDatabase(it, name, email)
                    }
                    Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                } else {
                    // Falha no registro
                    Toast.makeText(this, "Falha no registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(userId: String, name: String, email: String) {
        val userMap = mapOf(
            "name" to name,
            "email" to email,
            "devices" to emptyMap<String, Any>() // Inicializa com um nó de dispositivos vazio
        )

        database.child("customers").child(userId).setValue(userMap)
            .addOnSuccessListener {
                // Usuário adicionado ao banco de dados com sucesso
                Toast.makeText(this, "User added to database.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Falha ao adicionar usuário ao banco de dados
                Toast.makeText(this, "Failed to add user to database.", Toast.LENGTH_SHORT).show()
            }
    }
}
