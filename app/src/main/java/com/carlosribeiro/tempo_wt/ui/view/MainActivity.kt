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
import com.carlosribeiro.tempo_wt.ui.adapter.ForecastAdapter
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModel
import com.carlosribeiro.tempo_wt.ui.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private val forecastAdapter = ForecastAdapter(emptyList())
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnLocation.setOnClickListener {
            checkLocationPermissionAndFetchWeather()
        }

        binding.rvDaily.setHasFixedSize(true)
        binding.rvDaily.isNestedScrollingEnabled = false

        val api = RetrofitInstance.api
        val repository = WeatherRepository(api, "bb6ecc2665b7996900f60174b6731200")
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        binding.rvDaily.layoutManager = LinearLayoutManager(this)
        binding.rvDaily.adapter = forecastAdapter

        fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val city = binding.etSearch.text?.toString().orEmpty().ifBlank { "Osasco" }
            binding.progress.visibility = View.VISIBLE
            viewModel.loadWeather(city)
            true
        }

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
            checkLocationPermissionAndFetchWeather() // recarrega com nova unidade
        }

        viewModel.current.observe(this) { result ->
            binding.progress.visibility = View.GONE
            result.onSuccess { response ->
                Log.d("API_CURRENT", "Resposta: $response")

                binding.tvCity.text = response.name ?: "-"
                val main = response.main
                val unitSymbol = if (viewModel.getCurrentUnits() == "metric") "C" else "F"
                val windUnit = if (viewModel.getCurrentUnits() == "metric") "m/s" else "mph"
                binding.tvTemp.text = main?.temp?.let { "$it°$unitSymbol" } ?: "--°$unitSymbol"
                binding.tvFeelsLike.text = "Sensacao: ${main?.feels_like ?: "--"}°$unitSymbol"
                binding.tvHumidity.text = "Umidade: ${main?.humidity ?: "--"}%"
                val windSpeed = response.wind?.speed
                binding.tvWind.text = "Vento: ${windSpeed ?: "--"} $windUnit"

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

        viewModel.forecast.observe(this) { result ->
            result.onSuccess { forecast ->
                Log.d("API_FORECAST", "Qtd blocos: ${forecast.list?.size}")

                val uiItems = (forecast.list ?: emptyList()).take(7).mapNotNull { data ->
                    val dt = data.dt ?: return@mapNotNull null
                    val main = data.main
                    val min = main?.temp_min ?: main?.temp
                    val max = main?.temp_max ?: main?.temp
                    val w = data.weather?.firstOrNull()
                    val desc = w?.description ?: "-"
                    val icon = w?.icon ?: ""
                    if (min == null || max == null) return@mapNotNull null
                    ForecastUiItem(dt, min, max, desc, icon)
                }

                binding.rvDaily.adapter = ForecastAdapter(uiItems)
            }.onFailure { e ->
                Toast.makeText(this, "Erro forecast: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_FORECAST", "Falha", e)
            }
        }

        for (i in 0 until binding.chipGroupCities.childCount) {
            val chip = binding.chipGroupCities.getChildAt(i) as Chip
            chip.setOnClickListener {
                val city = chip.text.toString()
                binding.progress.visibility = View.VISIBLE
                viewModel.loadWeather(city)
            }
        }

        val chipDefault = binding.chipNewYork
        chipDefault.isChecked = true
        binding.progress.visibility = View.VISIBLE
        viewModel.loadWeather(chipDefault.text.toString())
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