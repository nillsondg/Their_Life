package com.nillsondg.theirlife;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by Dmitry on 21.05.2015.
 */
public class FetchPanoramioPhotos extends AsyncTask<LatLng, Void, ArrayList<Photo>> {
    private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();
//    private ArrayList<Photo> mPhotosList;
//        private Context mContext;
    private MapsActivity activity;
    public FetchPanoramioPhotos(MapsActivity activity) {
//            mContext = context;
//        mPhotosList = photoAdapter;
        this.activity = activity;
//        this.activity = activity;
    }
    @Override
    protected ArrayList<Photo> doInBackground(LatLng... latLngs) {
        if(latLngs[0] == null || latLngs[1] == null) return null;

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
        final String COUNT = "count";
        final String HAS_MORE = "has_more";
        final String MAP_LOCATION = "map_location";

        final String LAT = "lat";
        final String LON = "lon";
        final String PANORAMIO_ZOOM = "panoramio_zoom";

        final String PHOTOS = "photos";

        final String PHOTO_FILE_URL = "photo_file_url";
        final String LATITUDE = "latitude";
        final String LONGITUDE = "longitude";
        final String TITLE = "photo_title";
        final String AUTHOR = "owner_name";
        final String PHOTO_URL = "photo_url";
        final String AUTHOR_URL = "owner_url";
        final String UPLOAD_DATE = "upload_date";
        final String HEIGHT = "height";
        final String WIDTH = "width";
        final String OWNER_ID = "owner_id";
        final String OWNER_URL = "owner_url";
        final String PHOTO_ID = "photo_id";
        final String PLACE_ID = "place_id";

        JSONObject photosJson = new JSONObject(photosJsonStr);
        ArrayList<Photo> resultPhotos = new ArrayList<>();
        JSONArray photosArray = photosJson.getJSONArray(PHOTOS);
        for( int i = 0; i < photosArray.length(); i++ ){
            JSONObject jsonPhoto = photosArray.getJSONObject(i);
            String photo_file_url = jsonPhoto.getString(PHOTO_FILE_URL);
            double latitude = jsonPhoto.getDouble(LATITUDE);
            double longitude = jsonPhoto.getDouble(LONGITUDE);
            String title = jsonPhoto.getString(TITLE);
            String owner_name = jsonPhoto.getString(AUTHOR);
            String upload_date = jsonPhoto.getString(UPLOAD_DATE);
            String photo_url = jsonPhoto.getString(PHOTO_URL);
            Photo photo = new Photo(latitude, longitude, photo_file_url);
            photo.setTitle(title);
            photo.setAuthor(owner_name);
            photo.setDate(upload_date);
            photo.setPhotoUrl(photo_url);
            resultPhotos.add(photo);
            Log.w(LOG_TAG, resultPhotos.get(i).getLatitude() + " " + resultPhotos.get(i).getLongitude());
        }
        return resultPhotos;
    }
    @Override
    protected void onPostExecute(ArrayList<Photo> result) {
        if (result != null) {
//            mPhotosList = result;
//            for(Photo photo : result) {
//                mPhotosList.add(photo);
//            }
            activity.setPhotosArray(result);
            activity.drawGeoMarkers();
            Log.w(LOG_TAG,"success");
        }
    }
}