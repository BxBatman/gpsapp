package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gpsapp.model.Configuration;
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

        //MOCKED ! NEED TO GET FROM SERVER BASED ON configuraiton number enetered by user
        Configuration configuration = new Configuration();
        configuration.setName(textInputEditText.getText().toString());
        configuration.setToken("TOKEENENE");
        configuration.setPositionIntervalInMilliseconds(1000);
        configuration.setTrackedObjectId("objectname");
        intent.putExtra("configuration", configuration);


        startActivity(intent);
    }
}
