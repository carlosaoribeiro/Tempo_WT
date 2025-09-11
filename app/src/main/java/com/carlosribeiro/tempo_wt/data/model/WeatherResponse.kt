package com.carlosribeiro.tempo_wt.data.model

// 🔹 Modelo principal da One Call API
data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,

    // Nome da cidade não vem no One Call → injetamos manualmente no ViewModel
    var cityName: String? = null
)

// 🔹 Clima atual
data class CurrentWeather(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDesc>
)

// 🔹 Previsão horária
data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDesc>,
    val pop: Double
)

// 🔹 Previsão diária
data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDesc>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

// 🔹 Descrição do clima
data class WeatherDesc(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
