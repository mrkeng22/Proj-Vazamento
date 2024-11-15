import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telalogin.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class YourActivity extends AppCompatActivity {

    private FirebaseDatabase realtimeDatabase;
    private FirebaseDatabase historicalDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o Firebase App padrão (dados em tempo real)
        realtimeDatabase = FirebaseDatabase.getInstance();

        // Configura o segundo Firebase App (dados históricos) programaticamente
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("SEGUNDO_PROJECT_ID") // Substitua pelo Project ID do segundo projeto
                .setApiKey("SEGUNDO_API_KEY") // Substitua pela API Key do segundo projeto
                .setDatabaseUrl("https://URL_DO_SEGUNDO_PROJETO.firebaseio.com") // URL do banco de dados do segundo projeto
                .build();

        FirebaseApp historicalApp;
        try {
            historicalApp = FirebaseApp.initializeApp(this, options, "historicalFirebase");
        } catch (IllegalStateException e) {
            // Caso já tenha sido inicializado, recupere a instância existente
            historicalApp = FirebaseApp.getInstance("historicalFirebase");
        }

        historicalDatabase = FirebaseDatabase.getInstance(historicalApp);

        // Agora você pode usar `realtimeDatabase` e `historicalDatabase` separadamente.
    }

    private void saveRealtimeData(String deviceId, Map<String, Object> data) {
        DatabaseReference realtimeRef = realtimeDatabase.getReference("realtime_data/" + deviceId);
        realtimeRef.setValue(data)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Dados em tempo real salvos com sucesso"))
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao salvar dados em tempo real", e));
    }

    private void saveHistoricalData(String deviceId, String date, String time, Map<String, Object> data) {
        DatabaseReference historicalRef = historicalDatabase.getReference("historical_data/" + deviceId + "/" + date + "/" + time);
        historicalRef.setValue(data)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Dados históricos salvos com sucesso"))
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao salvar dados históricos", e));
    }
}
