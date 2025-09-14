package com.carlosribeiro.tempo_wt.data.model.forecast

import com.carlosribeiro.tempo_wt.data.model.current.WeatherDesc

data class ForecastResponse(
    val list: List<ForecastItem>?
)

data class ForecastItem(
    val dt: Long,
    val main: MainForecast,
    val weather: List<WeatherDesc>,
    val pop: Double? = null   // 👈 probabilidade de precipitação
)

data class MainForecast(
    val temp: Double?,
    val temp_min: Double?,
    val temp_max: Double?
)
