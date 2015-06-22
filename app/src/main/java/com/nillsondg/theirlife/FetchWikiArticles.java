package com.nillsondg.theirlife;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Dmitry on 22.06.2015.
 */
public class FetchWikiArticles extends AsyncTask<LocationMarker, Void, ArrayList<WikiArticle>> {
    private final String LOG_TAG = FetchWikiArticles.class.getSimpleName();
    private MapsActivity activity;

    public FetchWikiArticles(MapsActivity activity) {
        this.activity = activity;
    }
    @Override
    protected ArrayList<WikiArticle> doInBackground(LocationMarker... marker) {
        if(marker == null || marker[0].getCoordinates() == null) return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String wikiArticlesJsonStr = null;
        // https://ru.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=500&gscoord=55.743050|37.574676&gslimit=100&format=json
        String language = "ru";
        String action = "query";
        String list = "geosearch";
        double gsradius = marker[0].getRadius();
        String gscoord = marker[0].getCoordinates().latitude + "|" + marker[0].getCoordinates().longitude;
        int gslimit = 20;
        String format = "json";
        try{
            final String WIKI_BASE_URL = "https://" + language + ".wikipedia.org/w/api.php?";
            final String ACTION_PARAM = "action";
            final String LIST_PARAM = "list";
            final String GSRADIUS_PARAM = "gsradius";
            final String GSCOORD_PARAM = "gscoord";
            final String GSLIMIT_PARAM = "gslimit";
            final String FORMAT_PARAM = "format";
            Uri builtUri = Uri.parse(WIKI_BASE_URL).buildUpon()
                    .appendQueryParameter(ACTION_PARAM, action)
                    .appendQueryParameter(LIST_PARAM, list)
                    .appendQueryParameter(GSRADIUS_PARAM, Double.toString(gsradius))
                    .appendQueryParameter(GSCOORD_PARAM, gscoord)
                    .appendQueryParameter(GSLIMIT_PARAM, Integer.toString(gslimit))
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.w(LOG_TAG, builtUri.toString());
            urlConnection = (HttpsURLConnection) url.openConnection();
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
            wikiArticlesJsonStr = buffer.toString();
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
            return getWikiArticlesDataFromJson(wikiArticlesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private ArrayList<WikiArticle> getWikiArticlesDataFromJson(String wikiArticlesJsonStr) throws JSONException{

        // These are the names of the JSON objects that need to be extracted.
        final String QUERY = "query";
        final String GEOSEARCH = "geosearch";

        final String PAGE_ID = "pageid";
        final String TITLE = "title";
        final String LAT = "lat";
        final String LON = "lon";

        ArrayList<WikiArticle> resultWikiArticles = new ArrayList<>();

        JSONObject wikiArticlesJson = new JSONObject(wikiArticlesJsonStr);
        JSONObject queryJson = wikiArticlesJson.getJSONObject(QUERY);
        JSONArray wikiArticlesArray = queryJson.getJSONArray(GEOSEARCH);
        for(int i = 0; i < wikiArticlesArray.length(); i++){
            JSONObject wikiArticleJson = wikiArticlesArray.getJSONObject(i);
            //String page_id = wikiArticleJson.getString(PAGE_ID);
            double latitude = wikiArticleJson.getDouble(LAT);
            double longitude = wikiArticleJson.getDouble(LON);
            String title = wikiArticleJson.getString(TITLE);
            WikiArticle wikiArticle = new WikiArticle(latitude, longitude);
            wikiArticle.setTitle(title);
            resultWikiArticles.add(wikiArticle);
            Log.w(LOG_TAG, resultWikiArticles.get(i).getLatitude() + " " + resultWikiArticles.get(i).getLongitude());
        }
        return resultWikiArticles;
    }
    @Override
    protected void onPostExecute(ArrayList<WikiArticle> result) {
        if (result != null) {
            activity.setWikiArticlesArray(result);
            activity.drawGeoMarkers();
            Log.w(LOG_TAG,"success");
        }
    }
}
