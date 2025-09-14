package com.carlosribeiro.tempo_wt.mapper

import com.carlosribeiro.tempo_wt.data.model.forecast.ForecastItem
import com.carlosribeiro.tempo_wt.ui.model.ForecastUiItem

object DailyWeatherMapper {
    fun mapToUi(dailyList: List<ForecastItem>): List<ForecastUiItem> {
        return dailyList.map { daily ->
            ForecastUiItem(
                date = daily.dt,
                minTemp = daily.main.temp_min ?: 0.0,
                maxTemp = daily.main.temp_max ?: 0.0,
                description = daily.weather.firstOrNull()?.description ?: "-",
                icon = daily.weather.firstOrNull()?.icon ?: "01d",
                rain = (daily.pop?.times(100))?.toInt()
            )
        }
    }
}

