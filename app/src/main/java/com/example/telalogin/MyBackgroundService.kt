package com.example.telalogin

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyBackgroundService : Service() {

    private lateinit var databaseRef: DatabaseReference
    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 5000  // 5 segundos

    override fun onCreate() {
        super.onCreate()
        Log.d("MyBackgroundService", "Serviço criado")
        startForegroundService()
        startMonitoring()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyBackgroundService", "Serviço iniciado")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyBackgroundService", "Serviço destruído")
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = "MyBackgroundServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Background Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notificação Hydro")
            .setContentText("Seus dados estão sendo monitorados em segundo plano")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun startMonitoring() {
        handler.post(object : Runnable {
            override fun run() {
                monitorFirebase()
                handler.postDelayed(this, interval)
            }
        })
    }

    private fun monitorFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            databaseRef = FirebaseDatabase.getInstance().reference
                .child("customers")
                .child(userId)
                .child("devices")

            databaseRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Não fazemos nada aqui, pois estamos interessados nas mudanças
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val dispositivoId = snapshot.key
                    val nome = snapshot.child("name").getValue(String::class.java)
                    val pressureValue = snapshot.child("pressure").getValue(Double::class.java)
                    val flowRateValue = snapshot.child("flow").getValue(Double::class.java)

                    pressureValue?.let {
                        if (it >= 1) {
                            Log.d("MyBackgroundService", "Dispositivo $nome - Novo valor de pressão: $it")
                            sendNotification("Pressão Alta", "Dispositivo $nome - Novo valor de pressão: $it")
                        }
                    }

                    flowRateValue?.let {
                        Log.d("MyBackgroundService", "Dispositivo $nome - Novo valor de vazão: $it")
                        sendNotification("Vazão Atualizada", "Dispositivo $nome - Novo valor de vazão: $it")
                    }

                    // Enviar broadcast para atualizar a atividade
                    val updateIntent = Intent("UPDATE_UI")
                    updateIntent.putExtra("dispositivoId", dispositivoId)
                    updateIntent.putExtra("pressure", pressureValue)
                    updateIntent.putExtra("flow", flowRateValue)
                    sendBroadcast(updateIntent)
                }


                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Não fazemos nada aqui
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Não fazemos nada aqui
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyBackgroundService", "Erro ao ler os dados do dispositivo.", error.toException())
                }
            })
        } else {
            Log.d("MyBackgroundService", "Usuário não autenticado.")
        }
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "FirebaseNotificationChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Firebase Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val managerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        managerCompat.notify(1, notificationBuilder.build())
    }
}
