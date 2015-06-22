package com.nillsondg.theirlife;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Dmitry on 19.05.2015.
 */
public class Photo implements Serializable {
    private final String LOG_TAG = Photo.class.getSimpleName();

    private double latitude;
    private double longitude;
    private String title;
    private String author;
    private String url;
    private Bitmap icon;
    private String date;
    private String photoUrl;

    public Photo(double latitude, double longitude, String url){
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
//        LoadImage load = new LoadImage();
//        load.execute(url);
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

    public void setAuthor(String author){
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor(){
        return author;
    }
    public String getUrl() {
        return url;
    }
    public String getTitle(){
        return title;
    }
    public Bitmap getIcon() {
        return icon;
    }
    private void setIcon(Bitmap icon){
        this.icon = icon;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

//    private class LoadImage extends AsyncTask<String, String, Bitmap> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            pDialog = new ProgressDialog(MainActivity.this);
////            pDialog.setMessage("Loading Image ....");
////            pDialog.show();
//
//        }
//        protected Bitmap doInBackground(String... args) {
//            try {
//                URL url = new URL(args[0].replace("medium", "square"));
//                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
//                return bitmap;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        protected void onPostExecute(Bitmap image) {
//
//            if(image != null){
//                setIcon(image);
//            }else{
//                //Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
