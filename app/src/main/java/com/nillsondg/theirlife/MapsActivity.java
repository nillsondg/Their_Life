package com.nillsondg.theirlife;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LatLng mLatLng;
    UiSettings mMapSettings;
    Marker mMarker;
    Circle mCircle;
    double cRadius = 1000.0;
    int cFillColor = 0x3242A3E1;
    int cStrokeColor = 0x32008ac4;
    int cStrokeWidth = 8;
    float mZoom = 13.5f;
    LocationManager mLocationManager;
    //List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if(savedInstanceState != null){
            double lat = savedInstanceState.getDouble("lat");
            double lon = savedInstanceState.getDouble("lon");
            mLatLng = new LatLng(lat, lon);
            drawMarker();
        }
    }

    protected void drawMarker(){
        if(mLatLng == null) return;
        mMap.clear();
        //Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng));
        mCircle = mMap.addCircle(new CircleOptions().center(mLatLng).radius(cRadius)
                .fillColor(cFillColor).strokeColor(cStrokeColor).strokeWidth(cStrokeWidth));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, mZoom);
        mMap.animateCamera(cameraUpdate);
        //markers.add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(){
        mMap.setMyLocationEnabled(true);
        mMapSettings = mMap.getUiSettings();
        mMapSettings.setCompassEnabled(true);
        mMapSettings.setMyLocationButtonEnabled(true);
        mMapSettings.setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Toast.makeText(MapsActivity.this, "got clicked " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show(); //do some stuff
                        mLatLng = latLng;
                        drawMarker();
                    }
                }
        );
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(
                        MapsActivity.this,
                        "Определяем текущую позицию",
                        Toast.LENGTH_SHORT).show();
                getMyLocation();
//                Toast.makeText(
//                        MapsActivity.this,
//                        "coor" + mLatLng.latitude + " " + mLatLng.longitude,
//                        Toast.LENGTH_SHORT).show();
                drawMarker();
                return true;
            }
        });


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getMyLocation();
        drawMarker();
    }

    public void onClickTest(View view) {
        if(mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Toast.makeText(MapsActivity.this, "zoom" + mMap.getCameraPosition().zoom, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if( mLatLng != null){
            outState.putDouble("lat", mLatLng.latitude);
            outState.putDouble("lon", mLatLng.longitude);
        }
    }

    public void onClickRadiusUp(View view){
        if(cRadius != 1000) {
            cRadius += 100;
            mZoom -= 0.25;
        }
        drawMarker();
    }

    public void onClickRadiusDown(View view){
        if(cRadius != 0){
            cRadius -= 100;
            mZoom += 0.25;
        }
        drawMarker();
    }

    void getMyLocation(){
        Criteria criteria = new Criteria();
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
    }
}
