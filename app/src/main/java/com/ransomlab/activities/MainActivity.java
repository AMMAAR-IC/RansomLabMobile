package com.ransomlab.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ransomlab.R;
import com.ransomlab.adapters.AttackAdapter;
import com.ransomlab.models.Attack;
import com.ransomlab.models.AttackRepository;
import com.ransomlab.utils.ColorUtil;
import com.ransomlab.utils.PhoneSimView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PhoneSimView phoneSimView;
    private TextView tvStatus;
    private boolean isSimulating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        phoneSimView = findViewById(R.id.phone_sim);
        tvStatus = findViewById(R.id.tv_status);

        List<Attack> attacks = AttackRepository.getAll();

        RecyclerView rv = findViewById(R.id.rv_attacks);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        AttackAdapter adapter = new AttackAdapter(attacks, attack -> {
            if (!isSimulating) {
                launchAttack(attack);
            }
        });
        rv.setAdapter(adapter);

        // Reset button
        View btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> {
            isSimulating = false;
            phoneSimView.reset();
            setStatus("◉  SECURE  ·  SELECT AN ATTACK", Color.rgb(0, 200, 255));
        });

        // AI button
        View btnAi = findViewById(R.id.btn_ai);
        btnAi.setOnClickListener(v -> {
            startActivity(new Intent(this, AiActivity.class));
        });

        setStatus("◉  SECURE  ·  SELECT AN ATTACK", Color.rgb(0, 200, 255));
    }

    private void launchAttack(Attack attack) {
        isSimulating = true;
        int color = ColorUtil.parse(attack.color);
        setStatus("▶  SIMULATING: " + attack.name.toUpperCase(), color);

        phoneSimView.simulate(attack.name, color, attack.iswiper, () -> {
            runOnUiThread(() -> {
                if (attack.iswiper) {
                    setStatus("☠  WIPED — " + attack.name + " (MBR Destroyed)", Color.rgb(80, 80, 80));
                } else {
                    setStatus("🔒  RANSOMED — " + attack.name + " complete", color);
                }
                isSimulating = false;
            });
        });

        // Open detail bottom sheet after short delay
        new android.os.Handler().postDelayed(() -> {
            Intent i = new Intent(this, AttackDetailActivity.class);
            i.putExtra("attack_id", attack.id);
            startActivity(i);
            overridePendingTransition(R.anim.slide_up, R.anim.fade_stay);
        }, 600);
    }

    private void setStatus(String text, int color) {
        tvStatus.setText(text);
        tvStatus.setTextColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_ai) {
            startActivity(new Intent(this, AiActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
