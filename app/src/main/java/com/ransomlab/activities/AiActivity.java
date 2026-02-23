package com.ransomlab.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ransomlab.R;
import com.ransomlab.utils.OllamaClient;
import com.ransomlab.utils.Prefs;

public class AiActivity extends AppCompatActivity {

    private TextView tvOutput;
    private EditText etInput;
    private ScrollView scrollView;
    private View btnSend;
    private View btnClear;
    private TextView tvStatus;
    private Thread currentThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_ai);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Threat Intelligence");
        }

        tvOutput = findViewById(R.id.tv_output);
        etInput  = findViewById(R.id.et_input);
        scrollView = findViewById(R.id.scroll_output);
        btnSend  = findViewById(R.id.btn_send);
        btnClear = findViewById(R.id.btn_clear);
        tvStatus = findViewById(R.id.tv_ai_status);

        tvOutput.setText("» Ready. Connected to Ollama via WiFi.\n» Model: kimi-k2-cloud\n» Server: " + Prefs.getOllamaUrl(this) + "\n\n» Type any ransomware name or threat query below.\n");

        btnSend.setOnClickListener(v -> sendQuery());
        etInput.setOnEditorActionListener((v, action, event) -> { sendQuery(); return true; });

        btnClear.setOnClickListener(v -> {
            tvOutput.setText("» Cleared.\n");
            tvStatus.setText("READY");
            tvStatus.setTextColor(Color.rgb(0, 200, 100));
        });

        // Pre-fill query if coming from attack detail
        String preQuery = getIntent().getStringExtra("pre_query");
        if (preQuery != null) {
            etInput.setText(preQuery);
            sendQuery();
        }
    }

    private void sendQuery() {
        String query = etInput.getText().toString().trim();
        if (query.isEmpty()) return;

        etInput.setText("");
        hideKeyboard();

        // Cancel any running query
        if (currentThread != null && currentThread.isAlive()) {
            currentThread.interrupt();
        }

        btnSend.setEnabled(false);
        tvStatus.setText("QUERYING...");
        tvStatus.setTextColor(Color.rgb(255, 165, 0));

        tvOutput.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        tvOutput.append("» YOU: " + query + "\n\n");
        tvOutput.append("» AI: ");
        scrollToBottom();

        String url = Prefs.getOllamaUrl(this);

        currentThread = OllamaClient.streamChat(url, query, new OllamaClient.StreamCallback() {
            @Override public void onChunk(String text) {
                runOnUiThread(() -> {
                    tvOutput.append(text);
                    scrollToBottom();
                });
            }

            @Override public void onDone() {
                runOnUiThread(() -> {
                    tvOutput.append("\n");
                    btnSend.setEnabled(true);
                    tvStatus.setText("DONE");
                    tvStatus.setTextColor(Color.rgb(0, 200, 100));
                    scrollToBottom();
                });
            }

            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    tvOutput.append("\n⚠ " + message + "\n");
                    btnSend.setEnabled(true);
                    tvStatus.setText("ERROR");
                    tvStatus.setTextColor(Color.rgb(255, 50, 50));
                    scrollToBottom();
                });
            }
        });
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (currentThread != null) currentThread.interrupt();
    }
}
