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

import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AreaUsuario extends AppCompatActivity {

    private Button btnSalvar;

    // 1. Alterado para TextInputEditText para ficar igual ao seu XML
    private TextInputEditText editArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_area_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSalvar = findViewById(R.id.btnSalvarRumo);

        // 2. Vinculando o ID correto encontrado no XML
        editArea = findViewById(R.id.txtArea);

        btnSalvar.setOnClickListener(v -> {

            // 3. Pega o usuário logado no Firebase para extrair o e-mail
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Erro: Sessão inválida!", Toast.LENGTH_SHORT).show();
                return;
            }
            String emailLogado = user.getEmail();

            // 4. Captura o que o usuário digitou no campo de cursos
            String cursosEscolhidos = "";
            if (editArea != null && editArea.getText() != null) {
                cursosEscolhidos = editArea.getText().toString().trim();
            }

            // 5. Prepara o objeto Curriculo
            Curriculo novoCurriculo = new Curriculo();
            novoCurriculo.setEmail(emailLogado);
            novoCurriculo.setObjetivo(cursosEscolhidos); // Salva o curso na variável "Objetivo"

            // 6. Salva no banco (o autoincrement cuida do ID)
            CurriculoDAO dao = new CurriculoDAO(this);
            dao.Insert(novoCurriculo);

            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

            // 7. Envia para a tela principal (Rumo)
            Intent it = new Intent(this, Rumo.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
            finish();
        });
    }
}