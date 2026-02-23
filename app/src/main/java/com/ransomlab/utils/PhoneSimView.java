package com.ransomlab.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PhoneSimView extends View {

    public enum Phase { IDLE, INFECTING, ENCRYPTING, RANSOMED, WIPED }

    private Phase phase = Phase.IDLE;
    private int accentColor = Color.RED;
    private String attackName = "";
    private float encryptProgress = 0f;
    private float glitchAlpha = 0f;
    private int glitchY = 0;
    private long animStart = 0;
    private boolean isWiper = false;
    private Runnable onDone;
    private Handler handler = new Handler(Looper.getMainLooper());

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random rng = new Random(42);
    private final Random glitchRng = new Random();

    private ValueAnimator glitchAnim;
    private ValueAnimator encryptAnim;

    private float[] fileAlphas = new float[10];
    private final String[] fileIcons = { "📷", "🎵", "📧", "🗺", "💬", "📞", "🌐", "⚙", "🏦", "🔐" };
    private final String[] fileNames = { "Photos", "Music", "Mail", "Maps", "Chat", "Phone", "Browser", "System", "Bank", "Keys" };

    public PhoneSimView(Context ctx) { super(ctx); init(); }
    public PhoneSimView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public PhoneSimView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        for (int i = 0; i < fileAlphas.length; i++) fileAlphas[i] = 1f;
        setWillNotDraw(false);
    }

    // ── Public API ──────────────────────────────────────────────────────────

    public void reset() {
        stopAnims();
        phase = Phase.IDLE;
        glitchAlpha = 0f;
        encryptProgress = 0f;
        attackName = "";
        for (int i = 0; i < fileAlphas.length; i++) fileAlphas[i] = 1f;
        postInvalidate();
    }

    public void simulate(String name, int color, boolean wiper, Runnable done) {
        stopAnims();
        attackName = name;
        accentColor = color;
        isWiper = wiper;
        onDone = done;
        animStart = System.currentTimeMillis();
        for (int i = 0; i < fileAlphas.length; i++) fileAlphas[i] = 1f;

        phase = Phase.INFECTING;
        startGlitch();

        // After 2.5s → encrypting
        handler.postDelayed(() -> {
            phase = Phase.ENCRYPTING;
            startEncrypt();
        }, 2500);

        // After 6s → ransomed or wiped
        handler.postDelayed(() -> {
            stopAnims();
            glitchAlpha = 0f;
            for (int i = 0; i < fileAlphas.length; i++) fileAlphas[i] = 0.08f;
            phase = wiper ? Phase.WIPED : Phase.RANSOMED;
            animStart = System.currentTimeMillis(); // reset for countdown
            postInvalidate();
            // keep redrawing for countdown
            startCountdownRefresh();
            if (done != null) done.run();
        }, 6000);
    }

    // ── Animations ──────────────────────────────────────────────────────────

    private void startGlitch() {
        glitchAnim = ValueAnimator.ofFloat(0f, 1f);
        glitchAnim.setDuration(80);
        glitchAnim.setRepeatCount(ValueAnimator.INFINITE);
        glitchAnim.setRepeatMode(ValueAnimator.REVERSE);
        glitchAnim.setInterpolator(new LinearInterpolator());
        glitchAnim.addUpdateListener(a -> {
            glitchAlpha = (float) a.getAnimatedValue() * 0.7f;
            glitchY = glitchRng.nextInt(Math.max(1, getHeight()));
            postInvalidate();
        });
        glitchAnim.start();
    }

    private void startEncrypt() {
        encryptAnim = ValueAnimator.ofFloat(0f, 1f);
        encryptAnim.setDuration(3500);
        encryptAnim.setInterpolator(new LinearInterpolator());
        encryptAnim.addUpdateListener(a -> {
            encryptProgress = (float) a.getAnimatedValue();
            for (int i = 0; i < fileAlphas.length; i++) {
                float threshold = (float) i / fileAlphas.length;
                if (encryptProgress > threshold) {
                    fileAlphas[i] = Math.max(0.08f, 1f - (encryptProgress - threshold) * fileAlphas.length * 1.2f);
                }
            }
            postInvalidate();
        });
        encryptAnim.start();
    }

    private void startCountdownRefresh() {
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                if (phase == Phase.RANSOMED) {
                    postInvalidate();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void stopAnims() {
        handler.removeCallbacksAndMessages(null);
        if (glitchAnim != null) glitchAnim.cancel();
        if (encryptAnim != null) encryptAnim.cancel();
    }

    // ── Draw ────────────────────────────────────────────────────────────────

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();

        // Phone dimensions — centered, 60% width
        int pw = (int)(w * 0.72f);
        int ph = (int)(pw * 2.1f);
        ph = Math.min(ph, h - 20);
        pw = (int)(ph / 2.1f);
        int px = (w - pw) / 2;
        int py = (h - ph) / 2;

        drawPhoneBody(canvas, px, py, pw, ph);

        int sx = px + pw / 14;
        int sy = py + ph / 9;
        int sw = pw - pw / 7;
        int sh = ph - ph / 5;

        drawScreen(canvas, sx, sy, sw, sh);
    }

    private void drawPhoneBody(Canvas c, int x, int y, int w, int h) {
        RectF body = new RectF(x, y, x + w, y + h);

        // Shadow
        paint.setColor(Color.argb(100, 0, 0, 0));
        c.drawRoundRect(new RectF(x + 6, y + 6, x + w + 6, y + h + 6), 32, 32, paint);

        // Body
        Paint grad = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient lg = new LinearGradient(x, y, x + w, y + h,
            Color.rgb(22, 30, 50), Color.rgb(10, 15, 28), Shader.TileMode.CLAMP);
        grad.setShader(lg);
        c.drawRoundRect(body, 32, 32, grad);

        // Border glow
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(phase == Phase.RANSOMED ? 3f : 1.5f);
        paint.setColor(phase == Phase.RANSOMED ? accentColor :
                       phase == Phase.WIPED ? Color.rgb(30, 30, 30) :
                       Color.rgb(42, 55, 80));
        c.drawRoundRect(body, 32, 32, paint);
        paint.setStyle(Paint.Style.FILL);

        // Speaker bar
        paint.setColor(Color.rgb(20, 28, 45));
        c.drawRoundRect(new RectF(x + w * 0.3f, y + h * 0.025f, x + w * 0.7f, y + h * 0.032f), 4, 4, paint);

        // Camera dot
        paint.setColor(Color.rgb(14, 22, 38));
        c.drawCircle(x + w * 0.72f, y + h * 0.028f, w * 0.04f, paint);

        // Home indicator
        paint.setColor(Color.rgb(35, 45, 68));
        c.drawRoundRect(new RectF(x + w * 0.35f, y + h * 0.965f, x + w * 0.65f, y + h * 0.971f), 4, 4, paint);

        // Glitch overlay on body
        if (glitchAlpha > 0) {
            paint.setColor(ColorUtil.withAlpha(accentColor, (int)(glitchAlpha * 60)));
            c.drawRect(x, glitchY, x + w, glitchY + 4, paint);
        }
    }

    private void drawScreen(Canvas c, int sx, int sy, int sw, int sh) {
        // Clip to screen
        c.save();
        Path clip = new Path();
        clip.addRoundRect(new RectF(sx, sy, sx + sw, sy + sh), 8, 8, Path.Direction.CW);
        c.clipPath(clip);

        switch (phase) {
            case IDLE -> drawIdle(c, sx, sy, sw, sh);
            case INFECTING -> drawInfecting(c, sx, sy, sw, sh);
            case ENCRYPTING -> drawEncrypting(c, sx, sy, sw, sh);
            case RANSOMED -> drawRansomed(c, sx, sy, sw, sh);
            case WIPED -> drawWiped(c, sx, sy, sw, sh);
        }

        c.restore();
    }

    private void drawIdle(Canvas c, int sx, int sy, int sw, int sh) {
        // Wallpaper
        Paint wp = new Paint();
        wp.setShader(new LinearGradient(sx, sy, sx, sy + sh,
            Color.rgb(7, 14, 35), Color.rgb(12, 22, 50), Shader.TileMode.CLAMP));
        c.drawRect(sx, sy, sx + sw, sy + sh, wp);

        // Status bar
        textPaint.setColor(Color.rgb(140, 160, 200));
        textPaint.setTextSize(sw * 0.07f);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        c.drawText("9:41", sx + sw * 0.06f, sy + sh * 0.04f, textPaint);

        // Battery bar
        paint.setColor(Color.rgb(0, 220, 100));
        c.drawRoundRect(new RectF(sx + sw * 0.72f, sy + sh * 0.018f, sx + sw * 0.88f, sy + sh * 0.033f), 3, 3, paint);

        // Clock
        textPaint.setColor(Color.rgb(232, 238, 255));
        textPaint.setTextSize(sw * 0.22f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        c.drawText(time, sx + sw / 2f, sy + sh * 0.18f, textPaint);

        textPaint.setTextSize(sw * 0.07f);
        textPaint.setFakeBoldText(false);
        textPaint.setColor(Color.rgb(100, 120, 160));
        String date = new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(new Date());
        c.drawText(date, sx + sw / 2f, sy + sh * 0.22f, textPaint);

        // App grid
        drawAppGrid(c, sx, sy, sw, sh);

        // Status label
        textPaint.setColor(Color.argb(160, 0, 200, 255));
        textPaint.setTextSize(sw * 0.055f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText("◉  SECURE  ·  SELECT ATTACK", sx + sw / 2f, sy + sh * 0.97f, textPaint);
    }

    private void drawAppGrid(Canvas c, int sx, int sy, int sw, int sh) {
        int cols = 3, rows = 3;
        float iconSize = sw * 0.22f;
        float gap = sw * 0.06f;
        float totalW = cols * iconSize + (cols - 1) * gap;
        float startX = sx + (sw - totalW) / 2f;
        float startY = sy + sh * 0.28f;

        int[] iconColors = {
            Color.rgb(25, 60, 100), Color.rgb(60, 25, 80), Color.rgb(25, 80, 50),
            Color.rgb(80, 50, 20), Color.rgb(20, 70, 70), Color.rgb(70, 25, 25),
            Color.rgb(40, 40, 80), Color.rgb(50, 70, 20), Color.rgb(25, 55, 90), Color.rgb(80, 30, 60)
        };

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int idx = row * cols + col;
                if (idx >= fileIcons.length) break;

                float ix = startX + col * (iconSize + gap);
                float iy = startY + row * (iconSize + gap + sh * 0.04f);

                float alpha = fileAlphas[idx];

                paint.setAlpha((int)(255 * alpha));
                paint.setColor(idx < iconColors.length ? iconColors[idx] : Color.DKGRAY);

                if (alpha < 0.5f) {
                    // Encrypted — show lock color
                    paint.setColor(ColorUtil.withAlpha(accentColor, (int)(200 * alpha)));
                }
                c.drawRoundRect(new RectF(ix, iy, ix + iconSize, iy + iconSize), 10, 10, paint);

                // Label
                textPaint.setAlpha((int)(255 * alpha));
                textPaint.setTextSize(iconSize * 0.28f);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setColor(Color.rgb(180, 190, 220));
                c.drawText(alpha < 0.4f ? "🔒" : fileNames[idx], ix + iconSize / 2, iy + iconSize * 0.68f, textPaint);
                paint.setAlpha(255);
                textPaint.setAlpha(255);
            }
        }
    }

    private void drawInfecting(Canvas c, int sx, int sy, int sw, int sh) {
        // Terminal background
        paint.setColor(Color.rgb(3, 7, 15));
        c.drawRect(sx, sy, sx + sw, sy + sh, paint);

        // Scanline
        if (glitchAlpha > 0) {
            paint.setColor(ColorUtil.withAlpha(accentColor, (int)(glitchAlpha * 40)));
            c.drawRect(sx, glitchY % sh + sy, sx + sw, glitchY % sh + sy + 2, paint);
        }

        long elapsed = System.currentTimeMillis() - animStart;
        String[] lines = {
            "> SYSTEM COMPROMISE INITIATED",
            "> ESTABLISHING C2 CONNECTION...",
            "> DOWNLOADING PAYLOAD...",
            "> BYPASSING ANTIVIRUS ENGINE...",
            "> ESCALATING PRIVILEGES...",
            "> DISABLING SHADOW COPIES...",
            "> ENUMERATING FILE SYSTEM...",
            "> SPREADING TO NETWORK SHARES...",
            "> " + attackName.toUpperCase() + " LOADING..."
        };

        int shown = Math.min((int)(elapsed / 260) + 1, lines.length);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setFakeBoldText(false);
        textPaint.setTextAlign(Paint.Align.LEFT);
        float ts = sw * 0.065f;
        textPaint.setTextSize(ts);

        for (int i = 0; i < shown; i++) {
            boolean isLast = (i == shown - 1);
            float blink = isLast ? 0.5f + 0.5f * (float) Math.sin(elapsed * 0.01) : 1f;
            textPaint.setColor(isLast
                ? ColorUtil.withAlpha(accentColor, (int)(255 * blink))
                : Color.rgb(0, 200, 80));
            c.drawText(lines[i], sx + sw * 0.04f, sy + sh * 0.06f + i * (ts * 1.6f), textPaint);
        }
    }

    private void drawEncrypting(Canvas c, int sx, int sy, int sw, int sh) {
        paint.setColor(Color.rgb(5, 8, 18));
        c.drawRect(sx, sy, sx + sw, sy + sh, paint);

        drawAppGrid(c, sx, sy, sw, sh);

        // Progress bar background
        float barX = sx + sw * 0.06f;
        float barY = sy + sh * 0.88f;
        float barW = sw * 0.88f;
        float barH = sh * 0.018f;
        paint.setColor(Color.rgb(30, 40, 60));
        c.drawRoundRect(new RectF(barX, barY, barX + barW, barY + barH), 4, 4, paint);

        // Fill
        paint.setColor(accentColor);
        c.drawRoundRect(new RectF(barX, barY, barX + barW * encryptProgress, barY + barH), 4, 4, paint);

        // % text
        textPaint.setColor(accentColor);
        textPaint.setTextSize(sw * 0.15f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText(String.format("%d%%", (int)(encryptProgress * 100)), sx + sw / 2f, sy + sh * 0.84f, textPaint);

        textPaint.setTextSize(sw * 0.06f);
        textPaint.setFakeBoldText(false);
        textPaint.setColor(Color.rgb(100, 110, 140));
        c.drawText("ENCRYPTING WITH AES-256...", sx + sw / 2f, sy + sh * 0.935f, textPaint);
    }

    private void drawRansomed(Canvas c, int sx, int sy, int sw, int sh) {
        // Dark red bg
        Paint bgP = new Paint();
        bgP.setShader(new LinearGradient(sx, sy, sx, sy + sh,
            Color.rgb(25, 4, 4), Color.rgb(10, 2, 2), Shader.TileMode.CLAMP));
        c.drawRect(sx, sy, sx + sw, sy + sh, bgP);

        // Skull
        textPaint.setTextSize(sw * 0.25f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(false);
        c.drawText("💀", sx + sw / 2f, sy + sh * 0.13f, textPaint);

        // Title
        textPaint.setColor(accentColor);
        textPaint.setTextSize(sw * 0.08f);
        textPaint.setFakeBoldText(true);
        textPaint.setTypeface(Typeface.MONOSPACE);
        c.drawText("YOUR FILES ARE ENCRYPTED", sx + sw / 2f, sy + sh * 0.20f, textPaint);

        // Divider
        paint.setColor(ColorUtil.withAlpha(accentColor, 80));
        c.drawRect(sx + sw * 0.08f, sy + sh * 0.225f, sx + sw * 0.92f, sy + sh * 0.228f, paint);

        // Attack badge
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(sw * 0.065f);
        textPaint.setColor(ColorUtil.withAlpha(accentColor, 180));
        c.drawText("[ " + attackName.toUpperCase() + " ]", sx + sw / 2f, sy + sh * 0.265f, textPaint);

        // Message
        String[] msg = {
            "All files encrypted with",
            "RSA-2048 + AES-256.",
            "",
            "Send payment to recover:"
        };
        textPaint.setTextSize(sw * 0.062f);
        textPaint.setColor(Color.rgb(180, 60, 60));
        for (int i = 0; i < msg.length; i++) {
            c.drawText(msg[i], sx + sw / 2f, sy + sh * 0.315f + i * sw * 0.075f, textPaint);
        }

        // BTC address
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(sw * 0.055f);
        c.drawText("1BvBMSEYstW...kd8XQ", sx + sw / 2f, sy + sh * 0.56f, textPaint);

        // Timer
        long secs = Math.max(0, 72 * 3600 - (System.currentTimeMillis() - animStart) / 1000);
        String timer = String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
        textPaint.setColor(Color.rgb(255, 40, 40));
        textPaint.setTextSize(sw * 0.13f);
        textPaint.setFakeBoldText(true);
        c.drawText(timer, sx + sw / 2f, sy + sh * 0.67f, textPaint);

        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(sw * 0.055f);
        textPaint.setColor(Color.rgb(100, 100, 140));
        c.drawText("TIME REMAINING", sx + sw / 2f, sy + sh * 0.71f, textPaint);

        // Amount
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(sw * 0.075f);
        textPaint.setFakeBoldText(true);
        c.drawText("0.5 BTC  ≈ $14,000", sx + sw / 2f, sy + sh * 0.78f, textPaint);

        // Sim badge
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(sw * 0.052f);
        textPaint.setColor(Color.rgb(0, 200, 100));
        c.drawText("⚡ SIMULATION — NO REAL HARM", sx + sw / 2f, sy + sh * 0.97f, textPaint);
    }

    private void drawWiped(Canvas c, int sx, int sy, int sw, int sh) {
        paint.setColor(Color.rgb(2, 2, 2));
        c.drawRect(sx, sy, sx + sw, sy + sh, paint);

        textPaint.setColor(Color.rgb(35, 35, 35));
        textPaint.setTextSize(sw * 0.065f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.MONOSPACE);

        String[] lines = { "MBR OVERWRITTEN", "PARTITION TABLE DESTROYED", "SYSTEM UNRECOVERABLE", "", "[NotPetya/WIPER MODE]", "NO RECOVERY POSSIBLE" };
        for (int i = 0; i < lines.length; i++) {
            c.drawText(lines[i], sx + sw / 2f, sy + sh * 0.35f + i * sw * 0.1f, textPaint);
        }
    }
}
