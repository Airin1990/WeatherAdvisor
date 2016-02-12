package com.weijie.weatheradvisor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weiji_000 on 2016/2/8.
 */
public class Weather implements Parcelable {
    String local;
    String country;
    String main_des;
    String sub_des;
    String icon;
    double lat;
    double lng;
    double temp;
    double temp_min;
    double temp_max;
    double pressure;
    double humidity;
    double wind;

    public Weather(String local, String country, String main_des, String sub_des, String icon, double lat, double lng, double temp, double temp_min, double temp_max, double pressure, double humidity, double wind) {
        this.local = local;
        this.country = country;
        this.main_des = main_des;
        this.sub_des = sub_des;
        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
    }

    public String getLocal() {
        return local;
    }

    public String getCountry() {
        return country;
    }

    public String getMain_des() {
        return main_des;
    }

    public String getSub_des() {
        return sub_des;
    }

    public String getIcon() {
        return icon;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getTemp() {
        return temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWind() {
        return wind;
    }

    protected Weather(Parcel in) {
        local = in.readString();
        country = in.readString();
        main_des = in.readString();
        sub_des = in.readString();
        icon = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        temp = in.readDouble();
        temp_min = in.readDouble();
        temp_max = in.readDouble();
        pressure = in.readDouble();
        humidity = in.readDouble();
        wind = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(local);
        dest.writeString(country);
        dest.writeString(main_des);
        dest.writeString(sub_des);
        dest.writeString(icon);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(temp);
        dest.writeDouble(temp_min);
        dest.writeDouble(temp_max);
        dest.writeDouble(pressure);
        dest.writeDouble(humidity);
        dest.writeDouble(wind);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
}