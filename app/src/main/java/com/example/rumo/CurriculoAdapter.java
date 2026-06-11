package com.example.rumo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumo.model.Curriculo;

import java.util.List;

public class CurriculoAdapter extends RecyclerView.Adapter<CurriculoAdapter.ViewHolder> {

    private final List<Curriculo> itens;
    private final Context context;

    public CurriculoAdapter(Context context, List<Curriculo> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_curriculo_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Curriculo item = itens.get(position);

        // Nome: usa o objetivo como título do card, ou "Currículo" se vazio
        String titulo = (item.getObjetivo() != null && !item.getObjetivo().isEmpty())
                ? item.getObjetivo()
                : "Currículo " + (position + 1);
        holder.tvNome.setText(titulo);

        // Subtítulo: dados pessoais resumidos ou texto padrão
        String subtitulo = (item.getDadosPessoais() != null && !item.getDadosPessoais().isEmpty())
                ? item.getDadosPessoais()
                : "Sem dados pessoais";
        holder.tvSubtitulo.setText(subtitulo);

        // Badge: verde se tiver resumo preenchido, amarelo se não
        boolean completo = item.getResumo() != null && !item.getResumo().isEmpty();
        if (completo) {
            holder.tvStatus.setText("Completo");
            holder.tvStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
        } else {
            holder.tvStatus.setText("Incompleto");
            holder.tvStatus.setTextColor(0xFFF57F17);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
        }

        // Clique no card — abre tela de manutenção
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManutencaoCurriculo.class);
            intent.putExtra("curriculo_id", item.getId());
            context.startActivity(intent);
        });

        // Clique nos três pontos — também abre manutenção
        holder.ivMenu.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManutencaoCurriculo.class);
            intent.putExtra("curriculo_id", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome;
        TextView tvSubtitulo;
        TextView tvStatus;
        ImageView ivMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome     = itemView.findViewById(R.id.tvCurriculoNome);
            tvSubtitulo = itemView.findViewById(R.id.tvCurriculoSubtitulo);
            tvStatus   = itemView.findViewById(R.id.tvCurriculoStatus);
            ivMenu     = itemView.findViewById(R.id.ivCurriculoMenu);
        }
    }
}