package com.example.gpsapp.http;

import com.example.gpsapp.model.ConfigurationDto;
import com.example.gpsapp.model.LocationDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HttpApi {

    @GET("/{id}")
    Call<ConfigurationDto> getConfiguration(@Path("id") String configurationId);

    @POST("/report")
    Call<LocationDto> saveLocation(@Body LocationDto locationDto);
}
