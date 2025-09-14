package com.carlosribeiro.tempo_wt.data.model.current

data class CurrentWeatherResponse(
    val coord: Coord?,
    val weather: List<WeatherDesc>?,
    val base: String?,
    val main: MainCurrent?,   // 👈 renomeado
    val visibility: Int?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: Sys?,
    val timezone: Int?,
    val id: Long?,
    val name: String?,
    val cod: Int?
)

data class Coord(
    val lon: Double?,
    val lat: Double?
)

data class MainCurrent(
    val temp: Double?,
    val feels_like: Double?,
    val temp_min: Double?,
    val temp_max: Double?,
    val pressure: Int?,
    val humidity: Int?
)

data class Wind(
    val speed: Double?,
    val deg: Int?
)

data class Clouds(
    val all: Int?
)

data class Sys(
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?
)

data class WeatherDesc(
    val id: Int?,
    val main: String?,
    val description: String?,
    val icon: String?
)
