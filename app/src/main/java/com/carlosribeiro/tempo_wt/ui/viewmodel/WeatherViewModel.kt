package com.carlosribeiro.tempo_wt.ui.viewmodel

import androidx.lifecycle.*
import com.carlosribeiro.tempo_wt.data.model.current.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.forecast.ForecastResponse
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _current = MutableLiveData<Result<CurrentWeatherResponse>>()
    val current: LiveData<Result<CurrentWeatherResponse>> = _current

    private val _forecast = MutableLiveData<Result<ForecastResponse>>()
    val forecast: LiveData<Result<ForecastResponse>> = _forecast

    private var currentUnits: String = "metric" // Celsius como padrão

    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _current.value = runCatching {
                repository.getCurrentWeatherByCoords(lat, lon, currentUnits)
            }
            _forecast.value = runCatching {
                repository.getForecastByCoords(lat, lon, currentUnits)
            }
        }
    }

    fun getCurrentUnits(): String {
        return currentUnits
    }

    fun setCurrentUnits(units: String) {
        currentUnits = units
    }

    fun loadWeather(city: String, units: String = currentUnits) {
        currentUnits = units
        viewModelScope.launch {
            try {
                val currentWeather = repository.getCurrentWeather(city, units)
                _current.postValue(Result.success(currentWeather))

                val forecastWeather = repository.getForecast(city, units)
                _forecast.postValue(Result.success(forecastWeather))
            } catch (e: Exception) {
                _current.postValue(Result.failure(e))
                _forecast.postValue(Result.failure(e))
            }
        }
    }
}
