package com.carlosribeiro.tempo_wt.data.model

data class WeatherResponse(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)

data class CurrentWeather(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val wind_speed: Double,
    val weather: List<WeatherDesc>
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val weather: List<WeatherDesc>
)

data class DailyWeather(
    val dt: Long,
    val temp: Temp,
    val weather: List<WeatherDesc>
)

data class Temp(
    val min: Double,
    val max: Double
)

data class WeatherDesc(
    val description: String,
    val icon: String
)
