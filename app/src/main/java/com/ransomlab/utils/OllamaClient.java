package com.ransomlab.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OllamaClient {

    private static final String TAG = "OllamaClient";
    private static final String MODEL = "kimi-k2:1t-cloud";

    public interface StreamCallback {
        void onChunk(String text);
        void onDone();
        void onError(String message);
    }

    public static Thread streamChat(String baseUrl, String userMessage, StreamCallback callback) {
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // Build endpoint
                String endpoint = baseUrl.trim();
                if (!endpoint.startsWith("http")) endpoint = "http://" + endpoint;
                if (!endpoint.endsWith("/")) endpoint += "/";
                endpoint += "api/chat";

                URL url = new URL(endpoint);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(120000);

                // System prompt
                String system = "You are a cybersecurity threat intelligence expert specializing in ransomware analysis. " +
                    "When asked about any ransomware variant or attack, provide concise analysis covering: " +
                    "1) Attribution & history, 2) Attack vector & TTPs (MITRE ATT&CK), " +
                    "3) Encryption method, 4) Notable victims, 5) Detection & mitigation. " +
                    "Keep responses under 400 words. Use technical language. Format clearly with sections.";

                // JSON payload
                JSONObject payload = new JSONObject();
                payload.put("model", MODEL);
                payload.put("stream", true);

                JSONArray messages = new JSONArray();

                JSONObject sysMsg = new JSONObject();
                sysMsg.put("role", "system");
                sysMsg.put("content", system);
                messages.put(sysMsg);

                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", userMessage);
                messages.put(userMsg);

                payload.put("messages", messages);

                // Write body
                byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
                OutputStream os = conn.getOutputStream();
                os.write(body);
                os.flush();

                // Check response code
                int code = conn.getResponseCode();
                if (code != 200) {
                    callback.onError("Server returned HTTP " + code + " — is Ollama running?");
                    return;
                }

                // Stream response
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    try {
                        JSONObject json = new JSONObject(line);
                        if (json.has("message")) {
                            JSONObject msg = json.getJSONObject("message");
                            if (msg.has("content")) {
                                String chunk = msg.getString("content");
                                if (!chunk.isEmpty()) callback.onChunk(chunk);
                            }
                        }
                        if (json.optBoolean("done", false)) break;
                    } catch (Exception e) {
                        Log.w(TAG, "Parse error on line: " + line);
                    }
                }
                reader.close();
                callback.onDone();

            } catch (java.net.ConnectException e) {
                callback.onError("Cannot connect to Ollama.\n\nMake sure:\n• Ollama is running on your PC\n• Both devices on same WiFi\n• Run: ollama serve\n• Pull model: ollama pull kimi-k2-cloud");
            } catch (java.net.SocketTimeoutException e) {
                callback.onError("Connection timed out.\nCheck Ollama server is running.");
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
                callback.onError("Error: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();
        return thread;
    }
}
