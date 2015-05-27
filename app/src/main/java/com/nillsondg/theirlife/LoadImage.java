package com.nillsondg.theirlife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Dmitry on 27.05.2015.
 */
public class LoadImage extends AsyncTask<String, String, Bitmap> {
    private final String LOG_TAG = LoadImage.class.getSimpleName();
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
            //final BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 8;
            //Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent(), null, options);
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
            //image.recycle();
        }else{
            //Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}