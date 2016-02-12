package com.weijie.weatheradvisor;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tv1 = (TextView) findViewById(R.id.textView_day);
        tv2 = (TextView) findViewById(R.id.textView_main);
        tv3 = (TextView) findViewById(R.id.textView_sub);
        tv4 = (TextView) findViewById(R.id.textView_high);
        tv5 = (TextView) findViewById(R.id.textView_low);
        tv6 = (TextView) findViewById(R.id.textView_details);
        imageView = (ImageView) findViewById(R.id.imageView);

        int day = getIntent().getIntExtra("day", 0);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, day);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String wkday = sdf.format(c.getTime());
        tv1.setText(wkday);
        boolean ForC = getIntent().getBooleanExtra("F/C",true);

        Weather detailWeather = getIntent().getParcelableExtra("weather_detail");
        tv2.setText(detailWeather.getMain_des());
        tv3.setText(detailWeather.getSub_des());
        int high = ForC?toFara(detailWeather.getTemp_max()):toCel(detailWeather.getTemp_max());
        int low = ForC?toFara(detailWeather.getTemp_min()):toCel(detailWeather.getTemp_min());

        tv4.setText(String.valueOf(high));
        tv5.setText(String.valueOf(low));
        tv6.setText("Humidity: " + detailWeather.getHumidity()+"%\n"+"Wind: "+detailWeather.getWind()+"km/h\n"+"Pressure: "+detailWeather.getPressure()+" hPa");
        String imgUri = "http://openweathermap.org/img/w/"+detailWeather.getIcon()+".png";
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imgUri, imageView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private int toCel(double temp) {
        int result = (int)(temp - 273.15);
        return result;
    }

    private int toFara(double temp) {
        int result = (int)((temp - 273.15)*1.8 + 32);
        return result;
    }
}
