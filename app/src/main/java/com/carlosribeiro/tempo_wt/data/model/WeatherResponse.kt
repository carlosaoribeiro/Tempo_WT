package com.carlosribeiro.tempo_wt.data.model

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherInfo>
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val humidity: Int
)

data class WeatherInfo(
    val description: String,
    val icon: String
)