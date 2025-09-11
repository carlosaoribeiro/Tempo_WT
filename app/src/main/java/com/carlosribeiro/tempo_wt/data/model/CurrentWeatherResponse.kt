package com.carlosribeiro.tempo_wt.data.model

data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<WeatherDesc>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

data class Coord(val lon: Double, val lat: Double)
data class Main(val temp: Double, val feels_like: Double, val humidity: Int)
data class Wind(val speed: Double, val deg: Int)
data class Clouds(val all: Int)
data class Sys(val country: String, val sunrise: Long, val sunset: Long)
