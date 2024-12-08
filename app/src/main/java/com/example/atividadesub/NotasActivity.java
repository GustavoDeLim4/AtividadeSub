package com.example.atividadesub;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class NotasActivity extends AppCompatActivity {

    private Spinner spAluno;
    private TableLayout tblNotas;
    private SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        spAluno = findViewById(R.id.sp_aluno);
        tblNotas = findViewById(R.id.tbl_notas);

        // Inicializar banco de dados
        database = openOrCreateDatabase("CadastroAlunos", MODE_PRIVATE, null);

        // Preencher spinner com alunos
        carregarAlunos();

        spAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarNotas(spAluno.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada
            }
        });
    }

    private void carregarAlunos() {
        Cursor cursor = database.rawQuery("SELECT nome FROM Aluno", null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        while (cursor.moveToNext()) {
            adapter.add(cursor.getString(0));
        }
        cursor.close();

        spAluno.setAdapter(adapter);
    }

    private void carregarNotas(String alunoNome) {
        // Limpar tabela
        tblNotas.removeViews(1, tblNotas.getChildCount() - 1);

        Cursor cursor = database.rawQuery(
                "SELECT Nota.disciplina, Nota.bimestre, Nota.nota " +
                        "FROM Nota INNER JOIN Aluno ON Nota.aluno_id = Aluno.id " +
                        "WHERE Aluno.nome = ?", new String[]{alunoNome});

        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                TextView tv = new TextView(this);
                tv.setText(cursor.getString(i));
                tv.setPadding(8, 8, 8, 8);
                row.addView(tv);
            }

            tblNotas.addView(row);
        }
        cursor.close();
    }
}
