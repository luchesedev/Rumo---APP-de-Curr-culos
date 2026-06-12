package com.example.rumo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.example.rumo.model.Vaga;
import com.example.rumo.repository.VagaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Rumo extends AppCompatActivity {

    private RecyclerView rvCurriculos;
    private View llEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumo);

        rvCurriculos  = findViewById(R.id.rvCurriculos);
        llEmptyState  = findViewById(R.id.llEmptyState);

        configurarCarrossel();
        configurarListaCurriculos();
    }

    // Recarrega a lista toda vez que o usuário volta para esta tela
    // (ex: após criar ou editar um currículo)
    @Override
    protected void onResume() {
        super.onResume();
        configurarListaCurriculos();
        configurarCarrossel();
    }

    private void configurarCarrossel() {
        RecyclerView rvCarrossel = findViewById(R.id.rvCarousel);
        rvCarrossel.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Valores padrão caso o usuário ainda não tenha preenchido nada
        String areaBusca = "Jovem Aprendiz";
        String nivelBusca = "estágio";

        // 1. Pega o usuário logado no Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // 2. Busca o currículo salvo no SQLite
            CurriculoDAO dao = new CurriculoDAO(this);
            Curriculo curriculo = dao.buscarPorEmail(user.getEmail());

            // 3. Verifica se a Área de Cursos (Objetivo) foi preenchida
            if (curriculo != null && curriculo.getObjetivo() != null && !curriculo.getObjetivo().isEmpty()) {

                // Como as áreas são separadas por vírgula, pegamos a primeira para focar a pesquisa
                String[] areas = curriculo.getObjetivo().split(",");
                if (areas.length > 0 && !areas[0].trim().isEmpty()) {
                    areaBusca = areas[0].trim(); // Define a área da API como a área do usuário
                }
            }
        }

        // 4. Chama o repositório com a área dinâmica
        VagaRepository repository = new VagaRepository(this);
        repository.buscarVagas(areaBusca, nivelBusca, new VagaRepository.VagaCallback() {
            @Override
            public void onSucesso(List<Vaga> vagas) {
                runOnUiThread(() ->
                        rvCarrossel.setAdapter(new CarrosselAdapter(Rumo.this, vagas)));
            }

            @Override
            public void onErro(String mensagem) {
                Log.e("CARROSSEL", "Erro: " + mensagem);
            }
        });
    }

    private void configurarListaCurriculos() {
        CurriculoDAO dao  = new CurriculoDAO(this);
        List<Curriculo> lista = dao.obterCurriculosProntos();

        rvCurriculos.setLayoutManager(new LinearLayoutManager(this));
        rvCurriculos.setAdapter(new CurriculoAdapter(this, lista));

        // Mostra empty state se não houver nenhum currículo
        llEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        rvCurriculos.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public void telaCurriculo(View view) {
        startActivity(new Intent(this, EscolhaCurriculo.class));
    }

    public void telaManutencao(View view) {
        String emailLogado = getIntent().getStringExtra("email_usuario");
        Intent it = new Intent(this, ManutencaoUsuario.class);
        it.putExtra("email_usuario", emailLogado);
        startActivity(it);
    }
}