package com.carlosribeiro.tempo_wt.data.remote


import com.carlosribeiro.tempo_wt.data.model.current.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.forecast.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 🔹 Clima atual por cidade
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): CurrentWeatherResponse

    // 🔹 Previsão 5 dias / blocos de 3h
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): ForecastResponse

    @GET("data/2.5/onecall")
    suspend fun getWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): com.carlosribeiro.tempo_wt.data.model.WeatherResponse


    // 🔹 Geocoding (para converter cidade → lat/lon)
    @GET("geo/1.0/direct")
    suspend fun getCoordsByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("limit") limit: Int = 1
    ): List<GeoResponse>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): ForecastResponse


}

// 🔹 Modelo auxiliar do Geocoding
data class GeoResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)
