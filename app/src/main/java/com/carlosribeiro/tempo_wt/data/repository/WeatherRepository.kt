package com.carlosribeiro.tempo_wt.data.repository


import com.carlosribeiro.tempo_wt.data.model.current.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.forecast.ForecastResponse
import com.carlosribeiro.tempo_wt.data.remote.WeatherApiService

class WeatherRepository(
    private val api: WeatherApiService,
    private val apiKey: String
) {

    suspend fun getCurrentWeather(city: String, units: String): CurrentWeatherResponse {
        return api.getCurrentWeather(city, apiKey, units)
    }

    suspend fun getForecast(city: String, units: String): ForecastResponse {
        return api.getForecast(city, apiKey, units)
    }

    suspend fun getCurrentWeatherByCoords(lat: Double, lon: Double, units: String): CurrentWeatherResponse {
        return api.getCurrentWeatherByCoordinates(lat, lon, apiKey, units)
    }

    suspend fun getForecastByCoords(lat: Double, lon: Double, units: String): ForecastResponse {
        return api.getForecastByCoordinates(lat, lon, apiKey, units)
    }
}
