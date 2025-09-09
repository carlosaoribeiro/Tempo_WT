package com.carlosribeiro.tempo_wt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel

class WeatherViewModelFactory(private val apiKey: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            val repo = WeatherRepository(apiKey) // ✅ passando apiKey
            return WeatherViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
