package com.example.rumo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AreaUsuario  extends Tela_Base  {

    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSalvar = findViewById(R.id.btnSalvarRumo); // Certifique-se de que o ID no XML é este mesmo

        btnSalvar.setOnClickListener(v -> {
            // 1. AQUI VOCÊ DEVE CHAMAR A LÓGICA QUE SALVA OS DADOS NO BANCO (DAO)
            // ex: dao.Insert(curriculo);

            // 2. REMOVEMOS O signOut()! Não queremos deslogar o usuário ao salvar.

            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

            // 3. Agora sim, enviamos para a tela Rumo
            Intent it = new Intent(this, Rumo.class);
            // Usamos clear task para garantir que o usuário não volte para a tela de preenchimento
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
            finish();
        });
    }
}