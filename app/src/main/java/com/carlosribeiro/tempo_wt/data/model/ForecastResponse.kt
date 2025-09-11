package com.carlosribeiro.tempo_wt.data.model

data class ForecastResponse(
    val list: List<ForecastData>
)

data class ForecastData(
    val dt: Long,
    val main: ForecastMain,
    val weather: List<WeatherDesc>
)

data class ForecastMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)
