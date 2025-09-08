package com.carlosribeiro.tempo_wt.data

import com.carlosribeiro.tempo_wt.data.model.WeatherResponse
import com.carlosribeiro.tempo_wt.data.remote.NetworkModule
import com.carlosribeiro.tempo_wt.data.remote.WeatherApiService

class WeatherRepository {

    // cria a instância do service usando o NetworkModule
    private val api = NetworkModule.create(WeatherApiService::class.java)

    // função suspensa que busca o clima atual
    suspend fun getCurrentWeather(city: String): WeatherResponse {
        return api.getCurrentWeather(city)
    }
}
