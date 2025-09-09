package com.carlosribeiro.tempo_wt.data.remote

import com.carlosribeiro.tempo_wt.data.model.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Clima atual por cidade
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): CurrentWeatherResponse

    // Previsão 5 dias / 3h
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): ForecastResponse
}
