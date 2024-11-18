package com.example.telalogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.telalogin.databinding.ActivitySistema2Binding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class Sistema2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivitySistema2Binding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o binding
        binding = ActivitySistema2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configura a Toolbar
        setSupportActionBar(binding.toolbar)  // Configure a Toolbar como ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)

        // Define o listener para o NavigationView
        navView.setNavigationItemSelectedListener(this)

        // Aplica os insets da barra do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                irParaDispositivosMenu()
                Toast.makeText(this, "Dispositivo selecionado", Toast.LENGTH_SHORT).show()
            }
            R.id.action_settings -> {
                // Inicia a tela de Configurações
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Configurações selecionado", Toast.LENGTH_SHORT).show()
            }
            R.id.action_profile -> {
                // Inicia a tela de Perfil
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Perfil selecionado", Toast.LENGTH_SHORT).show()
            }
            R.id.graphics ->{
                val intent = Intent(this, Graphics::class.java)
                startActivity(intent)
                Toast.makeText(this, "Graficos selecionado",Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun irParaDispositivosMenu() {
        val segundaTela = Intent(this, DispositivosMenu::class.java)
        startActivity(segundaTela)
    }

}
