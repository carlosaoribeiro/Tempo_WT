package com.carlosribeiro.tempo_wt.data.model

data class ForecastUiItem(
    val date: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String
)
