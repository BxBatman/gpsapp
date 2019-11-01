package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    TextInputEditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        textInputEditText = findViewById(R.id.configurationNumberInput);
    }


    public void goToTrackGps(View view) {
        Intent intent =  new Intent(this, TrackGpsActivity.class);
        startActivity(intent);
    }
}
