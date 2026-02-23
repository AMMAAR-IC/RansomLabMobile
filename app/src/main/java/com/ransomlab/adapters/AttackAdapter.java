package com.ransomlab.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ransomlab.R;
import com.ransomlab.models.Attack;
import com.ransomlab.utils.ColorUtil;

import java.util.List;

public class AttackAdapter extends RecyclerView.Adapter<AttackAdapter.VH> {

    public interface OnClick { void onClick(Attack attack); }

    private final List<Attack> attacks;
    private final OnClick listener;

    public AttackAdapter(List<Attack> attacks, OnClick listener) {
        this.attacks = attacks;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_attack, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Attack a = attacks.get(pos);
        int color = ColorUtil.parse(a.color);

        h.name.setText(a.name);
        h.year.setText(String.valueOf(a.year));
        h.technique.setText(a.technique);
        h.vector.setText(a.vector);

        // Left accent bar
        h.accentBar.setBackgroundColor(color);

        // Card border glow
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(13, 19, 31));
        bg.setCornerRadius(12);
        bg.setStroke(1, ColorUtil.withAlpha(color, 80));
        h.card.setBackground(bg);

        h.name.setTextColor(Color.rgb(232, 238, 255));
        h.year.setTextColor(color);
        h.technique.setTextColor(color);
        h.vector.setTextColor(Color.rgb(90, 105, 140));

        h.itemView.setOnClickListener(v -> listener.onClick(a));

        // Ripple-style feedback via alpha
        h.itemView.setOnLongClickListener(v -> { listener.onClick(a); return true; });
    }

    @Override public int getItemCount() { return attacks.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View card, accentBar;
        TextView name, year, technique, vector;
        VH(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            accentBar = v.findViewById(R.id.accent_bar);
            name = v.findViewById(R.id.tv_name);
            year = v.findViewById(R.id.tv_year);
            technique = v.findViewById(R.id.tv_technique);
            vector = v.findViewById(R.id.tv_vector);
        }
    }
}
