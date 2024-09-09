package com.example.telalogin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val botaoVoltar: ImageButton = findViewById(R.id.btn_back)

        botaoVoltar.setOnClickListener{
            val tela2 = Intent(this, Sistema2::class.java)
            startActivity(tela2)
            finish()
        }
    }

}