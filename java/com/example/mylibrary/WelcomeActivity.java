package com.example.mylibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    ImageView flagImage;
    TextView langCode, welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_welcome);

        flagImage = findViewById(R.id.flagImage);
        langCode = findViewById(R.id.langCode);
        welcomeText = findViewById(R.id.welcomeText);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // cek permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        } else {
            getCountryByLocation();
        }
    }

    private void getCountryByLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            1
                    );

                    if (addresses != null && !addresses.isEmpty()) {
                        updateUI(addresses.get(0).getCountryCode());
                    } else {
                        updateUI("ID");
                    }

                } catch (IOException e) {
                    updateUI("ID");
                }
            } else {
                updateUI("ID");
            }
        });
    }

    private void updateUI(String code) {

        langCode.setText(code);

        switch (code) {
            case "ID":
                flagImage.setImageResource(R.drawable.flag_id);
                welcomeText.setText("Selamat Datang!");
                break;

            case "US":
                flagImage.setImageResource(R.drawable.flag_us);
                welcomeText.setText("Welcome!");
                break;

            case "JP":
                flagImage.setImageResource(R.drawable.flag_jp);
                welcomeText.setText("ようこそ！");
                break;

            case "KR":
                flagImage.setImageResource(R.drawable.flag_kr);
                welcomeText.setText("환영합니다!");
                break;

            case "CN":
                flagImage.setImageResource(R.drawable.flag_cn);
                welcomeText.setText("欢迎！");
                break;

            default:
                flagImage.setImageResource(R.drawable.flag_id);
                welcomeText.setText("Selamat Datang!");
                break;
        }

        // delay modern (tidak deprecated)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCountryByLocation();
        } else {
            updateUI("ID");
        }
    }
}