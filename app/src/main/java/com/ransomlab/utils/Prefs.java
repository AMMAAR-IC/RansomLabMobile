package com.ransomlab.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String FILE = "ransomlab_prefs";
    private static final String KEY_OLLAMA_URL = "ollama_url";
    private static final String DEFAULT_URL = "http://192.168.1.100:11434";

    public static String getOllamaUrl(Context ctx) {
        return ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                  .getString(KEY_OLLAMA_URL, DEFAULT_URL);
    }

    public static void setOllamaUrl(Context ctx, String url) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
           .edit().putString(KEY_OLLAMA_URL, url).apply();
    }
}
