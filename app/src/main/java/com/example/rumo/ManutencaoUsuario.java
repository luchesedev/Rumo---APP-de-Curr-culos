package com.example.rumo;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.google.android.material.textfield.TextInputEditText;

public class ManutencaoUsuario extends AppCompatActivity {

    // Componentes mapeados do XML
    private TextInputEditText editNome, editBairro, editTelefone, editEmail, editLink, editInstituicao, editPeriodo,editStatus;
    private MultiAutoCompleteTextView editAreaCursos;
    private TextInputEditText editFormacao, editHabilidade, editExperiencia, editResumo;
    private Button btnSalvar;


    // Conexão com o banco e o objeto
    private CurriculoDAO dao;
    private Curriculo curriculoAtual;

    // Separador usado para concatenar e desconcatenar os dados pessoais no banco
    private final String SEPARADOR = "##";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Confirme se o nome do seu layout XML é este mesmo
        setContentView(R.layout.activity_manutencao_usuario);

        dao = new CurriculoDAO(this);

        inicializarComponentes();
        configurarAbas(); // Ativa o efeito sanfona
        configurarCursosMultiplos(); // Prepara o dropdown múltiplo

        // Verifica se veio um currículo para edição da tela anterior
        curriculoAtual = (Curriculo) getIntent().getSerializableExtra("curriculo_selecionado");

        if (curriculoAtual != null) {
            preencherDadosNaTela();
        }

        btnSalvar.setOnClickListener(v -> salvarCurriculo());
    }

    private void inicializarComponentes() {
        // Campos de Dados Pessoais
        editNome = findViewById(R.id.editNome);
        editBairro = findViewById(R.id.editBairro);
        editTelefone = findViewById(R.id.editTelefone);
        editEmail = findViewById(R.id.editEmail);
        editLink = findViewById(R.id.editLink);

        // Área / Cursos (Mapeado para o 'objetivo' no banco)
        editAreaCursos = findViewById(R.id.editAreaCursos);

        // Demais campos do currículo
        // Substitua o antigo editFormacao por estes 3:
        editInstituicao = findViewById(R.id.editInstituicao);
        editPeriodo = findViewById(R.id.editPeriodo);
        editStatus = findViewById(R.id.editStatus);
        editHabilidade = findViewById(R.id.editHabilidade);
        editExperiencia = findViewById(R.id.editExperiencia);
        editResumo = findViewById(R.id.editResumo);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void preencherDadosNaTela() {
        // 1. Desconcatena os Dados Pessoais
        if (curriculoAtual.getDadosPessoais() != null && !curriculoAtual.getDadosPessoais().isEmpty()) {
            String[] dados = curriculoAtual.getDadosPessoais().split(SEPARADOR);

            if (dados.length > 0) editNome.setText(dados[0]);
            if (dados.length > 1) editBairro.setText(dados[1]);
            if (dados.length > 2) editTelefone.setText(dados[2]);
            if (dados.length > 3) editEmail.setText(dados[3]);
            if (dados.length > 4) editLink.setText(dados[4]);
        }

        // 2. Desconcatena a Formação Detalhada
        if (curriculoAtual.getFormacao() != null && !curriculoAtual.getFormacao().isEmpty()) {
            String[] form = curriculoAtual.getFormacao().split(SEPARADOR);

            if (form.length > 0) editInstituicao.setText(form[0]);
            if (form.length > 1) editPeriodo.setText(form[1]);
            if (form.length > 2) editStatus.setText(form[2]);
        }

        // 3. Preenche os campos simples que não precisam de split
        editAreaCursos.setText(curriculoAtual.getObjetivo());
        editHabilidade.setText(curriculoAtual.getHabilidade());
        editExperiencia.setText(curriculoAtual.getExperiencia());
        editResumo.setText(curriculoAtual.getResumo());
    }

    private void salvarCurriculo() {
        // 1. Coleta os textos digitados
        String nome = editNome.getText().toString().trim();
        String bairro = editBairro.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String link = editLink.getText().toString().trim();

        // Coleta os novos campos de Formação
        String instituicao = editInstituicao.getText().toString().trim();
        String periodo = editPeriodo.getText().toString().trim();
        String status = editStatus.getText().toString().trim();

        // 2. Validação básica obrigatória
        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nome e E-mail são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Junta tudo em Strings únicas (Serialização)
        String dadosPessoaisConcatenados = nome + SEPARADOR + bairro + SEPARADOR + telefone + SEPARADOR + email + SEPARADOR + link;
        String formacaoConcatenada = instituicao + SEPARADOR + periodo + SEPARADOR + status;

        // 4. Se for um novo registro, cria a instância
        boolean isNovo = false;
        if (curriculoAtual == null) {
            curriculoAtual = new Curriculo();
            isNovo = true;
        }

        // 5. Alimenta o objeto com os dados processados
        curriculoAtual.setDadosPessoais(dadosPessoaisConcatenados);
        curriculoAtual.setFormacao(formacaoConcatenada); // Agora salvamos os 3 campos unidos

        curriculoAtual.setObjetivo(editAreaCursos.getText().toString().trim());
        curriculoAtual.setHabilidade(editHabilidade.getText().toString().trim());
        curriculoAtual.setExperiencia(editExperiencia.getText().toString().trim());
        curriculoAtual.setResumo(editResumo.getText().toString().trim());

        // 6. Operação no Banco de Dados
        try {
            if (isNovo) {
                long resultado = dao.Insert(curriculoAtual);
                if (resultado != -1) {
                    Toast.makeText(this, "Currículo salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao cadastrar currículo.", Toast.LENGTH_SHORT).show();
                }
            } else {
                dao.update(curriculoAtual);
                Toast.makeText(this, "Currículo atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ==========================================================
    // MÉTODOS DE CONFIGURAÇÃO DA UI
    // ==========================================================

    private void configurarCursosMultiplos() {
        // Opções para o MultiAutoComplete (adicione ou remova conforme a necessidade)
        String[] cursosDisponiveis = {
                "Administração", "Análise de Sistemas", "Direito",
                "Engenharia de Software", "Logística", "Matemática",
                "Recursos Humanos", "Tecnologia da Informação"
        };
        ArrayAdapter<String> adapterCursos = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cursosDisponiveis);
        editAreaCursos.setAdapter(adapterCursos);
        editAreaCursos.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    private void configurarAbas() {
        // Vincula as abas (cabeçalhos) aos seus respectivos conteúdos (layouts a serem escondidos/exibidos)
        configurarAba(findViewById(R.id.btnAbaDadosPessoais), findViewById(R.id.conteudoDadosPessoais), findViewById(R.id.setaDadosPessoais));
        configurarAba(findViewById(R.id.btnAbaCursos), findViewById(R.id.conteudoCursos), findViewById(R.id.setaCursos));
        configurarAba(findViewById(R.id.btnAbaFormacao), findViewById(R.id.conteudoFormacao), findViewById(R.id.setaFormacao));
        configurarAba(findViewById(R.id.btnAbaHabilidade), findViewById(R.id.conteudoHabilidade), findViewById(R.id.setaHabilidade));
        configurarAba(findViewById(R.id.btnAbaExperiencia), findViewById(R.id.conteudoExperiencia), findViewById(R.id.setaExperiencia));
    }

    private void configurarAba(View btnAba, View conteudo, TextView seta) {
        if (btnAba != null && conteudo != null && seta != null) {
            btnAba.setOnClickListener(v -> {
                // Alterna a visibilidade e muda a seta dependendo do estado atual
                if (conteudo.getVisibility() == View.VISIBLE) {
                    conteudo.setVisibility(View.GONE);
                    seta.setText("▼");
                } else {
                    conteudo.setVisibility(View.VISIBLE);
                    seta.setText("▲");
                }
            });
        }
    }
}