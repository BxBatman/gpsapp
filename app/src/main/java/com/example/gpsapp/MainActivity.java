package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gpsapp.http.HttpApi;
import com.example.gpsapp.http.RetrofitClientInstance;
import com.example.models.ConfigurationDto;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final Intent intent = new Intent(this, TrackGpsActivity.class);
        HttpApi api = RetrofitClientInstance.getRetrofitInstance().create(HttpApi.class);

        if (textInputEditText.getText() == null || textInputEditText.getText().equals("")) {
            Toast.makeText(MainActivity.this, "Textinput cannot be null", Toast.LENGTH_SHORT).show();
        } else {
            Call<ConfigurationDto> call = api.getConfiguration(textInputEditText.getText().toString());
            call.enqueue(new Callback<ConfigurationDto>() {
                @Override
                public void onResponse(Call<ConfigurationDto> call, Response<ConfigurationDto> response) {
                    intent.putExtra("configuration", response.body());
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<ConfigurationDto> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
