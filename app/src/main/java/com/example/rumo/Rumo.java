package com.example.rumo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.example.rumo.model.Vaga;
import com.example.rumo.repository.VagaRepository;

import java.util.ArrayList;
import java.util.List;

public class Rumo extends AppCompatActivity {
    private Intent it;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumo);

        configurarCarrossel();
        configurarListaCurriculos();
    }

    private void configurarCarrossel() {
        RecyclerView rvCarrossel = findViewById(R.id.rvCarousel);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCarrossel.setLayoutManager(layoutManager);

        // Busca vagas reais pela API
        VagaRepository repository = new VagaRepository(this);
        repository.buscarVagas("TI", "estagiário", new VagaRepository.VagaCallback() {
            @Override
            public void onSucesso(List<Vaga> vagas) {
                runOnUiThread(() -> {
                    rvCarrossel.setAdapter(new CarrosselAdapter(Rumo.this, vagas));
                });
            }

            @Override
            public void onErro(String mensagem) {
                Log.e("CARROSSEL", "Erro: " + mensagem);
            }
        });
    }

    private void configurarListaCurriculos() {
        RecyclerView rvCurriculos = findViewById(R.id.rvCurriculos);
        CurriculoDAO dao = new CurriculoDAO(this);
        List<Curriculo> lista = dao.obterTodos();
        rvCurriculos.setLayoutManager(new LinearLayoutManager(this));
        rvCurriculos.setAdapter(new CurriculoAdapter(this, lista));
    }
    public void telaCurriculo(View view){
        it = new Intent(getApplicationContext(), EscolhaCurriculo.class);
        startActivity(it);
    }
    public void telaManutencao(View view){
        // 1. "Pesca" o e-mail que a tela de Login enviou para esta tela
        String emailLogado = getIntent().getStringExtra("email_usuario");

        // 2. Prepara a viagem para a tela de Manutenção
        Intent it = new Intent(this, ManutencaoUsuario.class);

        // 3. Coloca o e-mail na bagagem para a próxima tela
        it.putExtra("email_usuario", emailLogado);

        // 4. Inicia a nova tela
        startActivity(it);
    }
}