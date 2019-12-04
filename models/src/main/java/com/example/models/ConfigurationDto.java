package com.example.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigurationDto implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("token")
    private String token;
    @SerializedName("timeInterval")
    private int timeIntervalInMinutes;

    public ConfigurationDto(String name, String token, int timeIntervalInMinutes) {
        this.name = name;
        this.token = token;
        this.timeIntervalInMinutes = timeIntervalInMinutes;
    }

    public ConfigurationDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTimeIntervalInMinutes() {
        return timeIntervalInMinutes;
    }

    public void setTimeIntervalInMinutes(int timeIntervalInMinutes) {
        this.timeIntervalInMinutes = timeIntervalInMinutes;
    }
}
