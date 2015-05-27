package com.nillsondg.theirlife;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {
    private final String LOG_TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng mLatLng;
    UiSettings mMapSettings;
    Marker mMarker;
    Circle mCircle;
    private double cRadius = 1000.0;
    int cFillColor = 0x3242A3E1;
    int cStrokeColor = 0x32008ac4;
    int cStrokeWidth = 8;
    private float mZoom = 13.5f;
    private LocationManager mLocationManager;
    List<PhotoMarker> markers = new ArrayList<>();

    private LatLng mLeftDownLatLng;
    private LatLng mRightUpLatLng;

    private ArrayList<Photo> mPhotosList;
    private HashMap<Marker, PhotoMarker> mMarkersHashMap;

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

        mMarkersHashMap = new HashMap<>();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(final Marker marker) {
                                              PhotoMarker photomarker = mMarkersHashMap.get(marker);
                                              if (photomarker == null) return false;
                                              Intent detailIntent = new Intent(MapsActivity.this, ImageFullScreenActivity.class)
                                                      .putExtra("Photo", photomarker.getPhoto());
                                              startActivity(detailIntent);
                                              //int arrayListPosition = getArrayListPosition(pos);
                                              return true;
                                          }

                                      }
        );
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        //Toast.makeText(MapsActivity.this, "got clicked " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show(); //do some stuff
                        mLatLng = latLng;
                        GetOffset();
                        drawMarker();
                    }
                }
        );
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(
                        MapsActivity.this,
                        "Определяем текущую геопозицию",
                        Toast.LENGTH_SHORT).show();
                getMyLocation();
//                Toast.makeText(
//                        MapsActivity.this,
//                        "coor" + mLatLng.latitude + " " + mLatLng.longitude,
//                        Toast.LENGTH_SHORT).show();
                GetOffset();
                drawMarker();
                return true;
            }
        });


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getMyLocation();
        GetOffset();
        drawMarker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected void drawMarker(){
        if(mLatLng == null) return;
        mMap.clear();
        //Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng));
        //mMap.addMarker(new MarkerOptions().position(mRightUpLatLng));
        //mMap.addMarker(new MarkerOptions().position(mLeftDownLatLng));
        mCircle = mMap.addCircle(new CircleOptions().center(mLatLng).radius(cRadius)
                .fillColor(cFillColor).strokeColor(cStrokeColor).strokeWidth(cStrokeWidth));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, mZoom);
        mMap.animateCamera(cameraUpdate);
        //markers.add(marker);
        FetchPanoramioPhotos photosTask = new FetchPanoramioPhotos(this);
        photosTask.execute(mLeftDownLatLng, mRightUpLatLng);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if( mLatLng != null){
            outState.putDouble("lat", mLatLng.latitude);
            outState.putDouble("lon", mLatLng.longitude);
        }
    }

//    public void onClickTest(View view) {
//        if(mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        else
//            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        Toast.makeText(MapsActivity.this, "zoom" + mMap.getCameraPosition().zoom, Toast.LENGTH_SHORT).show();
//    }

    public void onClickRadiusUp(View view){
        if(cRadius != 2000) {
            cRadius += 100;
            mZoom -= 0.25;
            GetOffset();
        }
        drawMarker();
    }

    public void onClickRadiusDown(View view){
        if(cRadius != 0){
            cRadius -= 100;
            mZoom += 0.25;
            GetOffset();
        }
        drawMarker();
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

    public void setPhotosArray(ArrayList<Photo> photos){
        mPhotosList = photos;
    }
    public void drawGeoMarkers(){
        if( mPhotosList == null) return;
        for( Photo photo : mPhotosList){
            PhotoMarker photoMarker = new PhotoMarker(photo);
            markers.add(photoMarker);
            LatLng coordinates = photoMarker.getCoordinates();
            //Bitmap icon = photo.getIcon();
            //if(icon != null)
                //mMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.fromBitmap(icon)));
            //else{
            Marker marker = mMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.fromResource(R.mipmap.panoramio_icon)));
            mMarkersHashMap.put(marker, photoMarker);
            //}
        }
    }
}
