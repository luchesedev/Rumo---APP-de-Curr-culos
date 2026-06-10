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
        EdgeToEdge.enable(this);
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

        // Dados fictícios da lista de currículos
        List<CurriculoAdapter.CurriculoItem> itens = new ArrayList<>();
        itens.add(new CurriculoAdapter.CurriculoItem(
                "Currículo Principal",
                "07/06/2026",
                "Completo",
                true
        ));
        itens.add(new CurriculoAdapter.CurriculoItem(
                "Currículo Tech",
                "01/06/2026",
                "Completo",
                true
        ));
        itens.add(new CurriculoAdapter.CurriculoItem(
                "Currículo Freelancer",
                "20/05/2026",
                "Incompleto",
                false
        ));

        // Configura o RecyclerView vertical
        rvCurriculos.setLayoutManager(new LinearLayoutManager(this));
        rvCurriculos.setAdapter(new CurriculoAdapter(this, itens));
    }
    public void telaCurriculo(View view){
        it = new Intent(getApplicationContext(), EscolhaCurriculo.class);
        startActivity(it);
    }
    public void telaManutencao(View view){
        it = new Intent(getApplicationContext(), ManutencaoUsuario.class);
        startActivity(it);
    }
}