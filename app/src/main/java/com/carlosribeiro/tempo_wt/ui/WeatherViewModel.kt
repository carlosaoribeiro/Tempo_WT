package com.carlosribeiro.tempo_wt.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.tempo_wt.data.WeatherRepository
import com.carlosribeiro.tempo_wt.data.model.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    // LiveData para expor o estado
    private val _weather = MutableLiveData<Result<WeatherResponse>>()
    val weather: LiveData<Result<WeatherResponse>> = _weather

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCurrentWeather(city)
                _weather.postValue(Result.success(response))
            } catch (e: Exception) {
                _weather.postValue(Result.failure(e))
            }
        }
    }
}
