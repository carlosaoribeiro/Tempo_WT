package com.carlosribeiro.tempo_wt.data.remote


data class OpenWeatherResponse(
    val name: String?,
    val weather: List<WeatherItem>?,
    val main: Main?,
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
