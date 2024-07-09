package com.example.telalogin
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Dispositivo está conectado à Internet
            Log.d("ConnectivityReceiver", "Dispositivo está conectado à Internet")

            // Iniciar sua atividade de background aqui
            val serviceIntent = Intent(context, MyBackgroundService::class.java)
            context.startService(serviceIntent)
        } else {
            // Dispositivo não está conectado à Internet
            Log.d("ConnectivityReceiver", "Dispositivo não está conectado à Internet")
        }
    }
}
