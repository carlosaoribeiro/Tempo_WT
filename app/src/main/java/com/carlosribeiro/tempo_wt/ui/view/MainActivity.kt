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
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import com.carlosribeiro.tempo_wt.ui.adapter.DailyAdapter
import com.carlosribeiro.tempo_wt.ui.adapter.DailyForecast
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyAdapter
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecast
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherUiState
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // ⚡ coloque sua API Key do OpenWeather
    private val apiKey = "5172f522f1b9d13fb238fc95c1393f4a"

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(apiKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.tempo_wt)
        supportActionBar?.subtitle = "Clima agora"

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val top = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = top)
            insets
        }

        // Views
        val etCity    = findViewById<TextInputEditText>(R.id.etCity)
        val btnLoad   = findViewById<MaterialButton>(R.id.btnLoad)
        val ivIcon    = findViewById<ImageView>(R.id.ivIcon)
        val tvCity    = findViewById<TextView>(R.id.tvCity)
        val tvTemp    = findViewById<TextView>(R.id.tvTemp)
        val tvDesc    = findViewById<TextView>(R.id.tvDesc)
        val tvFeels   = findViewById<TextView>(R.id.tvFeelsLike)
        val tvHum     = findViewById<TextView>(R.id.tvHumidity)
        val tvWind    = findViewById<TextView>(R.id.tvWind)
        val tvUpdated = findViewById<TextView>(R.id.tvUpdated)
        val progress  = findViewById<View>(R.id.progress)

        val rvHourly  = findViewById<RecyclerView>(R.id.rvHourly)
        val rvDaily   = findViewById<RecyclerView>(R.id.rvDaily)

        rvHourly.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDaily.layoutManager = LinearLayoutManager(this)

        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Clique no botão de buscar
        btnLoad.setOnClickListener {
            val city = etCity.text?.toString().orEmpty().ifBlank { "Osasco" }
            viewModel.fetchWeather(city)
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
                            tvTemp.text = "%.1f°C".format(current.main.temp)
                            tvDesc.text = current.weather.firstOrNull()?.description ?: "-"
                            ivIcon.load("https://openweathermap.org/img/wn/${current.weather.firstOrNull()?.icon}@4x.png")

                            tvFeels.text = "Sensação: %.1f°C".format(current.main.feels_like)
                            tvHum.text = "Umidade: ${current.main.humidity}%"
                            tvWind.text = "Vento: %.1f m/s".format(current.wind.speed)

                            // Hourly (usa as primeiras 12 entradas = 36h, 3h cada)
                            val hourlyList = forecast.list.take(12).map {
                                com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecast(
                                    hour = SimpleDateFormat("HH'h'", Locale.getDefault()).format(Date(it.dt * 1000)),
                                    temp = "%.0f°".format(it.main.temp),
                                    iconUrl = "https://openweathermap.org/img/wn/${it.weather.firstOrNull()?.icon}@2x.png"
                                )
                            }
                            rvHourly.adapter = HourlyAdapter(hourlyList)

                            // Daily (agrupar por dia, aqui simplifiquei pegando de 8 em 8 = 24h)
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

                            tvUpdated.text = "Atualizado: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
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
