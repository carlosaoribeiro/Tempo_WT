package com.carlosaoribeiro.tempo_wt.data.repository


import com.carlosribeiro.tempo_wt.data.model.WeatherDomain
import kotlin.random.Random

class WeatherRepository {
    fun getWeatherByCity(city: String): WeatherDomain {
        val temp = Random.nextInt(10, 35) + Random.nextDouble()
        val desc = listOf("Clear sky", "Cloudy", "Rainy", "Thunderstorm").random()
        return WeatherDomain(
            city = city,
            temperatureC = String.format("%.1f", temp).toDouble(),
            description = desc
        )
    }
}
