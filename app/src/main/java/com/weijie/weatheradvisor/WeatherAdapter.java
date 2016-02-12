package com.weijie.weatheradvisor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by weiji_000 on 2016/2/10.
 */
public class WeatherAdapter extends ArrayAdapter {

    Context context;
    String[] mainArray;
    String[] subArray;
    String[] iconArray;
    int[] tempArray;
    int[] tempmArray;

    public WeatherAdapter(Context context, String[] main_des, String[] sub_des, String[] icons, int[] temp, int[] temp_min) {
        super(context, R.layout.forecast_single_row, R.id.single_textview,main_des);
        this.context = context;
        this.mainArray = main_des;
        this.subArray = sub_des;
        this.iconArray = icons;
        this.tempArray = temp;
        this.tempmArray = temp_min;
    }


    class MyViewHolder {
        TextView myMain;
        TextView mySub;
        ImageView myIcon;
        TextView myTemp;
        TextView myTempm;

        MyViewHolder(View view) {
            myMain = (TextView) view.findViewById(R.id.single_textview);
            mySub = (TextView) view.findViewById(R.id.single_textview2);
            myIcon = (ImageView) view.findViewById(R.id.weather_imageview);
            myTemp = (TextView) view.findViewById(R.id.temp_textview);
            myTempm = (TextView) view.findViewById(R.id.temp_textview2);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MyViewHolder holder = null;
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.forecast_single_row, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (MyViewHolder) row.getTag();
        }

        holder.myMain.setText(mainArray[position]);
        holder.mySub.setText(subArray[position]);
        imageLoader.displayImage(iconArray[position], holder.myIcon);
        holder.myTemp.setText(String.valueOf(tempArray[position]));
        holder.myTempm.setText(String.valueOf(tempmArray[position]));

        return row;
    }
}
