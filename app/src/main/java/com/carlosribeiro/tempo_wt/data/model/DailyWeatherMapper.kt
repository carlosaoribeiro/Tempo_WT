package com.carlosribeiro.tempo_wt.data.model

object DailyWeatherMapper {
    fun mapToUi(dailyList: List<DailyWeather>): List<ForecastUiItem> {
        return dailyList.map { daily ->
            ForecastUiItem(
                date = daily.dt,
                minTemp = daily.temp.min,
                maxTemp = daily.temp.max,
                description = daily.weather.firstOrNull()?.description ?: "-",
                icon = daily.weather.firstOrNull()?.icon ?: "01d"
            )
        }
    }
}
