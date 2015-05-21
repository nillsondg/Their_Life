package com.nillsondg.theirlife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();

    private Photo mPhoto;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if( intent != null && intent.hasExtra("Photo"))
            mPhoto = (Photo) intent.getSerializableExtra("Photo");
        ImageView img = (ImageView)rootView.findViewById(R.id.imageView);
        //img.setScaleType(ImageView.ScaleType.FIT_XY);
        LoadImage load = new LoadImage(img);
        load.execute(mPhoto.getUrl());
        TextView text = (TextView)rootView.findViewById(R.id.textView);
        text.append(mPhoto.getTitle() + " by " + mPhoto.getAuthor());
        return rootView;
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        ImageView imageView;
        public LoadImage(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage("Loading Image ....");
//            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                Log.w(LOG_TAG, url.toString());
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                imageView.setImageBitmap(image);
            }else{
                //Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
