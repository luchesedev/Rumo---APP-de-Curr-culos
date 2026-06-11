package com.example.rumo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class ConnectionFactory extends SQLiteOpenHelper {

    // É importante manter a versão como 2 agora que alteramos a estrutura da tabela
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "dbCurriculo.db";

    public ConnectionFactory(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criando a tabela com a coluna email incluída
        String sql = "CREATE TABLE tbcurriculo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "dadosPessoais TEXT, " +
                "objetivo TEXT, " +
                "experiencia TEXT, " +
                "habilidade TEXT, " +
                "formacao TEXT, " +
                "resumo TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Se estivermos atualizando da versão 1 para a 2, apenas adicionamos a coluna
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE tbcurriculo ADD COLUMN email TEXT");
        } else {
            // Caso seja uma atualização mais drástica, recriamos a tabela
            db.execSQL("DROP TABLE IF EXISTS tbcurriculo");
            onCreate(db);
        }
    }
}