package com.example.rumo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ManutencaoUsuario extends Tela_Base {

    private TextInputEditText editNome, editBairro, editTelefone, editEmail, editInstituicao, editPeriodo, editStatus, editHabilidade, editExperiencia, editResumo;
    private MultiAutoCompleteTextView editAreaCursos;
    private Button btnSalvar, btnSair;
    private ImageView btnVoltar;

    private final String[] AREAS = {
            "Administração", "Agronegócio", "Agronomia", "Análise de Dados",
            "Análise de Sistemas", "Arquitetura", "Arquitetura de Software",
            "Artes Visuais", "Assistência Social", "Astronomia",
            "Atendimento ao Cliente", "Auditoria", "Automação Industrial",
            "Biologia", "Biomedicina", "Biotecnologia", "Ciência da Computação",
            "Ciências Contábeis", "Ciências Econômicas", "Cinema e Audiovisual",
            "Coaching", "Comércio Exterior", "Comunicação", "Construção Civil",
            "Consultoria", "Contabilidade", "Customer Success",
            "Data Science", "Design", "Design de Interiores", "Design Gráfico",
            "Design de Produto", "Design UX/UI", "Desenvolvimento Mobile",
            "Desenvolvimento Web", "DevOps", "Direito", "E-commerce",
            "Educação", "Educação Física", "Eletricidade", "Eletrônica",
            "Enfermagem", "Engenharia Aeronáutica", "Engenharia Agronômica",
            "Engenharia Ambiental", "Engenharia Civil", "Engenharia de Alimentos",
            "Engenharia de Computação", "Engenharia de Produção",
            "Engenharia de Software", "Engenharia Elétrica", "Engenharia Mecânica",
            "Engenharia Química", "Estética", "Eventos", "Farmácia",
            "Finanças", "Física", "Fisioterapia", "Fotografia",
            "Gastronomia", "Geologia", "Gestão Ambiental", "Gestão de Pessoas",
            "Gestão de Projetos", "Gestão de Qualidade", "Gestão de Riscos",
            "Gestão Financeira", "Gestão Hospitalar", "Gestão Pública",
            "Hotelaria", "Infraestrutura de TI", "Inteligência Artificial",
            "Jornalismo", "Jurídico", "Letras", "Logística", "Machine Learning",
            "Marketing", "Marketing Digital", "Matemática", "Medicina",
            "Medicina Veterinária", "Meteorologia", "Moda", "Música",
            "Nutrição", "Oceanografia", "Odontologia", "Pedagogia",
            "Petróleo e Gás", "Piscicultura", "Produção Cultural",
            "Produção Industrial", "Psicologia", "Publicidade e Propaganda",
            "Química", "Radiologia", "Recursos Humanos", "Redes de Computadores",
            "Relações Internacionais", "Relações Públicas", "Saúde",
            "Segurança da Informação", "Segurança do Trabalho",
            "Serviço Social", "Sistemas de Informação", "Sociologia",
            "Suporte Técnico", "Sustentabilidade", "Tecnologia da Informação",
            "Telecomunicações", "Terapia Ocupacional", "Turismo",
            "Urbanismo", "Vendas", "Zootecnia"
    };

    private CurriculoDAO dao;
    private Curriculo curriculoAtual;
    private FirebaseUser usuarioFirebase; // Para pegar dados do Firebase
    private String emailUsuarioLogado;
    private final String SEPARADOR = "##";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_usuario);

        // 1. VERIFICA E PEGA DADOS DO FIREBASE
        usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioFirebase != null) {
            emailUsuarioLogado = usuarioFirebase.getEmail();
        } else {
            Toast.makeText(this, "Erro: Nenhum usuário logado no Firebase!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dao = new CurriculoDAO(this);
        inicializarComponentes();
        configurarAbas();
        configurarCursosMultiplos();

        // 2. BUSCA NO BANCO DE DADOS LOCAL (SQLite)
        curriculoAtual = dao.buscarPorEmail(emailUsuarioLogado);

        // 3. PREENCHE A TELA (Mesclando Firebase e SQLite)
        if (curriculoAtual != null) {
            // Se já tem cadastro no banco (passou pela AreaUsuario antes), preenche com os dados do banco
            preencherDadosNaTela();

            // Garantia: Se o nome ou email estiverem em branco no banco, puxa do Firebase
            if (editNome.getText().toString().isEmpty() && usuarioFirebase.getDisplayName() != null) {
                editNome.setText(usuarioFirebase.getDisplayName());
            }
            if (editEmail.getText().toString().isEmpty()) {
                editEmail.setText(emailUsuarioLogado);
            }
        } else {
            // Se NÃO tem nada no banco, puxa o Nome e o E-mail que vieram do Cadastro (Firebase)
            editEmail.setText(emailUsuarioLogado);
            if (usuarioFirebase.getDisplayName() != null) {
                editNome.setText(usuarioFirebase.getDisplayName());
            }
        }

        btnSalvar.setOnClickListener(v -> salvarCurriculo());
        btnSair.setOnClickListener(v -> deslogarUsuario());
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNome);
        editBairro = findViewById(R.id.editBairro);
        editTelefone = findViewById(R.id.editTelefone);
        editEmail = findViewById(R.id.editEmail);
        editAreaCursos = findViewById(R.id.editAreaCursos);
        editInstituicao = findViewById(R.id.editInstituicao);
        editPeriodo = findViewById(R.id.editPeriodo);
        editStatus = findViewById(R.id.editStatus);
        editHabilidade = findViewById(R.id.editHabilidade);
        editExperiencia = findViewById(R.id.editExperiencia);
        editResumo = findViewById(R.id.editResumo);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSair = findViewById(R.id.btnSair);
        btnVoltar = findViewById(R.id.btnVoltar);
    }

    private void preencherDadosNaTela() {
        if (curriculoAtual.getDadosPessoais() != null) {
            String[] d = curriculoAtual.getDadosPessoais().split(SEPARADOR);
            if (d.length > 0 && !d[0].isEmpty()) editNome.setText(d[0]);
            if (d.length > 1) editBairro.setText(d[1]);
            if (d.length > 2) editTelefone.setText(d[2]);
            if (d.length > 3 && !d[3].isEmpty()) editEmail.setText(d[3]);
        }
        if (curriculoAtual.getFormacao() != null) {
            String[] f = curriculoAtual.getFormacao().split(SEPARADOR);
            if (f.length > 0) editInstituicao.setText(f[0]);
            if (f.length > 1) editPeriodo.setText(f[1]);
            if (f.length > 2) editStatus.setText(f[2]);
        }

        // Aqui ele puxa a Área do Curso que foi salva no banco de dados pela tela AreaUsuario
        if (curriculoAtual.getObjetivo() != null) {
            editAreaCursos.setText(curriculoAtual.getObjetivo());
        }

        editHabilidade.setText(curriculoAtual.getHabilidade());
        editExperiencia.setText(curriculoAtual.getExperiencia());
        editResumo.setText(curriculoAtual.getResumo());
    }

    private void salvarCurriculo() {
        String emailDigitado = editEmail.getText().toString().trim();
        if (editNome.getText().toString().isEmpty() || emailDigitado.isEmpty()) {
            Toast.makeText(this, "Nome e E-mail obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNovo = (curriculoAtual == null);
        if (isNovo) curriculoAtual = new Curriculo();

        // Salva os dados no formato concatenado
        String dados = editNome.getText() + SEPARADOR + editBairro.getText() + SEPARADOR + editTelefone.getText() + SEPARADOR + emailDigitado;
        String formacao = editInstituicao.getText() + SEPARADOR + editPeriodo.getText() + SEPARADOR + editStatus.getText();

        curriculoAtual.setDadosPessoais(dados);
        curriculoAtual.setFormacao(formacao);
        curriculoAtual.setObjetivo(editAreaCursos.getText().toString());
        curriculoAtual.setHabilidade(editHabilidade.getText().toString());
        curriculoAtual.setExperiencia(editExperiencia.getText().toString());
        curriculoAtual.setResumo(editResumo.getText().toString());
        curriculoAtual.setEmail(emailDigitado);

        if (isNovo) dao.Insert(curriculoAtual); else dao.update(curriculoAtual);
        Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deslogarUsuario() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginCadastro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void configurarCursosMultiplos() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                AREAS
        );
        editAreaCursos.setAdapter(adapter);
        editAreaCursos.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        editAreaCursos.setThreshold(1);

        editAreaCursos.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] partes = s.toString().split(",");
                long preenchidas = 0;
                for (String p : partes) {
                    if (!p.trim().isEmpty()) preenchidas++;
                }
                if (preenchidas > 3) {
                    editAreaCursos.setError("Máximo de 3 áreas atingido");
                } else {
                    editAreaCursos.setError(null);
                }
            }
        });
    }

    private void configurarAbas() {
        configurarAba(findViewById(R.id.btnAbaDadosPessoais), findViewById(R.id.conteudoDadosPessoais), findViewById(R.id.setaDadosPessoais));
        configurarAba(findViewById(R.id.btnAbaCursos), findViewById(R.id.conteudoCursos), findViewById(R.id.setaCursos));
        configurarAba(findViewById(R.id.btnAbaFormacao), findViewById(R.id.conteudoFormacao), findViewById(R.id.setaFormacao));
        configurarAba(findViewById(R.id.btnAbaHabilidade), findViewById(R.id.conteudoHabilidade), findViewById(R.id.setaHabilidade));
        configurarAba(findViewById(R.id.btnAbaExperiencia), findViewById(R.id.conteudoExperiencia), findViewById(R.id.setaExperiencia));
    }

    private void configurarAba(View b, View c, TextView s) {
        if (b != null) b.setOnClickListener(v -> {
            boolean vsv = c.getVisibility() == View.VISIBLE;
            c.setVisibility(vsv ? View.GONE : View.VISIBLE);
            s.setText(vsv ? "▼" : "▲");
        });
    }
}