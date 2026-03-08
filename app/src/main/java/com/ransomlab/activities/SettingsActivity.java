package com.ransomlab.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ransomlab.R;
import com.ransomlab.utils.Prefs;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        EditText etUrl = findViewById(R.id.et_ollama_url);
        TextView tvStatus = findViewById(R.id.tv_connection_status);
        View btnSave = findViewById(R.id.btn_save);
        View btnTest = findViewById(R.id.btn_test);

        View btnContact = findViewById(R.id.btn_contact_us);
        View btnLinkedin = findViewById(R.id.btn_linkedin);
        View btnGithub = findViewById(R.id.btn_github);

        etUrl.setText(Prefs.getOllamaUrl(this));

        btnSave.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) { Toast.makeText(this, "Enter a URL", Toast.LENGTH_SHORT).show(); return; }
            Prefs.setOllamaUrl(this, url);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });

        btnTest.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            tvStatus.setText("Testing connection...");
            tvStatus.setTextColor(Color.rgb(255, 165, 0));

            new Thread(() -> {
                try {
                    String endpoint = url.trim();
                    if (!endpoint.startsWith("http")) endpoint = "http://" + endpoint;
                    if (!endpoint.endsWith("/")) endpoint += "/";
                    endpoint += "api/tags";

                    java.net.URL u = new java.net.URL(endpoint);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    conn.disconnect();

                    boolean ok = code == 200;
                    runOnUiThread(() -> {
                        tvStatus.setText(ok ? "✓ Connected to Ollama!" : "✗ HTTP " + code);
                        tvStatus.setTextColor(ok ? Color.rgb(0, 200, 100) : Color.rgb(255, 60, 60));
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        tvStatus.setText("✗ Cannot connect: " + e.getMessage());
                        tvStatus.setTextColor(Color.rgb(255, 60, 60));
                    });
                }
            }).start();
        });

        btnContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:the.ammaar.ic@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "RansomLab Inquiry");
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        btnLinkedin.setOnClickListener(v -> openUrl("https://www.linkedin.com/in/ammaar-ic"));
        btnGithub.setOnClickListener(v -> openUrl("https://github.com/AMMAAR-IC"));
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
