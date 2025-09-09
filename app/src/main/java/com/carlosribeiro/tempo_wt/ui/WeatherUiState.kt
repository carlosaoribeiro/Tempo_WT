package com.carlosribeiro.tempo_wt.ui

import com.carlosribeiro.tempo_wt.data.model.WeatherDomain

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val data: WeatherDomain) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
