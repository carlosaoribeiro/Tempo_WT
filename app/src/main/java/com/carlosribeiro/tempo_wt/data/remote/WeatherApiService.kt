package com.carlosribeiro.tempo_wt.data.remote

import com.carlosribeiro.tempo_wt.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {

    // Endpoint: /data/2.5/weather?q={city name}&appid={API key}
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String
    ): WeatherResponse

}