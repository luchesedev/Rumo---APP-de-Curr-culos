package com.example.rumo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rumo.dao.CurriculoDAO;
import com.example.rumo.model.Curriculo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AreaUsuario extends Tela_Base {

    private TextInputEditText editNome, editBairro, editTelefone, editEmail,
            editInstituicao, editPeriodo, editStatus,
            editHabilidade, editExperiencia, editResumo;
    private MultiAutoCompleteTextView editAreaCursos;
    private Button btnSalvar;

    private CurriculoDAO dao;
    private FirebaseUser usuarioFirebase;
    private String emailUsuarioLogado;
    private final String SEPARADOR = "##";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioFirebase == null) {
            finish();
            return;
        }
        emailUsuarioLogado = usuarioFirebase.getEmail();
        dao = new CurriculoDAO(this);

        inicializarComponentes();
        configurarAbas();
        configurarCursosMultiplos();

        editEmail.setText(emailUsuarioLogado);
        if (usuarioFirebase.getDisplayName() != null) {
            editNome.setText(usuarioFirebase.getDisplayName());
        }

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        boolean valido = true;

        if (editNome.getText().toString().trim().isEmpty()) {
            editNome.setError("Preencha seu nome");
            valido = false;
        }
        if (editBairro.getText().toString().trim().isEmpty()) {
            editBairro.setError("Preencha seu bairro/cidade");
            valido = false;
        }
        if (editTelefone.getText().toString().trim().isEmpty()) {
            editTelefone.setError("Preencha seu telefone");
            valido = false;
        }
        if (editEmail.getText().toString().trim().isEmpty()) {
            editEmail.setError("Preencha seu e-mail");
            valido = false;
        }

        // Valida área — máximo 3
        String areas = editAreaCursos.getText().toString().trim();
        if (areas.isEmpty()) {
            editAreaCursos.setError("Preencha sua área de atuação");
            valido = false;
        } else {
            String[] selecionadas = areas.split(",");
            if (selecionadas.length > 3) {
                editAreaCursos.setError("Selecione no máximo 3 áreas");
                valido = false;
            }
        }

        if (editInstituicao.getText().toString().trim().isEmpty()) {
            editInstituicao.setError("Preencha a instituição");
            valido = false;
        }
        if (editPeriodo.getText().toString().trim().isEmpty()) {
            editPeriodo.setError("Preencha o semestre");
            valido = false;
        }
        if (editStatus.getText().toString().trim().isEmpty()) {
            editStatus.setError("Preencha o status");
            valido = false;
        }
        if (editHabilidade.getText().toString().trim().isEmpty()) {
            editHabilidade.setError("Preencha suas habilidades");
            valido = false;
        }
        if (editExperiencia.getText().toString().trim().isEmpty()) {
            editExperiencia.setError("Preencha sua experiência");
            valido = false;
        }
        if (editResumo.getText().toString().trim().isEmpty()) {
            editResumo.setError("Preencha seu resumo");
            valido = false;
        }

        if (!valido) {
            Toast.makeText(this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailDigitado = editEmail.getText().toString().trim();
        String dados = editNome.getText() + SEPARADOR +
                editBairro.getText() + SEPARADOR +
                editTelefone.getText() + SEPARADOR +
                emailDigitado;

        String formacao = editInstituicao.getText() + SEPARADOR +
                editPeriodo.getText() + SEPARADOR +
                editStatus.getText();

        Curriculo c = new Curriculo();
        c.setEmail(emailDigitado);
        c.setDadosPessoais(dados);
        c.setFormacao(formacao);
        c.setObjetivo(editAreaCursos.getText().toString());
        c.setHabilidade(editHabilidade.getText().toString());
        c.setExperiencia(editExperiencia.getText().toString());
        c.setResumo(editResumo.getText().toString());

        dao.Insert(c);

        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

        Intent it = new Intent(this, Rumo.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
        finish();
    }

    private void inicializarComponentes() {
        editNome        = findViewById(R.id.editNome);
        editBairro      = findViewById(R.id.editBairro);
        editTelefone    = findViewById(R.id.editTelefone);
        editEmail       = findViewById(R.id.editEmail);
        editAreaCursos  = findViewById(R.id.editAreaCursos);
        editInstituicao = findViewById(R.id.editInstituicao);
        editPeriodo     = findViewById(R.id.editPeriodo);
        editStatus      = findViewById(R.id.editStatus);
        editHabilidade  = findViewById(R.id.editHabilidade);
        editExperiencia = findViewById(R.id.editExperiencia);
        editResumo      = findViewById(R.id.editResumo);
        btnSalvar       = findViewById(R.id.btnSalvar);
    }

    private void configurarCursosMultiplos() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                AREAS
        );
        editAreaCursos.setAdapter(adapter);
        editAreaCursos.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        editAreaCursos.setThreshold(1); // mostra sugestões a partir do 1º caractere

        // Bloqueia ao atingir 3 áreas
        editAreaCursos.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString();
                String[] partes = texto.split(",");
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
            boolean visivel = c.getVisibility() == View.VISIBLE;
            c.setVisibility(visivel ? View.GONE : View.VISIBLE);
            s.setText(visivel ? "▼" : "▲");
        });
    }
}