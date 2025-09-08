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
import coil.load
import com.carlosribeiro.tempo_wt.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.tempo_wt)
        supportActionBar?.subtitle = "Clima agora"

        // (Edge-to-edge) evita que o título fique sob a status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val top = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = top)
            insets
        }

        // Views
        val etCity = findViewById<TextInputEditText>(R.id.etCity)
        val btnLoad = findViewById<MaterialButton>(R.id.btnLoad)

        val ivIcon = findViewById<ImageView>(R.id.ivIcon)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvTemp = findViewById<TextView>(R.id.tvTemp)
        val tvDesc = findViewById<TextView>(R.id.tvDesc)
        val tvFeels = findViewById<TextView>(R.id.tvFeelsLike)
        val tvHum  = findViewById<TextView>(R.id.tvHumidity)
        val tvWind = findViewById<TextView>(R.id.tvWind)
        val tvUpdated = findViewById<TextView>(R.id.tvUpdated)
        val progress = findViewById<View>(R.id.progress)

        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        btnLoad.setOnClickListener {
            val city = etCity.text?.toString().orEmpty().ifBlank { "Osasco" }
            progress.visibility = View.VISIBLE
            viewModel.loadWeather(city)
        }

        viewModel.weather.observe(this) { result ->
            progress.visibility = View.GONE
            result.onSuccess { response ->
                tvCity.text = response.name
                tvTemp.text = "${response.main.temp}°C"
                tvDesc.text = response.weather.firstOrNull()?.description ?: "-"

                val icon = response.weather.firstOrNull()?.icon
                if (!icon.isNullOrBlank()) {
                    val url = "https://openweathermap.org/img/wn/${icon}@4x.png"
                    ivIcon.load(url)
                } else ivIcon.setImageDrawable(null)

                tvFeels.text = "Sensação: ${response.main.feels_like}°C"
                tvHum.text   = "Umidade: ${response.main.humidity}%"
                tvWind.text  = "Vento: ${response.wind?.speed ?: 0f} m/s"
                tvUpdated.text = "Atualizado: ${now()}"
            }.onFailure { e ->
                tvDesc.text = "Erro: ${e.message}"
            }
        }
    }
}
