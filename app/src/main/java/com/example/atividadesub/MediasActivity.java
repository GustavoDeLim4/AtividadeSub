package com.example.atividadesub;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MediasActivity extends AppCompatActivity {

    private Spinner spDisciplina;
    private TableLayout tblMedias;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medias);

        spDisciplina = findViewById(R.id.sp_disciplina);
        tblMedias = findViewById(R.id.tbl_medias);

        database = openOrCreateDatabase("CadastroAlunos", MODE_PRIVATE, null);


        carregarDisciplinas();

        spDisciplina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarMedias(spDisciplina.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void carregarDisciplinas() {
        Cursor cursor = database.rawQuery("SELECT DISTINCT disciplina FROM Nota", null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        while (cursor.moveToNext()) {
            adapter.add(cursor.getString(0));
        }
        cursor.close();

        spDisciplina.setAdapter(adapter);
    }

    private void carregarMedias(String disciplina) {

        tblMedias.removeViews(1, tblMedias.getChildCount() - 1);

        Cursor cursor = database.rawQuery(
                "SELECT Aluno.ra, Aluno.nome, AVG(Nota.nota) AS media " +
                        "FROM Nota INNER JOIN Aluno ON Nota.aluno_id = Aluno.id " +
                        "WHERE Nota.disciplina = ? " +
                        "GROUP BY Aluno.id", new String[]{disciplina});

        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                TextView tv = new TextView(this);
                tv.setText(cursor.getString(i));
                tv.setPadding(8, 8, 8, 8);
                row.addView(tv);
            }

            tblMedias.addView(row);
        }
        cursor.close();
    }
}
