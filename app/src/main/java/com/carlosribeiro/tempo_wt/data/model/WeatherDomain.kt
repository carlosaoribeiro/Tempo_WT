package com.carlosribeiro.tempo_wt.data.model

data class WeatherDomain(
    val city: String?,
    val temperatureC: Double?,
    val description: String?,
    val feelsLike: Double?,
    val humidity: Int?,
    val windSpeed: Double?,
    val icon: String?
)

