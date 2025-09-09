package com.carlosribeiro.tempo_wt.data.remote

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    val name: String?,
    val weather: List<WeatherItem>?,
    val main: Main?,
    val wind: Wind?,
    val visibility: Int// ✅ Adicione esta linha aqui
)

data class WeatherItem(
    val description: String?,
    val icon: String?
)

data class Main(
    val temp: Double?,
    val feels_like: Double?,
    val humidity: Int?
)

data class Wind(
    val speed: Double?
)
