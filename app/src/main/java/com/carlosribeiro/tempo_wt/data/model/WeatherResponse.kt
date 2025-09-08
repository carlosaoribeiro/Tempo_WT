package com.carlosribeiro.tempo_wt.data.model

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherInfo>,
    val wind: Wind?            // novo (pode vir null)
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

data class Wind(
    val speed: Float
)