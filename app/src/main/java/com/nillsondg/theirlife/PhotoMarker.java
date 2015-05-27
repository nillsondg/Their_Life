package com.nillsondg.theirlife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dmitry on 21.05.2015.
 */
public class PhotoMarker {
    private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();
    //private String label;
    //private String icon;
    private Photo photo;
    public PhotoMarker(Photo photo) {
        //this.label = label;
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }
    public LatLng getCoordinates(){
        return photo.getCoordinates();
    }
}