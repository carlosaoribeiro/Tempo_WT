package com.carlosribeiro.tempo_wt.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.carlosribeiro.tempo_wt.ui.model.ForecastUiItem
import com.carlosribeiro.tempo_wt.data.remote.RetrofitInstance
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import com.carlosribeiro.tempo_wt.databinding.ActivityMainBinding
import com.carlosribeiro.tempo_wt.ui.adapter.DailyForecastAdapter
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecastAdapter
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Clique na lupa do campo de busca
        binding.searchInputLayout.setEndIconOnClickListener {
            val city = binding.etSearch.text?.toString().orEmpty()
            if (city.isNotBlank()) {
                binding.progress.visibility = View.VISIBLE
                viewModel.loadWeather(city)
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 📍 Botão de localização
        binding.btnLocation.setOnClickListener {
            checkLocationPermissionAndFetchWeather()
        }

        // 🔄 Configuração dos RecyclerViews
        binding.rvHourly.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvDaily.layoutManager = LinearLayoutManager(this)

        val api = RetrofitInstance.api
        val repository = WeatherRepository(api, "bb6ecc2665b7996900f60174b6731200")
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // 🔎 Busca por cidade
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val city = binding.etSearch.text?.toString().orEmpty().ifBlank { "Osasco" }
            binding.progress.visibility = View.VISIBLE
            viewModel.loadWeather(city)
            true
        }

        // 🌡️ Toggle de temperatura
        binding.btnTemperatureToggle.setOnClickListener {
            val current = viewModel.getCurrentUnits()
            val newUnit = if (current == "metric") {
                binding.btnTemperatureToggle.text = "F"
                "imperial"
            } else {
                binding.btnTemperatureToggle.text = "C"
                "metric"
            }
            viewModel.setCurrentUnits(newUnit)
            checkLocationPermissionAndFetchWeather()
        }

        // 🔄 Observa clima atual
        viewModel.current.observe(this) { result ->
            binding.progress.visibility = View.GONE

            result.onSuccess { response ->
                binding.tvCity.text = response.name ?: "-"
                val main = response.main
                val unitSymbol = if (viewModel.getCurrentUnits() == "metric") "C" else "F"
                val windUnit = if (viewModel.getCurrentUnits() == "metric") "m/s" else "mph"

                // 🌡️ Clima principal
                binding.tvTemp.text = main?.temp?.let { "$it°$unitSymbol" } ?: "--°$unitSymbol"
                binding.tvFeelsLike.text = "Sensação: ${main?.feels_like ?: "--"}°$unitSymbol"
                binding.tvHumidity.text = "Umidade: ${main?.humidity ?: "--"}%"
                binding.tvWind.text = "Vento: ${response.wind?.speed ?: "--"} $windUnit"

                // 🌤️ Descrição + ícone
                val w = response.weather?.firstOrNull()
                binding.tvDesc.text = w?.description ?: "-"
                val icon = w?.icon
                if (!icon.isNullOrBlank()) {
                    binding.ivIcon.load("https://openweathermap.org/img/wn/${icon}@4x.png")
                } else {
                    binding.ivIcon.setImageDrawable(null)
                }

                // 🌍 Weather Details Card
                binding.tvHumidityDetail.text = "${main?.humidity ?: "--"}%"

                // 🌬️ Vento (com direção)
                val windSpeed = response.wind?.speed ?: 0.0
                val windDir = response.wind?.deg?.let { deg -> getWindDirection(deg) } ?: ""
                binding.tvWindDetail.text = "$windSpeed $windUnit $windDir"

                // 👁️ Visibilidade
                val visibilityMeters = response.visibility ?: 0
                val visibilityText = if (viewModel.getCurrentUnits() == "metric") {
                    "${visibilityMeters / 1000} km"
                } else {
                    String.format(Locale.getDefault(), "%.1f mi", visibilityMeters / 1609.34)
                }
                binding.tvVisibility.text = visibilityText

                // 🌡️ Pressão
                binding.tvPressure.text = "${main?.pressure ?: "--"} hPa"

                // ☀️ Nascer/pôr do sol
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                val sunrise = response.sys?.sunrise?.let { ts -> sdf.format(Date(ts * 1000L)) } ?: "--:--"
                val sunset = response.sys?.sunset?.let { ts -> sdf.format(Date(ts * 1000L)) } ?: "--:--"
                binding.tvSunrise.text = sunrise
                binding.tvSunset.text = sunset
            }

            result.onFailure { e ->
                Toast.makeText(this, "Erro (current): ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_CURRENT", "Falha", e)
            }
        }

        // 🔄 Observa previsão (horária e diária)
        viewModel.forecast.observe(this) { result ->
            result.onSuccess { forecast ->
                Log.d("API_FORECAST", "Qtd blocos: ${forecast.list?.size}")
                val forecastList = forecast.list ?: emptyList()

                // ⏱️ Próximas 12h
                val hourlyItems = forecastList
                    .take(12)
                    .map { data ->
                        val w = data.weather.firstOrNull()
                        ForecastUiItem(
                            date = data.dt,
                            minTemp = data.main.temp ?: 0.0,
                            maxTemp = data.main.temp ?: 0.0,
                            description = w?.description ?: "-",
                            icon = w?.icon ?: "",
                            rain = (data.pop?.times(100))?.toInt()
                        )
                    }
                binding.rvHourly.adapter = HourlyForecastAdapter(hourlyItems)

                // 📅 Previsão diária (7 dias)
                val dailyItems = forecastList
                    .filterIndexed { index, _ -> index % 8 == 0 }
                    .take(7)
                    .map { data ->
                        val min = data.main.temp_min ?: data.main.temp
                        val max = data.main.temp_max ?: data.main.temp
                        val w = data.weather.firstOrNull()
                        ForecastUiItem(
                            date = data.dt,
                            minTemp = min ?: 0.0,
                            maxTemp = max ?: 0.0,
                            description = w?.description ?: "-",
                            icon = w?.icon ?: "",
                            rain = (data.pop?.times(100))?.toInt()
                        )
                    }
                binding.rvDaily.adapter = DailyForecastAdapter(dailyItems)
            }

            result.onFailure { e ->
                Toast.makeText(this, "Erro forecast: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_FORECAST", "Falha", e)
            }
        }

        // 🌍 Inicializa com localização atual
        checkLocationPermissionAndFetchWeather()
    }

    private fun checkLocationPermissionAndFetchWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                viewModel.loadWeatherByCoordinates(lat, lon)
            } else {
                Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    // Função auxiliar para direção do vento
    private fun getWindDirection(deg: Int): String {
        return when (deg) {
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..337 -> "NW"
            else -> "N"
        }
    }
}
