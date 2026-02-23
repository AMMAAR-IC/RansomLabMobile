package com.ransomlab.utils;

import android.graphics.Color;

public class ColorUtil {
    public static int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int darken(int color, float factor) {
        return Color.rgb(
            (int)(Color.red(color) * factor),
            (int)(Color.green(color) * factor),
            (int)(Color.blue(color) * factor)
        );
    }

    public static int parse(String hex) {
        try { return Color.parseColor(hex); }
        catch (Exception e) { return Color.RED; }
    }
}
