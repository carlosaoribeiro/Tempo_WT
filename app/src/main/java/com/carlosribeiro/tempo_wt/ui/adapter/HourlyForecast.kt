package com.carlosribeiro.tempo_wt.ui.adapter

data class HourlyForecast(
    val hour: String,
    val temp: Double,
    val iconUrl: String,
    val rain: Int? = null   // 🌧️ porcentagem de chuva
)
