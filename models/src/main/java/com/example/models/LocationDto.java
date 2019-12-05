package com.example.models;

import com.google.gson.annotations.SerializedName;

public class LocationDto {
    private String acc;
    private String alt;
    private Integer bea;
    private String lat;
    @SerializedName("long")
    private String lng;
    private String prov;
    private Integer spd;
    private Integer sat;
    private String time;
    private String serial;
    private String tid;
    private String plat;
    private String platVer;
    private Integer bat;


    public LocationDto(String acc, String alt, Integer bea, String lat, String lng, String prov, Integer spd, Integer sat, String time, String serial, String tid, String plat, String platVer, Integer bat) {
        this.acc = acc;
        this.alt = alt;
        this.bea = bea;
        this.lat = lat;
        this.lng = lng;
        this.prov = prov;
        this.spd = spd;
        this.sat = sat;
        this.time = time;
        this.serial = serial;
        this.tid = tid;
        this.plat = plat;
        this.platVer = platVer;
        this.bat = bat;
    }

    public LocationDto() {
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getBea() {
        return bea;
    }

    public void setBea(Integer bea) {
        this.bea = bea;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public Integer getSpd() {
        return spd;
    }

    public void setSpd(Integer spd) {
        this.spd = spd;
    }

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getPlatVer() {
        return platVer;
    }

    public void setPlatVer(String platVer) {
        this.platVer = platVer;
    }

    public Integer getBat() {
        return bat;
    }

    public void setBat(Integer bat) {
        this.bat = bat;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
