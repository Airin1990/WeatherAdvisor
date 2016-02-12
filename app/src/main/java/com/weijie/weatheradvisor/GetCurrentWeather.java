package com.weijie.weatheradvisor;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by weiji_000 on 2016/2/8.
 */
public class GetCurrentWeather extends AsyncTask<Double,Void,ArrayList<Weather>> {

    MainActivity activity;
    private static final String API_KEY = "28f6a7d5b7b856a346263d2764dbf6c0";

    public GetCurrentWeather(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Weather> doInBackground(Double... params) {
        String WeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat="+ params[0]+ "&lon="+params[1]+"&appid="+API_KEY;
        String ForecastURL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat="+params[0]+"&lon="+params[1]+"&cnt=10&mode=json&appid="+API_KEY;

        Weather weather;
        ArrayList<Weather> forecast;

        weather = getCurrentWeather(WeatherURL);
        forecast = getForecastWeather(ForecastURL);

        forecast.add(0,weather);
        return  forecast;
    }

    private ArrayList<Weather> getForecastWeather(String forecastURL) {
        ArrayList<Weather> forecast = new ArrayList<>();
        Weather weather;

        try {
            URL url = new URL(forecastURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"));
            String json = reader.readLine();
            JSONObject jsonObject = new JSONObject(json);

            //General Info
            JSONObject cityObj = jsonObject.getJSONObject("city");
            JSONObject coordObj = cityObj.getJSONObject("coord");
            double lat = coordObj.getDouble("lat");
            double lng = coordObj.getDouble("lon");
            String name = cityObj.getString("name");
            String country = cityObj.getString("country");

            //Forecast Info
            JSONArray listObj = jsonObject.getJSONArray("list");
            for (int i = 0; i < 10; i++) {
                JSONObject forecastObj = listObj.getJSONObject(i);
                int dt = forecastObj.getInt("dt");
                JSONObject weatherObj = forecastObj.getJSONArray("weather").getJSONObject(0);
                JSONObject tempObj = forecastObj.getJSONObject("temp");
                double temp = tempObj.getDouble("day");
                double temp_min = tempObj.getDouble("min");
                double temp_max = tempObj.getDouble("max");
                double pressure = forecastObj.getDouble("pressure");
                double wind = forecastObj.getDouble("speed");
                double humidity = forecastObj.getDouble("humidity");
                String main = weatherObj.getString("main");
                String des = weatherObj.getString("description");
                String icon = weatherObj.getString("icon");
                weather = new Weather(name, country, main, des, icon, lat, lng, temp, temp_min, temp_max,pressure,humidity,wind);
                forecast.add(weather);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return forecast;
    }

    private Weather getCurrentWeather(String weatherURL) {

        Weather weather = null;
        try {
            URL url = new URL(weatherURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"));
            String json = reader.readLine();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject coordObj = jsonObject.getJSONObject("coord");
            JSONObject weatherObj = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject dataObj = jsonObject.getJSONObject("main");
            JSONObject sysObj = jsonObject.getJSONObject("sys");

            String name = jsonObject.getString("name");
            double lat = coordObj.getDouble("lat");
            double lng = coordObj.getDouble("lon");
            String main = weatherObj.getString("main");
            String des = weatherObj.getString("description");
            String icon = weatherObj.getString("icon");
            double temp = dataObj.getDouble("temp");
            double temp_min = dataObj.getDouble("temp_min");
            double temp_max = dataObj.getDouble("temp_max");
            double pressure = dataObj.getDouble("pressure");
            double humidity = dataObj.getDouble("humidity");
            double wind = jsonObject.getJSONObject("wind").getDouble("speed");
            String country = sysObj.getString("country");
            weather = new Weather(name, country, main, des, icon, lat, lng, temp, temp_min, temp_max,pressure,humidity,wind);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weather;
    }

    @Override
    protected void onPostExecute(ArrayList<Weather> Forecast) {
        super.onPostExecute(Forecast);
        activity.postWeather(Forecast);
    }
}
