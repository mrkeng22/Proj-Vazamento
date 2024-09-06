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
    private lateinit var btnLogout: Button  // Adicionado para o botão de logout

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configura o ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Define o listener para o NavigationView
        navView.setNavigationItemSelectedListener(this)

        // Aplica os insets da barra do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o botão de logout
        btnLogout = findViewById(R.id.btnLogout)

        // Define o clique para o botão de logout
        btnLogout.setOnClickListener {
            confirmarLogout()
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun confirmarLogout() {
        // Cria o AlertDialog para confirmar o logout
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Logout")
        builder.setMessage("Você tem certeza que deseja sair?")

        // Define o botão de confirmação
        builder.setPositiveButton("Sim") { dialog, _ ->
            auth.signOut()  // Faz logout do usuário no Firebase

            // Limpar a preferência de manter o usuário conectado
            val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("keepLoggedIn", false) // Limpa a preferência de login
            editor.apply()

            // Redireciona para a tela principal (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Fecha a activity atual
        }

        // Define o botão de cancelamento
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun irParaDispositivosMenu() {
        val segundaTela = Intent(this, DispositivosMenu::class.java)
        startActivity(segundaTela)
    }
}
