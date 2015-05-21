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
    // кол-во элементов
    @Override
    public int getCount() {
        return photos.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        // используем созданные, но не используемые view
        View view = convertView;
//        if (view == null) {
//            view = lInflater.inflate(R.layout.item, parent, false);
//        }
//
//        Product p = getProduct(position);
//
//        // заполняем View в пункте списка данными из товаров: наименование, цена
//        // и картинка
//        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
//        ((TextView) view.findViewById(R.id.tvPrice)).setText(p.price + "");
//        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
//
//        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
//        // присваиваем чекбоксу обработчик
//        cbBuy.setOnCheckedChangeListener(myCheckChangList);
//        // пишем позицию
//        cbBuy.setTag(position);
//        // заполняем данными из товаров: в корзине или нет
//        cbBuy.setChecked(p.box);
        return view;
    }
}
