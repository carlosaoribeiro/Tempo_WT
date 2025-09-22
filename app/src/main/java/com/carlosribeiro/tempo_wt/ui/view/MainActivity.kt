package com.carlosribeiro.tempo_wt.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.carlosribeiro.tempo_wt.data.remote.RetrofitInstance
import com.carlosribeiro.tempo_wt.data.repository.WeatherRepository
import com.carlosribeiro.tempo_wt.databinding.ActivityMainBinding
import com.carlosribeiro.tempo_wt.ui.adapter.DailyForecastAdapter
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecast
import com.carlosribeiro.tempo_wt.ui.adapter.HourlyForecastAdapter
import com.carlosribeiro.tempo_wt.ui.model.ForecastUiItem
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔎 Clique na lupa
        binding.searchInputLayout.setEndIconOnClickListener {
            submitCity()
        }

        // 🔤 Filtro de caracteres válidos
        val cityInputFilter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val ch = source[i]
                if (!(ch.isLetter() || ch == ' ' || ch == '-' || ch == '\'' || ch == '’' || ch == '.')) {
                    return@InputFilter "" // bloqueia caractere inválido
                }
            }
            null
        }
        binding.etSearch.filters = arrayOf(cityInputFilter, InputFilter.LengthFilter(50))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 📍 Botão de localização
        binding.btnLocation.setOnClickListener {
            checkLocationPermissionAndFetchWeather()
        }

        // 📤 Botão de compartilhar temperatura
        binding.btnShared.setOnClickListener {
            val temp = binding.tvTemp.text.toString()
            val city = binding.tvCity.text.toString()

            if (temp.isNotBlank() && city.isNotBlank()) {
                val message = "Current temperature in $city is $temp 🌡️"

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "Share weather via")
                startActivity(shareIntent)
            } else {
                Toast.makeText(this, "Weather information not available yet", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔄 Configuração dos RecyclerViews
        binding.rvHourly.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDaily.layoutManager = LinearLayoutManager(this)

        val api = RetrofitInstance.api
        val repository = WeatherRepository(api, "bb6ecc2665b7996900f60174b6731200")
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // 🔎 Busca ao pressionar "enter"
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            submitCity()
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
                binding.tvTemp.text = main?.temp?.let { "${it.roundToInt()}°$unitSymbol" } ?: "--°$unitSymbol"
                binding.tvFeelsLike.text = "Sensação: ${(main?.feels_like)?.roundToInt() ?: "--"}°$unitSymbol"
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
                    String.Companion.format(Locale.getDefault(), "%.1f mi", visibilityMeters / 1609.34)
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
                val msg = if (e.message?.contains("404") == true) {
                    "City not found. Please check the name."
                } else {
                    "Error fetching current weather: ${e.message}"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
                        HourlyForecast(
                            hour = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(data.dt * 1000)),
                            temp = data.main.temp ?: 0.0, // Double
                            iconUrl = "https://openweathermap.org/img/wn/${w?.icon}@2x.png",
                            rain = (data.pop?.times(100))?.toInt()
                        )
                    }

                binding.rvHourly.adapter = HourlyForecastAdapter(hourlyItems)

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
                val msg = if (e.message?.contains("404") == true) {
                    "City not found. Please check the name."
                } else {
                    "Error fetching forecast: ${e.message}"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                Log.e("API_FORECAST", "Falha", e)
            }
        }

        // 🌍 Inicializa com localização atual
        checkLocationPermissionAndFetchWeather()
    }

    private fun submitCity() {
        val city = binding.etSearch.text?.toString()?.trim().orEmpty()
        when {
            city.isBlank() -> {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
            city.matches(Regex("^[0-9]+$")) -> {
                Toast.makeText(this, "City name cannot be only numbers", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding.progress.visibility = View.VISIBLE
                viewModel.loadWeather(city)
            }
        }
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

    // ✅ corrigido: verificação explícita + @SuppressLint
    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    viewModel.loadWeatherByCoordinates(lat, lon)
                } else {
                    Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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