package com.carlosribeiro.tempo_wt.data.model

data class CurrentWeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherDesc>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)
