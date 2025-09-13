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
import com.carlosribeiro.tempo_wt.data.model.ForecastUiItem
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
                Log.d("API_CURRENT", "Resposta: $response")

                binding.tvCity.text = response.name ?: "-"
                val main = response.main
                val unitSymbol = if (viewModel.getCurrentUnits() == "metric") "C" else "F"
                val windUnit = if (viewModel.getCurrentUnits() == "metric") "m/s" else "mph"

                binding.tvTemp.text = main?.temp?.let { "$it°$unitSymbol" } ?: "--°$unitSymbol"
                binding.tvFeelsLike.text = "Sensação: ${main?.feels_like ?: "--"}°$unitSymbol"
                binding.tvHumidity.text = "Umidade: ${main?.humidity ?: "--"}%"
                binding.tvWind.text = "Vento: ${response.wind?.speed ?: "--"} $windUnit"

                val w = response.weather?.firstOrNull()
                binding.tvDesc.text = w?.description ?: "-"
                val icon = w?.icon
                if (!icon.isNullOrBlank()) {
                    binding.ivIcon.load("https://openweathermap.org/img/wn/${icon}@4x.png")
                } else {
                    binding.ivIcon.setImageDrawable(null)
                }

                binding.tvUpdated.text = "Atualizado: ${now()}"
            }.onFailure { e ->
                Toast.makeText(this, "Erro (current): ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_CURRENT", "Falha", e)
            }
        }

        // 🔄 Observa previsão (horária e diária)
        viewModel.forecast.observe(this) { result ->
            result.onSuccess { forecast ->
                Log.d("API_FORECAST", "Qtd blocos: ${forecast.list?.size}")
                val forecastList = forecast.list ?: emptyList()

                // Próximas 12 horas
                val hourlyItems = forecastList
                    .take(12)
                    .mapNotNull { data ->
                        val dt = data.dt ?: return@mapNotNull null
                        val temp = data.main?.temp ?: return@mapNotNull null
                        val w = data.weather?.firstOrNull()
                        ForecastUiItem(dt, temp, temp, w?.description ?: "-", w?.icon ?: "")
                    }
                binding.rvHourly.adapter = HourlyForecastAdapter(hourlyItems)

                // Previsão diária (7 dias)
                val dailyItems = forecastList
                    .filterIndexed { index, _ -> index % 8 == 0 } // 1 ponto por dia
                    .take(7)
                    .mapNotNull { data ->
                        val dt = data.dt ?: return@mapNotNull null
                        val main = data.main
                        val min = main?.temp_min ?: main?.temp
                        val max = main?.temp_max ?: main?.temp
                        val w = data.weather?.firstOrNull()
                        ForecastUiItem(dt, min ?: 0.0, max ?: 0.0, w?.description ?: "-", w?.icon ?: "")
                    }
                binding.rvDaily.adapter = DailyForecastAdapter(dailyItems)

            }.onFailure { e ->
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
}
