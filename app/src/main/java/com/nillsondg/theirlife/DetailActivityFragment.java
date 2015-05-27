package com.nillsondg.theirlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();

    private Photo mPhoto;
    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        img.setOnClickListener( new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView:
                        Intent fullscreenIntent = new Intent(getActivity(), ImageFullScreenActivity.class)
                                .putExtra("Photo", mPhoto);
                        startActivity(fullscreenIntent);
                    default:
                }
            }});
        LoadImage load = new LoadImage(img);
        load.execute(mPhoto.getUrl());
        TextView text = (TextView)rootView.findViewById(R.id.textView);
        text.append(mPhoto.getTitle() + " by " + mPhoto.getAuthor());
        return rootView;
    }
}
