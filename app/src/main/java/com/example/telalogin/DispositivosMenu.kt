package com.example.telalogin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.telalogin.databinding.ActivityDispositivosMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

open class DispositivosMenu : AppCompatActivity() {
    private lateinit var binding: ActivityDispositivosMenuBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var dispositivosAdapter: ArrayAdapter<String> // ou outro adaptador que você esteja usando
    private lateinit var dispositivosIDs: MutableList<String> // Para armazenar os DeviceIDs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispositivosMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Inicialize o banco de dados Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            databaseRef = FirebaseDatabase.getInstance().reference
                .child("customers")
                .child(userId)
                .child("devices")
        } else {
            // Usuário não autenticado, manipule esse caso conforme necessário
        }

        // Inicialize o adaptador para a lista de dispositivos
        dispositivosAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        binding.listViewDispositivos.adapter = dispositivosAdapter

        // Inicialize a lista de DeviceIDs
        dispositivosIDs = mutableListOf()

        // Adicione um listener de valor ao banco de dados para carregar os dispositivos
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dispositivosAdapter.clear()
                dispositivosIDs.clear()
                for (dispositivoSnapshot in dataSnapshot.children) {
                    val nomeDispositivo = dispositivoSnapshot.child("name").getValue(String::class.java)
                    val dispositivoId = dispositivoSnapshot.key
                    if (nomeDispositivo != null && dispositivoId != null) {
                        dispositivosAdapter.add(nomeDispositivo)
                        dispositivosIDs.add(dispositivoId)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Trate os erros de carregamento dos dispositivos
            }
        })

        // Adicionar um listener de clique aos itens da lista
        binding.listViewDispositivos.setOnItemClickListener { _, _, position, _ ->
            val dispositivoId = dispositivosIDs[position]
            val intent = Intent(this, Dispositivos::class.java).apply {
                putExtra("deviceID", dispositivoId)
            }
            startActivity(intent)
        }

        // Adicionar um listener de clique longo para excluir o dispositivo
        binding.listViewDispositivos.setOnItemLongClickListener { _, _, position, _ ->
            val dispositivoId = dispositivosIDs[position]
            val nomeDispositivo = dispositivosAdapter.getItem(position)

            // Exibe um diálogo de confirmação
            AlertDialog.Builder(this).apply {
                setTitle("Excluir Dispositivo")
                setMessage("Tem certeza que deseja excluir o dispositivo '$nomeDispositivo'?")
                setPositiveButton("Excluir") { _, _ ->
                    // Remove o dispositivo do Firebase
                    databaseRef.child(dispositivoId).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@DispositivosMenu, "Dispositivo excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@DispositivosMenu, "Falha ao excluir o dispositivo.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                setNegativeButton("Não", null)
            }.create().show()

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dispositivos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cadastrar_novo_dispositivo -> {
                startActivity(Intent(this, RegisterDevice::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
