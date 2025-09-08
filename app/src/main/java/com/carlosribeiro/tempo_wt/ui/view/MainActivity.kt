package com.carlosribeiro.tempo_wt.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.carlosribeiro.tempo_wt.R

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etCity = findViewById<EditText>(R.id.etCity)
        val btnLoad = findViewById<Button>(R.id.btnLoad)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnLoad.setOnClickListener {
            val city = etCity.text.toString().ifBlank { "Houston" }
            viewModel.loadWeather(city)
        }

        viewModel.weather.observe(this) { result ->
            result.onSuccess { response ->   // renomeei pra response
                val desc = response.weather.firstOrNull()?.description ?: "-"
                val text = """
            Cidade: ${response.name}
            Temp: ${response.main.temp}°C
            Sensação: ${response.main.feels_like}°C
            Umidade: ${response.main.humidity}%
            Clima: $desc
        """.trimIndent()

                tvResult.text = text
            }.onFailure { e ->
                tvResult.text = "Erro: ${e.message}"
            }
        }
    }
}
