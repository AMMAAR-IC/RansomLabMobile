package com.ransomlab.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private int phoneClicks = 0;

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

        // --- EASTER EGG 1: Name Change ---
        // Make sure to add android:id="@+id/tv_main_logo_title" to your TextView in activity_main.xml!
        TextView tvMainLogo = findViewById(R.id.tv_main_logo_title);
        if (tvMainLogo != null) {
            tvMainLogo.setOnClickListener(v -> {
                tvMainLogo.setText("◈  AMMAAR-IC");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    tvMainLogo.setText("◈  RANSOMLAB");
                }, 5000); // Reverts after 5 seconds
            });
        }

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
        // Use getColorHex() instead of .color
        int color = ColorUtil.parse(attack.getColorHex());
        // Use getName() instead of .name
        setStatus("▶  SIMULATING: " + attack.getName().toUpperCase(), color);

        // Use getName(), getColorHex(), and isWiper()
        phoneSimView.simulate(attack.getName(), color, attack.isWiper(), () -> {
            runOnUiThread(() -> {
                // Use isWiper() and getName()
                if (attack.isWiper()) {
                    setStatus("☠  WIPED — " + attack.getName() + " (MBR Destroyed)", Color.rgb(80, 80, 80));
                } else {
                    setStatus("🔒  RANSOMED — " + attack.getName() + " complete", color);
                }
                isSimulating = false;
            });
        });

        // Open detail bottom sheet after 18 seconds!
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(this, AttackDetailActivity.class);
            // Use getId() instead of .id
            i.putExtra("attack_id", attack.getId());
            startActivity(i);
            overridePendingTransition(R.anim.slide_up, R.anim.fade_stay);
        }, 18000);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_ai) {
            startActivity(new Intent(this, AiActivity.class));
            return true;
        } else if (id == R.id.action_contributors) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Contributors")
                    .setMessage("• Ammaar\n• Ayaan")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(android.net.Uri.parse("mailto:the.ammaar.ic@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "RansomLab App Inquiry");
            startActivity(Intent.createChooser(intent, "Send Email"));
            return true;
        } else if (id == R.id.action_linkedin) {
            startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.linkedin.com/in/ammaar-ic")));
            return true;
        } else if (id == R.id.action_github) {
            startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/AMMAAR-IC")));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}