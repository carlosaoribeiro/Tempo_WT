package com.carlosribeiro.tempo_wt.data.repository

import com.carlosribeiro.tempo_wt.data.model.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.ForecastResponse
import com.carlosribeiro.tempo_wt.data.remote.WeatherApiService

class WeatherRepository(
    private val api: WeatherApiService,
    private val apiKey: String
) {

    // Clima atual por cidade
    suspend fun getCurrentWeather(city: String): CurrentWeatherResponse {
        return api.getCurrentWeather(city, apiKey)
    }

    // Previsão 5 dias (3h em 3h)
    suspend fun getForecast(city: String): ForecastResponse {
        return api.getForecast(city, apiKey)
    }

    suspend fun getCurrentWeatherByCoords(lat: Double, lon: Double): CurrentWeatherResponse {
        return api.getCurrentWeatherByCoordinates(lat, lon, apiKey)
    }

    suspend fun getForecastByCoords(lat: Double, lon: Double): ForecastResponse {
        return api.getForecastByCoordinates(lat, lon, apiKey)
    }
}

