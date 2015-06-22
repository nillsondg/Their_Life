package com.nillsondg.theirlife;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity {
    private final String LOG_TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    UiSettings mMapSettings;
    LocationMarker mMarker;


    private ArrayList<Photo> mPhotosList;
    private ArrayList<WikiArticle> mWikiArticlesList;
    //TODO выпилить
    private HashMap<Marker, Photo> mMarkersHashMap;
    private HashMap<Marker, WikiArticle> mMarkersHashMap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if(savedInstanceState != null){
            double lat = savedInstanceState.getDouble("lat");
            double lon = savedInstanceState.getDouble("lon");
            mMarker.setCoordinates(new LatLng(lat, lon));
        }
        else
            mMarker.getMyLocation();
        drawMarker();
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
        mMarkersHashMap2 = new HashMap<>();
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMarker = new LocationMarker(mMap, mLocationManager);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(final Marker marker) {
                                              Photo photo = mMarkersHashMap.get(marker);
                                              if (photo == null)
                                                  return false;
                                              Intent detailIntent;
                                              detailIntent = new Intent(MapsActivity.this, ImageFullScreenActivity.class)
                                                      .putExtra("Photo", photo);
                                              startActivity(detailIntent);
                                              return true;
                                          }
                                      }
        );
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        //Toast.makeText(MapsActivity.this, "got clicked " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show(); //do some stuff
                        mMarker.setCoordinates(latLng);
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
                mMarker.getMyLocation();
                drawMarker();
                return true;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                WikiArticle wikiArticle = mMarkersHashMap2.get(marker);
                Intent detailIntent;
                if (wikiArticle != null){
                    detailIntent = new Intent(Intent.ACTION_VIEW)
                            .setData(wikiArticle.getArticleUrl());
                    Log.w(LOG_TAG, wikiArticle.getArticleUrl().toString());
                    startActivity(detailIntent);
                }
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected void drawMarker(){
        if(mMarker == null) return;
        mMap.clear();
        mMarker.drawMarker();
        mWikiArticlesList = null;
        FetchWikiArticles wikiArticlesTask = new FetchWikiArticles(this);
        wikiArticlesTask.execute(mMarker);
        mPhotosList = null;
        FetchPanoramioPhotos photosTask = new FetchPanoramioPhotos(this);
        photosTask.execute(mMarker.getSquareCoordinates());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(mMarker != null){
            outState.putDouble("lat", mMarker.getCoordinates().latitude);
            outState.putDouble("lon", mMarker.getCoordinates().longitude);
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
        boolean f = mMarker.RadiusUp();
        if(f) drawMarker();
    }

    public void onClickRadiusDown(View view){
        boolean f = mMarker.RadiusDown();
        if(f) drawMarker();
    }

    public void setPhotosArray(ArrayList<Photo> photos) {
        mPhotosList = photos;
    }

    public void setWikiArticlesArray(ArrayList<WikiArticle> wikiArticles){
        mWikiArticlesList = wikiArticles;
    }
    public void drawGeoMarkers(){
        if(mPhotosList != null)
            for( Photo photo : mPhotosList){
                LatLng coordinates = photo.getCoordinates();
                Marker marker = mMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.fromResource(R.mipmap.panoramio_icon)));
                mMarkersHashMap.put(marker, photo);
            }
        if(mWikiArticlesList == null) return;
        for(WikiArticle wikiArticle : mWikiArticlesList){
            LatLng coordinates = wikiArticle.getCoordinates();
            Marker marker = mMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.fromResource(R.mipmap.wikipedia_icon)).title(wikiArticle.getTitle()));
            mMarkersHashMap2.put(marker, wikiArticle);
        }
    }
}
