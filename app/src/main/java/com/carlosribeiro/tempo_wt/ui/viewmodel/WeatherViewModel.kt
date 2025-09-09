package com.carlosribeiro.tempo_wt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.tempo_wt.data.model.CurrentWeatherResponse
import com.carlosribeiro.tempo_wt.data.model.ForecastResponse
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val current: CurrentWeatherResponse, val forecast: ForecastResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val current = repository.getCurrentWeather(city)
                val forecast = repository.getForecast(city)
                _uiState.value = WeatherUiState.Success(current, forecast)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

class WeatherViewModelFactory(private val apiKey: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            val repo = WeatherRepository(apiKey)
            return WeatherViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
