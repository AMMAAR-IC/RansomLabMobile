package com.ransomlab.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ransomlab.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen setup
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.tv_logo);
        View ivLogo   = findViewById(R.id.iv_app_logo);
        TextView sub  = findViewById(R.id.tv_subtitle);
        TextView warn = findViewById(R.id.tv_warning);
        View scanline = findViewById(R.id.scanline);

        // Start all elements invisible
        logo.setAlpha(0f);
        if (ivLogo != null) ivLogo.setAlpha(0f);
        sub.setAlpha(0f);
        warn.setAlpha(0f);

        // 1. Initial fade-in for the text logo
        ObjectAnimator logoAnim = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        logoAnim.setDuration(600);
        logoAnim.setStartDelay(300);

        // 2. The Transformation: Text shrinks/fades out while the Rounded Image grows/fades in
        AnimatorSet transformAnim = new AnimatorSet();
        if (ivLogo != null) {
            ObjectAnimator textFadeOut = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f);
            ObjectAnimator textShrinkX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 0.4f);
            ObjectAnimator textShrinkY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 0.4f);

            ObjectAnimator imgFadeIn = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f);
            ObjectAnimator imgGrowX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.4f, 1f);
            ObjectAnimator imgGrowY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.4f, 1f);

            transformAnim.playTogether(textFadeOut, textShrinkX, textShrinkY, imgFadeIn, imgGrowX, imgGrowY);
            transformAnim.setDuration(500);
            transformAnim.setStartDelay(1300);
        }

        // 3. Subtitle and Warning animations
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(sub, "alpha", 0f, 1f);
        subAnim.setDuration(400);
        subAnim.setStartDelay(800);

        ObjectAnimator warnAnim = ObjectAnimator.ofFloat(warn, "alpha", 0f, 1f);
        warnAnim.setDuration(400);
        warnAnim.setStartDelay(1100);

        // 4. Scanline animation
        ObjectAnimator scan = ObjectAnimator.ofFloat(scanline, "translationY",
                -getResources().getDisplayMetrics().heightPixels,
                getResources().getDisplayMetrics().heightPixels);
        scan.setDuration(1400);
        scan.setStartDelay(200);

        // Play everything together
        AnimatorSet set = new AnimatorSet();
        if (ivLogo != null) {
            set.playTogether(logoAnim, transformAnim, subAnim, warnAnim, scan);
        } else {
            set.playTogether(logoAnim, subAnim, warnAnim, scan);
        }
        set.start();

        // Launch MainActivity after 2.8s
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2800);
    }
}