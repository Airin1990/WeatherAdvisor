package com.weijie.weatheradvisor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String[] weekdays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    GoogleApiClient mgoogleApiClient;
    LocationRequest mlocationRequest;

    String zipcode;
    String locality;
    double lat;
    double lng;

    TextView textView;
    TextView textView2;
    TextView textView3;
    TextView textView4;

    Boolean FarenOrCel;

    WeatherAdapter myAdapter;
    ListView listView;
    View view;
    ImageView imageView;
    ArrayList<Weather> parcelweather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.locality_textview);
        textView2 = (TextView) findViewById(R.id.weather_textview);
        textView3 = (TextView) findViewById(R.id.des_textview);
        textView4 = (TextView) findViewById(R.id.temprange_textview);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        view = findViewById(R.id.main_layout);
        view.getBackground().setAlpha(150);
        imageView = (ImageView) findViewById(R.id.imageView2);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);


        listView = (ListView)findViewById(R.id.listView_weather);

//        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_forecast,R.id.forecast_singlerow, days);
//        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("weather_detail",parcelweather.get(position));
                intent.putExtra("day",position+1);
                intent.putExtra("F/C",FarenOrCel);
                startActivity(intent);
            }
        });

    }

    public void postWeather (ArrayList<Weather> weathers) {


        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        //Post Current Weather
        Weather currentWeather = weathers.get(0);
        textView.setText(currentWeather.getLocal());
        textView2.setText(currentWeather.getMain_des());
        textView3.setText(currentWeather.getSub_des());
        if (FarenOrCel) {
            textView4.setText(weekdays[((dayOfWeek-1)%7)]+" Today" + "\n"+toFara(currentWeather.getTemp()) + "°F");
        }
        else {
            textView4.setText(weekdays[((dayOfWeek-1)%7)]+" Today" + "\n"+toCel(currentWeather.getTemp()) + "°C");
        }

        String imgUrl = "http://openweathermap.org/img/w/"+currentWeather.getIcon()+".png";

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imgUrl, imageView);
        //Post Weather Forecast
        ArrayList<Weather> forecast = new ArrayList<>(weathers);
        forecast.remove(0);
        updateForecast(forecast);
    }

    private void updateForecast(ArrayList<Weather> forecast) {

        parcelweather = forecast;
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);


        String[] wkday = new String[forecast.size()];
        String[] wth = new String[forecast.size()];
        String[] bmp = new String[forecast.size()];
        int[] temp = new int[forecast.size()];
        int[] temp_min = new int[forecast.size()];

        for (int i = 0; i < forecast.size(); i++) {
            Weather weather = forecast.get(i);

            wkday[i] = weekdays[((dayOfWeek+i)%7)];
            wth[i] = weather.getMain_des();
            bmp[i] = "http://openweathermap.org/img/w/"+weather.getIcon()+".png";

            temp[i] = (FarenOrCel)?toFara(weather.getTemp()):toCel(weather.getTemp());
            temp_min[i] = (FarenOrCel)?toFara(weather.getTemp_min()):toCel(weather.getTemp_min());
        }


        myAdapter = new WeatherAdapter(this,wkday,wth,bmp,temp,temp_min);
        listView.setAdapter(myAdapter);

    }

    private int toCel(double temp) {
        int result = (int)(temp - 273.15);
        return result;
    }

    private int toFara(double temp) {
        int result = (int)((temp - 273.15)*1.8 + 32);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.locate_item) {
            mgoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mgoogleApiClient.connect();
        }
        else if (id == R.id.refresh_item) {
            new GetCurrentWeather(this).execute(lat,lng);
        }
        else if (id == R.id.unit_item) {
            FarenOrCel = !FarenOrCel;
            new GetCurrentWeather(this).execute(lat,lng);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mlocationRequest = LocationRequest.create();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Toast.makeText(this, "Can't get Location!", Toast.LENGTH_SHORT).show();
        }
        else {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                Address address = addresses.get(0);
                zipcode = address.getPostalCode();
                locality = address.getLocality();
                lat = location.getLatitude();
                lng = location.getLongitude();
                LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient, this);

                Toast.makeText(this,"Current Location: " + locality +" Zipcode: "+ zipcode, Toast.LENGTH_SHORT).show();
                textView.setText(locality);
                GetCurrentWeather getCurrentWeather = new GetCurrentWeather(this);
                getCurrentWeather.execute(lat, lng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("MyPrefs",0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("lat", (float) lat);
        editor.putFloat("lng", (float) lng);
        editor.putBoolean("Far", FarenOrCel);
        editor.commit();
        //update sharedPref
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update city
        SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        lat = pref.getFloat("lat",50);
        lng = pref.getFloat("lng", 50);
        FarenOrCel = pref.getBoolean("Far",true);
        GetCurrentWeather getCurrentWeather = new GetCurrentWeather(this);
        getCurrentWeather.execute(lat, lng);
    }

}
