package com.example.gpsapp.model;

import java.io.Serializable;

public class Configuration implements Serializable {
    private String name;
    private String token;
    private String trackedObjectId;

    //TODO CHANGE TO MINUTES
    private int positionIntervalInMilliseconds;

    public Configuration(String name, String token, String trackedObjectId, int positionIntervalInMilliseconds) {
        this.name = name;
        this.token = token;
        this.trackedObjectId = trackedObjectId;
        this.positionIntervalInMilliseconds = positionIntervalInMilliseconds;
    }

    public Configuration() {
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

    public String getTrackedObjectId() {
        return trackedObjectId;
    }

    public void setTrackedObjectId(String trackedObjectId) {
        this.trackedObjectId = trackedObjectId;
    }

    public int getPositionIntervalInMilliseconds() {
        return positionIntervalInMilliseconds;
    }

    public void setPositionIntervalInMilliseconds(int positionIntervalInMilliseconds) {
        this.positionIntervalInMilliseconds = positionIntervalInMilliseconds;
    }
}
