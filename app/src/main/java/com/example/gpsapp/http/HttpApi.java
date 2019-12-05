package com.example.gpsapp.http;

import com.example.models.ConfigurationDto;
import com.example.models.LocationDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HttpApi {

    @GET("/{id}")
    Call<ConfigurationDto> getConfiguration(@Path("id") String configurationId);

    @Headers("Content-type: application/json")
    @POST("/report")
    Call<LocationDto> saveLocation(@Body LocationDto locationDto);
}
