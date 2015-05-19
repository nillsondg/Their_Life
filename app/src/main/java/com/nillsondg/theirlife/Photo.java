package com.nillsondg.theirlife;

/**
 * Created by Dmitry on 19.05.2015.
 */
public class Photo {
    private double latitude;
    private double longitude;
    private String url;
    public Photo(double latitude, double longitude, String url){
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }
}
