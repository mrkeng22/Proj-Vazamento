package com.example.telalogin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterDevice : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        val editTextDeviceName = findViewById<EditText>(R.id.editTextDeviceName)
        val editTextDeviceType = findViewById<EditText>(R.id.editTextDeviceType)
        val buttonRegisterDevice = findViewById<Button>(R.id.buttonRegisterDevice)


        buttonRegisterDevice.setOnClickListener {
            val deviceName = editTextDeviceName.text.toString()
            val deviceType = editTextDeviceType.text.toString()

            if (deviceName.isEmpty() || deviceType.isEmpty()) {
                Toast.makeText(this, "Por favor preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                registerDevice(deviceName, deviceType)
            }
        }
    }

    private fun registerDevice(deviceName: String, deviceType: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val deviceId = database.child("customers").child(userId).child("devices").push().key
            if (deviceId != null) {
                val deviceMap = mapOf(
                    "name" to deviceName,
                    "type" to deviceType,
                    "pressure" to 0.0,       // Initial value
                    "flow" to 0.0,           // Initial value
                    "state" to false,        // Initial value
                    "batteryLevel" to 0.0,   // Initial Value
                    "totalConsumo" to 0.0    // New field for total consumption
                )

                database.child("customers").child(userId).child("devices").child(deviceId).setValue(deviceMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dispositivo registrado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Falha ao registrar o dispositivo.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Failed to generate device ID.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }
}