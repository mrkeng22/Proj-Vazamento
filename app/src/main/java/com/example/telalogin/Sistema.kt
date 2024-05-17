package com.example.telalogin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Sistema: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sistema)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle the Home action
                }
                R.id.nav_profile -> {
                    // Handle the Profile action
                }
                R.id.nav_settings -> {
                    // Handle the Settings action
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
