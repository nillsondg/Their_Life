package com.nillsondg.theirlife;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Dmitry on 22.06.2015.
 */
public class WikiArticle {
    private final String LOG_TAG = WikiArticle.class.getSimpleName();

    private double latitude;
    private double longitude;
    private String title;
    private String url;
    private Bitmap icon;
    private String articleUrl;

    public WikiArticle(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public LatLng getCoordinates(){
        return new LatLng(latitude, longitude);
    }
//    public String getUrl() {
//        return url;
//    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
//    private void setIcon(Bitmap icon){
//        this.icon = icon;
//    }
//    public Bitmap getIcon() {
//        return icon;
//    }
//    public void setArticleUrl(URI articleUrl) {
//        this.articleUrl = articleUrl;
//    }
    public Uri getArticleUrl() {
        if(articleUrl == null)
            buildArticleUrl();
        return Uri.parse(articleUrl);
    }
    public void buildArticleUrl(){
        final String PROTOCOL = "https";
        final String LANGUAGE = "ru";
        final String SYTE = "wikipedia.org/wiki/";
        articleUrl = PROTOCOL + "://" + LANGUAGE + "." + SYTE + "/" + title;
    }
}
