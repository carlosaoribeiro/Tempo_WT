package com.carlosribeiro.tempo_wt.data.repository

import com.carlosribeiro.tempo_wt.data.model.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.ForecastResponse
import com.carlosribeiro.tempo_wt.data.remote.NetworkModule
import com.carlosribeiro.tempo_wt.data.remote.WeatherApiService

class WeatherRepository(private val apiKey: String) {

    private val api = NetworkModule.create(WeatherApiService::class.java)

    suspend fun getCurrentWeather(city: String): CurrentWeatherResponse {
        return api.getCurrentWeather(city, apiKey)
    }

    suspend fun getForecast(city: String): ForecastResponse {
        return api.getForecast(city, apiKey)
    }
}
