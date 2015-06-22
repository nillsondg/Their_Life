package com.nillsondg.theirlife;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Dmitry on 22.06.2015.
 */
public class LocationMarker {
    private LatLng mLatLng;
    private GoogleMap mMap;
    Marker mMarker;
    private Circle mCircle;
    private double cRadius = 1000.0;
    private int cFillColor = 0x3242A3E1;
    private int cStrokeColor = 0x32008ac4;
    private int cStrokeWidth = 8;
    private float mZoom = 13.5f;

    private LatLng mLeftDownLatLng;
    private LatLng mRightUpLatLng;
    private LocationManager mLocationManager;

    public LocationMarker(GoogleMap mMap, LocationManager mLocationManager){
        this.mMap = mMap;
        this.mLocationManager = mLocationManager;
        getMyLocation();
    }
    boolean drawMarker(){
        if(mLatLng == null)
            return false;
        mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng));
        mCircle = mMap.addCircle(new CircleOptions().center(mLatLng).radius(cRadius)
                .fillColor(cFillColor).strokeColor(cStrokeColor).strokeWidth(cStrokeWidth));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, mZoom);
        mMap.animateCamera(cameraUpdate);
        mCircle = mMap.addCircle(new CircleOptions().center(mLatLng).radius(cRadius)
                .fillColor(cFillColor).strokeColor(cStrokeColor).strokeWidth(cStrokeWidth));
        return true;
    }

    void getMyLocation(){
        //Criteria criteria = new Criteria();
        List<String> providers = mLocationManager.getAllProviders();
        Location bestLocation = null;
        for(String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if(bestLocation != null) {
            Log.w("loc", "ok");
            // Get latitude of the current location
            double latitude = bestLocation.getLatitude();
            // Get longitude of the current location
            double longitude = bestLocation.getLongitude();
            mLatLng = new LatLng(latitude, longitude);
        }
        GetOffset();
    }

    void GetOffset(){
        if(mLatLng == null) return;
        int angle = 45;
        double offset = cRadius * Math.cos(Math.PI/180.0*angle);
        double[] left = CalculateCoordinates(-offset);
        double[] right = CalculateCoordinates(offset);
        mRightUpLatLng = new LatLng(right[0], right[1]);
        mLeftDownLatLng = new LatLng(left[0], left[1]);
        Log.w("center", mLatLng.latitude + " " + mLatLng.longitude);
        Log.w("offset left", mLeftDownLatLng.latitude + " " + mLeftDownLatLng.longitude);
        Log.w("offset right", mRightUpLatLng.latitude + " " + mRightUpLatLng.longitude);
    }

    double[] CalculateCoordinates(double offset){
        double lat0 = mLatLng.latitude;
        double lon0 = mLatLng.longitude;
        double lat = lat0 + (180/Math.PI)*(offset/6378137);
        double lon = lon0 + (180/Math.PI)*(offset/6378137)/Math.cos(Math.PI/180.0*lat0);
        double[] result = new double[2];
        result[0] = lat;
        result[1] = lon;
        return result;
    }
    public LatLng getCoordinates(){
        return mLatLng;
    }
    public void setCoordinates(LatLng mLatLng){
        this.mLatLng = mLatLng;
        GetOffset();
    }
    public LatLng[] getSquareCoordinates(){
        LatLng[] result = new LatLng[2];
        result[0] = mLeftDownLatLng;
        result[1] = mRightUpLatLng;
        return result;
    }
    public boolean RadiusUp(){
        if(cRadius == 2000)
            return false;
        cRadius += 100;
        mZoom -= 0.25;
        GetOffset();
        return true;
    }
    public boolean RadiusDown(){
        if(cRadius == 0)
            return false;
        cRadius -= 100;
        mZoom += 0.25;
        GetOffset();
        return true;
    }

    public double getRadius() {
        return cRadius;
    }
}
