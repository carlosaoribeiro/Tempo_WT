package com.carlosribeiro.tempo_wt.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.carlosribeiro.tempo_wt.R
import com.carlosribeiro.tempo_wt.ui.model.ForecastUiItem
import kotlin.math.roundToInt

class HourlyAdapter(private val items: List<ForecastUiItem>) :
    RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HourlyViewHolder {
        TODO("Not yet implemented")
    }

    inner class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHour: TextView = view.findViewById(R.id.tvHour)
        val ivIcon: ImageView = view.findViewById(R.id.ivIconHour)
        val tvTemp: TextView = view.findViewById(R.id.tvTempHour)
        val tvRain: TextView = view.findViewById(R.id.tvRainChance)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]

        // Hora formatada
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        holder.tvHour.text = sdf.format(java.util.Date(item.date * 1000))

        // Temperatura arredondada
        holder.tvTemp.text = "${item.maxTemp.roundToInt()}°C"

        // % chuva
        holder.tvRain.text = "${item.rain ?: 0}%"

        // Ícone
        holder.ivIcon.load("https://openweathermap.org/img/wn/${item.icon}@2x.png") {
            crossfade(true)
            placeholder(R.drawable.ic_weather_placeholder)
            error(R.drawable.ic_weather_error)
        }
    }

    override fun getItemCount() = items.size
}


