package com.example.rumo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.rumo.model.Curriculo;
import com.example.rumo.util.ConnectionFactory;

public class CurriculoDAO {
    private ConnectionFactory conexao;
    private SQLiteDatabase banco;

    public CurriculoDAO(Context context) {
        try {
            // Agora o ConnectionFactory cuida do nome e da versão internamente
            conexao = new ConnectionFactory(context);
            banco = conexao.getWritableDatabase();
        } catch (Exception e) {
            Log.e("ERRO_BANCO", "Erro ao abrir banco: " + e.getMessage());
        }
    }

    public long Insert(Curriculo curriculo) {
        ContentValues values = new ContentValues();
        values.put("email", curriculo.getEmail());
        values.put("dadosPessoais", curriculo.getDadosPessoais());
        values.put("objetivo", curriculo.getObjetivo());
        values.put("experiencia", curriculo.getExperiencia());
        values.put("habilidade", curriculo.getHabilidade());
        values.put("formacao", curriculo.getFormacao());
        values.put("resumo", curriculo.getResumo());
        return banco.insert("tbcurriculo", null, values);
    }

    public void update(Curriculo curriculo) {
        ContentValues values = new ContentValues();
        // CORREÇÃO: Você precisa incluir o email no update também!
        values.put("email", curriculo.getEmail());
        values.put("dadosPessoais", curriculo.getDadosPessoais());
        values.put("objetivo", curriculo.getObjetivo());
        values.put("experiencia", curriculo.getExperiencia());
        values.put("habilidade", curriculo.getHabilidade());
        values.put("formacao", curriculo.getFormacao());
        values.put("resumo", curriculo.getResumo());

        String[] args = {String.valueOf(curriculo.getId())};
        banco.update("tbcurriculo", values, "id=?", args);
    }

    public Curriculo buscarPorEmail(String email) {
        // CORREÇÃO: Verifique se o e-mail não é nulo antes de buscar
        if (email == null) return null;

        Cursor cursor = banco.query("tbcurriculo",
                new String[]{"id", "dadosPessoais", "objetivo", "experiencia", "habilidade", "formacao", "resumo", "email"},
                "email = ?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Curriculo c = new Curriculo();
            c.setId(cursor.getInt(0));
            c.setDadosPessoais(cursor.getString(1));
            c.setObjetivo(cursor.getString(2));
            c.setExperiencia(cursor.getString(3));
            c.setHabilidade(cursor.getString(4));
            c.setFormacao(cursor.getString(5));
            c.setResumo(cursor.getString(6));
            c.setEmail(cursor.getString(7)); // Certifique-se de que o set do e-mail existe no modelo
            cursor.close();
            return c;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void delete(Curriculo curriculo) {
        String[] args = {String.valueOf(curriculo.getId())};
        banco.delete("tbcurriculo", "id=?", args);
    }
}