package com.ransomlab.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

        // Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.rgb(8, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 20));

        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.tv_logo);
        TextView sub   = findViewById(R.id.tv_subtitle);
        TextView warn  = findViewById(R.id.tv_warning);
        View scanline  = findViewById(R.id.scanline);

        // Start invisible
        logo.setAlpha(0f);
        sub.setAlpha(0f);
        warn.setAlpha(0f);

        // Animate in
        ObjectAnimator logoAnim = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        logoAnim.setDuration(600);
        logoAnim.setStartDelay(300);

        ObjectAnimator subAnim = ObjectAnimator.ofFloat(sub, "alpha", 0f, 1f);
        subAnim.setDuration(400);
        subAnim.setStartDelay(800);

        ObjectAnimator warnAnim = ObjectAnimator.ofFloat(warn, "alpha", 0f, 1f);
        warnAnim.setDuration(400);
        warnAnim.setStartDelay(1100);

        // Scanline sweep
        ObjectAnimator scan = ObjectAnimator.ofFloat(scanline, "translationY",
            -getResources().getDisplayMetrics().heightPixels,
            getResources().getDisplayMetrics().heightPixels);
        scan.setDuration(1400);
        scan.setStartDelay(200);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(logoAnim, subAnim, warnAnim, scan);
        set.start();

        // Launch MainActivity after 2.4s
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2400);
    }
}
