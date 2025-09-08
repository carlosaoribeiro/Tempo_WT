package com.carlosaoribeiro.tempo_wt.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carlosaoribeiro.tempo_wt.data.repository.WeatherRepository
import com.carlosribeiro.tempo_wt.data.model.WeatherDomain

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _weatherDomain = MutableLiveData<WeatherDomain>()
    val weatherDomain: LiveData<WeatherDomain> = _weatherDomain

    fun fetch(city: String) {
        if (city.isBlank()) return
        _weatherDomain.value = repository.getWeatherByCity(city)
    }
}
