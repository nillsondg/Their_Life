package com.nillsondg.theirlife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Dmitry on 21.05.2015.
 */
public class PhotoAdapter extends BaseAdapter {
    private final String LOG_TAG = FetchPanoramioPhotos.class.getSimpleName();
    Context context;
    LayoutInflater inflater;
    ArrayList<Photo> photos;

    PhotoAdapter(Context context, ArrayList<Photo> photos){
        this.context = context;
        this.photos = photos;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
//        if (view == null) {
//            view = lInflater.inflate(R.layout.item, parent, false);
//        }
//
//        Product p = getProduct(position);
//
//        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
//        ((TextView) view.findViewById(R.id.tvPrice)).setText(p.price + "");
//        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
//
//        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
//        cbBuy.setOnCheckedChangeListener(myCheckChangList);
//        cbBuy.setTag(position);
//        cbBuy.setChecked(p.box);
        return view;
    }
}
