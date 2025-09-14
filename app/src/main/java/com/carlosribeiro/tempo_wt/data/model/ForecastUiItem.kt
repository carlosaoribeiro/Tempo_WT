package com.carlosribeiro.tempo_wt.ui.model

data class ForecastUiItem(
    val date: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val description: String,
    val icon: String,
    val rain: Int? = null // 🌧️ porcentagem de chuva
)
