package com.example.gpsapp.model;

public class ActivityRecognized {
    private String activityName;
    private String type;

    public ActivityRecognized() {
    }

    public ActivityRecognized(String activityName, String type) {
        this.activityName = activityName;
        this.type = type;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
