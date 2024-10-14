package com.example.telalogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.telalogin.databinding.ActivityDispositivosMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Graphics extends AppCompatActivity {
    private ActivityDispositivosMenuBinding binding;
    private DatabaseReference databaseRef;
    private ArrayAdapter<String> dispositivosAdapter;
    private ArrayList<String> dispositivosIDs; // Para armazenar os DeviceIDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDispositivosMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Inicialize o banco de dados Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            databaseRef = FirebaseDatabase.getInstance().getReference()
                    .child("customers")
                    .child(userId)
                    .child("devices");
        }

        // Inicialize o adaptador para a lista de dispositivos
        dispositivosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        binding.listViewDispositivos.setAdapter(dispositivosAdapter);

        // Inicialize a lista de DeviceIDs
        dispositivosIDs = new ArrayList<>();

        // Adicione um listener de valor ao banco de dados para carregar os dispositivos
        if (databaseRef != null) {
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dispositivosAdapter.clear();
                    dispositivosIDs.clear();
                    for (DataSnapshot dispositivoSnapshot : dataSnapshot.getChildren()) {
                        String nomeDispositivo = dispositivoSnapshot.child("name").getValue(String.class);
                        String dispositivoId = dispositivoSnapshot.getKey();
                        if (nomeDispositivo != null && dispositivoId != null) {
                            dispositivosAdapter.add(nomeDispositivo);
                            dispositivosIDs.add(dispositivoId);
                        }
                    }
                    dispositivosAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Trate os erros de carregamento dos dispositivos
                }
            });
        }


        // Adicionar um listener de clique para mudar para a tela OptionGraphics ao clicar em um item
        binding.listViewDispositivos.setOnItemClickListener((parent, view, position, id) -> {
            String dispositivoId = dispositivosIDs.get(position); // Obtém o DeviceID
            Intent intent = new Intent(Graphics.this, GraphicsPeriod.class);
            intent.putExtra("deviceID", dispositivoId); // Passa o DeviceID para a próxima tela
            startActivity(intent);
        });
    }
}
