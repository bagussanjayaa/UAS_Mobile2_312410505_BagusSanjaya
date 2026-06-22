package com.example.mylibrary;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logoImage);
        appName = findViewById(R.id.appNameText);

        startLogoAnimation();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2700);
    }

    private void startLogoAnimation() {

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.5f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.5f, 1f);
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);

        logoScaleX.setDuration(1200);
        logoScaleY.setDuration(1200);
        logoFade.setDuration(1200);

        ObjectAnimator textFade =
                ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);

        textFade.setStartDelay(800);
        textFade.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(logoScaleX, logoScaleY, logoFade, textFade);
        set.start();
    }
}