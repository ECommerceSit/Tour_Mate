package com.urahamat01.myapplicationtour;

import com.urahamat01.myapplicationtour.current_weather.WeatherResponse;
import com.urahamat01.myapplicationtour.weather.WeatherResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IOpenWeatherMap {




     @GET
     Call<WeatherResponse> getWeatherData1(@Url String url);




    @GET
    Call<WeatherResult> getWeatherData(@Url String url);


}
