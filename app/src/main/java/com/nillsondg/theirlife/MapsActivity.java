package com.nillsondg.theirlife;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

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
    //List<Marker> markers = new ArrayList<Marker>();

    private LatLng mLeftDownLatLng;
    private LatLng mRightUpLatLng;

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
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Toast.makeText(MapsActivity.this, "got clicked " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show(); //do some stuff
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
        FetchPanoramioPhotos photosTask = new FetchPanoramioPhotos();
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

    public void onClickTest(View view) {
        if(mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Toast.makeText(MapsActivity.this, "zoom" + mMap.getCameraPosition().zoom, Toast.LENGTH_SHORT).show();
    }

    public void onClickRadiusUp(View view){
        if(cRadius != 1000) {
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

    public class FetchPanoramioPhotos extends AsyncTask<LatLng, Void, ArrayList<Photo>> {
        private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();
//        private ArrayAdapter<Photo> mPhotosAdapter;
//        private Context mContext;
        public FetchPanoramioPhotos() {
        //public FetchPanoramioPhotos(Context context, ArrayAdapter<Photo> photoAdapter) {
//            mContext = context;
//            mPhotosAdapter = photoAdapter;
        }
        @Override
        protected ArrayList<Photo> doInBackground(LatLng... latLngs) {
            if(latLngs == null) return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String photosJsonStr = null;
            // http://www.panoramio.com/map/get_panoramas.php?set=public&from=0&to=20&minx=37.78154268761862&miny=55.710612752003875&maxx=37.804096416628475&maxy=55.723316848584766&size=medium&mapfilter=true
            String size = "medium";
            String set = "public";
            int from = 0;
            int to = 10;
            double minx = latLngs[0].longitude;
            double miny = latLngs[0].latitude;
            double maxx = latLngs[1].longitude;
            double maxy = latLngs[1].latitude;
            String mapfilter = "true";
            try{
                final String PANORAMIO_BASE_URL = "http://www.panoramio.com/map/get_panoramas.php?";
                final String SIZE_PARAM = "size";
                final String SET_PARAM = "set";
                final String FROM_PARAM = "from";
                final String TO_PARAM = "to";
                final String MAPFILTER_PARAM = "mapfilter";
                final String MINX_PARAM = "minx";
                final String MINY_PARAM = "miny";
                final String MAXX_PARAM = "maxx";
                final String MAXY_PARAM = "maxy";
                Uri builtUri = Uri.parse(PANORAMIO_BASE_URL).buildUpon()
                        .appendQueryParameter(SET_PARAM, set)
                        .appendQueryParameter(FROM_PARAM, Integer.toString(from))
                        .appendQueryParameter(TO_PARAM, Integer.toString(to))
                        .appendQueryParameter(MINX_PARAM, Double.toString(minx))
                        .appendQueryParameter(MINY_PARAM, Double.toString(miny))
                        .appendQueryParameter(MAXX_PARAM, Double.toString(maxx))
                        .appendQueryParameter(MAXY_PARAM, Double.toString(maxy))
                        .appendQueryParameter(SIZE_PARAM, size)
                        .appendQueryParameter(MAPFILTER_PARAM, mapfilter)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.w(LOG_TAG, builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                photosJsonStr = buffer.toString();

            }catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getPhotosDataFromJson(photosJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private ArrayList<Photo> getPhotosDataFromJson(String photosJsonStr) throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String PHOTOS = "photos";
            final String PHOTO_FILE_URL = "photo_file_url";
            final String LATITUDE = "latitude";
            final String LONGITUDE = "longitude";

            JSONObject photosJson = new JSONObject(photosJsonStr);
            ArrayList<Photo> resultPhotos = new ArrayList<>();
            JSONArray photosArray = photosJson.getJSONArray(PHOTOS);
            for( int i = 0; i < photosArray.length(); i++ ){
                JSONObject photo = photosArray.getJSONObject(i);
                String photo_file_url = photo.getString(PHOTO_FILE_URL);
                double latitude = photo.getDouble(LATITUDE);
                double longitude = photo.getDouble(LONGITUDE);
                resultPhotos.add(new Photo(latitude, longitude, photo_file_url));
                Log.w(LOG_TAG, resultPhotos.get(i).getLatitude() + " " + resultPhotos.get(i).getLongitude());
            }
            return resultPhotos;
        }
        @Override
        protected void onPostExecute(ArrayList<Photo> result) {
//            if (result != null && mPhotosAdapter != null) {
//                mPhotosAdapter.clear();
//                for(ArrayList<Photo> photo : result) {
//                    mPhotosAdapter.add(photo);
//                }
                // New data is back from the server.  Hooray!
            Log.w(LOG_TAG,"success");
            }
        }
}
