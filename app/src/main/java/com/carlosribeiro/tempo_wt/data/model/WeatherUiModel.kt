package com.carlosribeiro.tempo_wt.data.model

import com.carlosribeiro.tempo_wt.ui.model.ForecastUiItem

data class WeatherUiModel(
    val city: String,
    val description: String,
    val icon: String?,
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val updatedAt: Long,
    val forecast: List<ForecastUiItem>
)

