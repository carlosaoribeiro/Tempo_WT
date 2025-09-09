package com.carlosribeiro.tempo_wt.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.carlosribeiro.tempo_wt.R
import com.carlosribeiro.tempo_wt.ui.adapter.DailyAdapter
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyAdapter
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherUiState
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val apiKey = "5172f522f1b9d13fb238fc95c1393f4a"

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(apiKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       /* // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.tempo_wt)
        supportActionBar?.subtitle = "Clima agora"*/

      /*  // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val top = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = top)
            insets
        }*/

        // Views do Card
        val etSearch     = findViewById<TextInputEditText>(R.id.etSearch)
        val btnLocation  = findViewById<View>(R.id.btnLocation)

        val ivIcon       = findViewById<ImageView>(R.id.ivIcon)
        val tvCity       = findViewById<TextView>(R.id.tvCity)
        val tvTemp       = findViewById<TextView>(R.id.tvTemp)
        val tvDesc       = findViewById<TextView>(R.id.tvDesc)
        val tvFeels      = findViewById<TextView>(R.id.tvFeelsLike)
        val tvHum        = findViewById<TextView>(R.id.tvHumidity)
        val tvWind       = findViewById<TextView>(R.id.tvWind)
        val tvVisibility = findViewById<TextView>(R.id.tvVisibility)
        val tvUvIndex    = findViewById<TextView>(R.id.tvUvIndex)
        val tvUpdated    = findViewById<TextView>(R.id.tvUpdated)
        val progress     = findViewById<View>(R.id.progress)

        val rvHourly  = findViewById<RecyclerView>(R.id.rvHourly)
        val rvDaily   = findViewById<RecyclerView>(R.id.rvDaily)

        rvHourly.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDaily.layoutManager = LinearLayoutManager(this)

        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Busca ao pressionar ENTER no teclado
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val city = etSearch.text?.toString().orEmpty().ifBlank { "Osasco" }
                viewModel.fetchWeather(city)
                true
            } else {
                false
            }
        }

        // Busca ao clicar no botão de localização
        btnLocation.setOnClickListener {
            val defaultCity = "Osasco" // futuramente GPS
            viewModel.fetchWeather(defaultCity)
        }

        // Observa o StateFlow do ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is WeatherUiState.Idle -> progress.visibility = View.GONE
                        is WeatherUiState.Loading -> progress.visibility = View.VISIBLE
                        is WeatherUiState.Success -> {
                            progress.visibility = View.GONE
                            val current = state.current
                            val forecast = state.forecast

                            // Cabeçalho
                            tvCity.text = current.name
                            tvDesc.text = current.weather.firstOrNull()?.description ?: "-"
                            ivIcon.load("https://openweathermap.org/img/wn/${current.weather.firstOrNull()?.icon}@4x.png")

                            // Temperatura
                            tvTemp.text = "%.0f".format(current.main.temp)
                            tvFeels.text = "Feels like %.0f°C".format(current.main.feels_like)

                            // Métricas (API + mockado)
                            tvHum.text = "Humidity: ${current.main.humidity}%"
                            tvWind.text = "Wind: %.1f km/h".format(current.wind.speed * 3.6)
                           // tvVisibility.text = getString(R.string.visibility, (current.visibility ?: 0) / 1000)



                            tvUvIndex.text = getString(R.string.uv_index, 6) // mockado
                            tvUpdated.text = getString(R.string.updated, now()) // mockado

                            // Hourly (primeiras 12 previsões = 36h)
                            val hourlyList = forecast.list.take(12).map {
                                com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecast(
                                    hour = SimpleDateFormat("HH'h'", Locale.getDefault()).format(Date(it.dt * 1000)),
                                    temp = "%.0f°".format(it.main.temp),
                                    iconUrl = "https://openweathermap.org/img/wn/${it.weather.firstOrNull()?.icon}@2x.png"
                                )
                            }
                            rvHourly.adapter = HourlyAdapter(hourlyList)

                            // Daily (5 dias, agrupando blocos de 8 = 24h)
                            val dailyList = forecast.list.chunked(8).take(5).map { dayGroup ->
                                val first = dayGroup.first()
                                com.carlosribeiro.tempo_wt.ui.adapter.DailyForecast(
                                    day = SimpleDateFormat("EEE", Locale("pt", "BR"))
                                        .format(Date(first.dt * 1000)),
                                    icon = first.weather.firstOrNull()?.icon ?: "01d",
                                    min = dayGroup.minOf { it.main.temp },
                                    max = dayGroup.maxOf { it.main.temp }
                                )
                            }
                            rvDaily.adapter = DailyAdapter(dailyList)
                        }

                        is WeatherUiState.Error -> {
                            progress.visibility = View.GONE
                            tvDesc.text = state.message
                            ivIcon.setImageDrawable(null)
                        }
                    }
                }
            }
        }
    }
}
