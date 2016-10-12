package com.example.raviteja.weatherapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RAVITEJA on 10/9/2016.
 */
public class FavoriteAdapter extends ArrayAdapter<Weather> {
    //List<Weather> weatherList;
    Context mContext;
    int mResource;
    ArrayList<Weather> favs;
    ImageView imageView;
    int flag=0;

    public FavoriteAdapter(Context context, int resource, ArrayList<Weather> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mResource=resource;
        this.favs=objects;
        Log.d("inside","inside adap");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        Weather weather = favs.get(position);
        TextView text2 = (TextView) convertView.findViewById(R.id.textView2);
        TextView text3 = (TextView) convertView.findViewById(R.id.textView3);
        TextView text4 = (TextView) convertView.findViewById(R.id.textView4);
        text2.setText(weather.getCityName()+","+weather.getStateName());
        text3.setText(weather.getTemperature()+"°F");
        String[] date=weather.getTime().split("-");
        Log.d("ch",date[0]);
        text4.setText("Updated on:"+date[0].trim());
        return convertView;
    }
}

