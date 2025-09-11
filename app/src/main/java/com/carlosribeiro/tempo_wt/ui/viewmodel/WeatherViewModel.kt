package com.carlosribeiro.tempo_wt.ui.viewmodel

import androidx.lifecycle.*
import com.carlosribeiro.tempo_wt.data.model.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.ForecastResponse
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _current = MutableLiveData<Result<CurrentWeatherResponse>>()
    val current: LiveData<Result<CurrentWeatherResponse>> = _current

    private val _forecast = MutableLiveData<Result<ForecastResponse>>()
    val forecast: LiveData<Result<ForecastResponse>> = _forecast

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                val currentWeather = repository.getCurrentWeather(city)
                _current.postValue(Result.success(currentWeather))

                val forecastWeather = repository.getForecast(city)
                _forecast.postValue(Result.success(forecastWeather))

            } catch (e: Exception) {
                _current.postValue(Result.failure(e))
                _forecast.postValue(Result.failure(e))
            }
        }
    }
}
