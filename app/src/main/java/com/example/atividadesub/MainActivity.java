package com.example.atividadesub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etRA, etNome, etNota;
    private Spinner spDisciplina, spBimestre;
    private Button btnAdicionar, btnVerNotas, btnVerMedias;
    private SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os componentes
        etRA = findViewById(R.id.et_ra);
        etNome = findViewById(R.id.et_nome);
        etNota = findViewById(R.id.et_nota);
        spDisciplina = findViewById(R.id.sp_disciplina);
        spBimestre = findViewById(R.id.sp_bimestre);
        btnAdicionar = findViewById(R.id.btn_adicionar);
        btnVerNotas = findViewById(R.id.btn_ver_notas);
        btnVerMedias = findViewById(R.id.btn_ver_medias);

        // Configurar Spinner
        ArrayAdapter<CharSequence> disciplinaAdapter = ArrayAdapter.createFromResource(this,
                R.array.disciplinas, android.R.layout.simple_spinner_item);
        disciplinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDisciplina.setAdapter(disciplinaAdapter);

        ArrayAdapter<CharSequence> bimestreAdapter = ArrayAdapter.createFromResource(this,
                R.array.bimestres, android.R.layout.simple_spinner_item);
        bimestreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBimestre.setAdapter(bimestreAdapter);

        // Inicializar banco de dados
        database = openOrCreateDatabase("CadastroAlunos", MODE_PRIVATE, null);
        criarTabelas();

        // Ação do botão Adicionar
        btnAdicionar.setOnClickListener(view -> adicionarNota());

        // Navegação
        btnVerNotas.setOnClickListener(view -> startActivity(new Intent(this, NotasActivity.class)));
        btnVerMedias.setOnClickListener(view -> startActivity(new Intent(this, MediasActivity.class)));
    }

    private void criarTabelas() {
        database.execSQL("CREATE TABLE IF NOT EXISTS Aluno (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ra TEXT, " +
                "nome TEXT)");

        database.execSQL("CREATE TABLE IF NOT EXISTS Nota (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "aluno_id INTEGER, " +
                "disciplina TEXT, " +
                "nota REAL, " +
                "bimestre TEXT, " +
                "FOREIGN KEY(aluno_id) REFERENCES Aluno(id))");
    }

    private void adicionarNota() {
        String ra = etRA.getText().toString();
        String nome = etNome.getText().toString();
        String disciplina = spDisciplina.getSelectedItem().toString();
        String bimestre = spBimestre.getSelectedItem().toString();
        String notaStr = etNota.getText().toString();

        if (ra.isEmpty() || nome.isEmpty() || notaStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double nota = Double.parseDouble(notaStr);

        // Verificar se aluno já existe
        long alunoId = inserirOuBuscarAluno(ra, nome);

        // Inserir nota
        database.execSQL("INSERT INTO Nota (aluno_id, disciplina, nota, bimestre) VALUES (?, ?, ?, ?)",
                new Object[]{alunoId, disciplina, nota, bimestre});

        Toast.makeText(this, "Nota adicionada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private long inserirOuBuscarAluno(String ra, String nome) {
        // Inserir aluno ou ignorar se já existir
        SQLiteStatement stmt = database.compileStatement(
                "INSERT OR IGNORE INTO Aluno (ra, nome) VALUES (?, ?)");
        stmt.bindString(1, ra);
        stmt.bindString(2, nome);
        long idInserido = stmt.executeInsert();

        // Se não foi inserido, buscar o ID do aluno existente
        if (idInserido == -1) {
            Cursor cursor = database.rawQuery("SELECT id FROM Aluno WHERE ra = ?", new String[]{ra});
            if (cursor.moveToFirst()) {
                idInserido = cursor.getLong(0);
            }
            cursor.close();
        }

        return idInserido;
    }
}
