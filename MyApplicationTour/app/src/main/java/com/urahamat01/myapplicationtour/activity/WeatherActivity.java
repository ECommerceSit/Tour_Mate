package com.urahamat01.myapplicationtour.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.urahamat01.myapplicationtour.current_weather.WeatherResponse;
import com.urahamat01.myapplicationtour.IOpenWeatherMap;
import com.urahamat01.myapplicationtour.R;
import com.urahamat01.myapplicationtour.weather.WeatherResult;
import com.urahamat01.myapplicationtour.adapter.WeatherAdapter;
import com.urahamat01.myapplicationtour.retrofit.RetrofitClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView currentWeatherDiscription, currentWeathertemp, currentWeatherWind, currentWeatherLocatonTv, currentWeatherHumidity;
    private ImageView currentWeatherIcon;

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private WeatherResult weatherResult;
    private ImageView backWIv;
    private WeatherResult currentWeatherResult;
    private double lat = 0;
    private double lon = 0;
    private ProgressDialog loadinbar;
    private String units = "metric";
    String url;
    String url1;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        loadinbar = new ProgressDialog(WeatherActivity.this);
        currentWeatherDiscription = findViewById(R.id.cityNameCurrentTvId);
        currentWeatherIcon = findViewById(R.id.weatherCurrentIconIvId);
        currentWeathertemp = findViewById(R.id.tempCurrentWeitherTvId);
        currentWeatherWind = findViewById(R.id.windCurrentWeitherTvId);
        currentWeatherHumidity = findViewById(R.id.humidityCurrentWeitherTvId);
        currentWeatherLocatonTv = findViewById(R.id.cityStatusCurrentTvId);
        backWIv = findViewById(R.id.backWId);

        weatherResult = new WeatherResult();
        recyclerView = findViewById(R.id.weatherRecyclerViewId);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        backWIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getMyLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    url1 = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", location.getLatitude(), location.getLongitude(), units, getResources().getString(R.string.appid1));
                    url = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s", location.getLatitude(), location.getLongitude(), units, getResources().getString(R.string.appid));

                    loadinbar.setTitle("Weather");
                    loadinbar.setMessage("Loading 5 days weather");
                    loadinbar.show();
                    loadinbar.setCanceledOnTouchOutside(true);
                    getWeatherUpdate();
                }

            }
        });

    }


    private void getWeatherUpdate() {

        IOpenWeatherMap iOpenWeatherMap = RetrofitClass.getRetrofitInstance().create(IOpenWeatherMap.class);



        Call<WeatherResult> weatherResultCall = iOpenWeatherMap.getWeatherData(url);

        weatherResultCall.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if (response.code() == 200) {
                    weatherResult = response.body();
                    currentWeatherResult = weatherResult;
                    Toast.makeText(WeatherActivity.this, "success", Toast.LENGTH_SHORT).show();
                    weatherAdapter = new WeatherAdapter(WeatherActivity.this, weatherResult);
                    recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));
                    recyclerView.setAdapter(weatherAdapter);

                    loadinbar.dismiss();

                }
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {

            }
        });


        IOpenWeatherMap weatherService = RetrofitClass.getRetrofitInstance().create(IOpenWeatherMap.class);
        Call<WeatherResponse> weatherResponseCall = weatherService.getWeatherData1(url1);
        weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    currentWeathertemp.setText(String.valueOf(weatherResponse.getMain().getTemp()) + "°C");
                    currentWeatherLocatonTv.setText(String.valueOf("Dhaka, "+weatherResponse.getSys().getCountry()));
                    currentWeatherDiscription.setText(String.valueOf(weatherResponse.getWeather().get(0).getDescription()));
                    currentWeatherHumidity.setText("Humidity: " + (String.valueOf(weatherResponse.getMain().getHumidity())) + "%");
                    currentWeatherWind.setText("Wind       : " + (String.valueOf(weatherResponse.getWind().getSpeed())) + "km/h");
                    Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                            .append(weatherResponse.getWeather().get(0).getIcon())
                            .append(".png").toString()).into(currentWeatherIcon);
                }

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {


            }
        });
    }
}
