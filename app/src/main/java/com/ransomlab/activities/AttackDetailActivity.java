package com.ransomlab.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ransomlab.R;
import com.ransomlab.models.Attack;
import com.ransomlab.models.AttackRepository;
import com.ransomlab.utils.ColorUtil;

import java.util.List;

public class AttackDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_attack_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        String attackId = getIntent().getStringExtra("attack_id");
        Attack attack = findAttack(attackId);
        if (attack == null) { finish(); return; }

        int color = ColorUtil.parse(attack.color);

        // Header
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvYear = findViewById(R.id.tv_year);
        TextView tvBadge = findViewById(R.id.tv_badge);
        View headerAccent = findViewById(R.id.header_accent);

        tvName.setText(attack.name);
        tvName.setTextColor(color);
        tvYear.setText(String.valueOf(attack.year));
        tvYear.setTextColor(ColorUtil.withAlpha(color, 180));
        tvBadge.setText(attack.technique);
        tvBadge.setTextColor(color);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(ColorUtil.withAlpha(color, 25));
        badgeBg.setCornerRadius(24);
        badgeBg.setStroke(1, ColorUtil.withAlpha(color, 100));
        tvBadge.setBackground(badgeBg);

        headerAccent.setBackgroundColor(color);

        // Stats grid
        bindStat(R.id.tv_damage_val, attack.damage, color);
        bindStat(R.id.tv_victims_val, attack.victims, color);
        bindStat(R.id.tv_ransom_val, attack.ransom, color);
        bindStat(R.id.tv_encryption_val, attack.encryption, color);
        bindStat(R.id.tv_attribution_val, attack.attribution, color);
        bindStat(R.id.tv_vector_val, attack.vector, color);

        // Description
        TextView tvDesc = findViewById(R.id.tv_description);
        tvDesc.setText(attack.description);

        // Phases
        LinearLayout phasesLayout = findViewById(R.id.phases_container);
        for (int i = 0; i < attack.phases.length; i++) {
            View phaseView = getLayoutInflater().inflate(R.layout.item_phase, phasesLayout, false);

            TextView tvNum = phaseView.findViewById(R.id.tv_phase_num);
            TextView tvPhase = phaseView.findViewById(R.id.tv_phase_text);
            View dot = phaseView.findViewById(R.id.phase_dot);
            View line = phaseView.findViewById(R.id.phase_line);

            tvNum.setText(String.format("%02d", i + 1));
            tvNum.setTextColor(ColorUtil.withAlpha(color, 150));
            tvPhase.setText(attack.phases[i]);
            dot.setBackgroundColor(color);
            if (i == attack.phases.length - 1) line.setVisibility(View.GONE);

            phasesLayout.addView(phaseView);
        }

        // AI button
        View btnAi = findViewById(R.id.btn_ask_ai);
        btnAi.setOnClickListener(v -> {
            Intent i = new Intent(this, AiActivity.class);
            i.putExtra("pre_query", "Tell me about " + attack.name + " ransomware attack");
            startActivity(i);
        });

        GradientDrawable aiBg = new GradientDrawable();
        aiBg.setColor(ColorUtil.withAlpha(color, 30));
        aiBg.setCornerRadius(12);
        aiBg.setStroke(1, color);
        btnAi.setBackground(aiBg);
        ((TextView) btnAi.findViewById(R.id.btn_ai_text)).setTextColor(color);
    }

    private void bindStat(int viewId, String value, int color) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setText(value);
            tv.setTextColor(color);
        }
    }

    private Attack findAttack(String id) {
        if (id == null) return null;
        List<Attack> all = AttackRepository.getAll();
        for (Attack a : all) if (a.id.equals(id)) return a;
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
