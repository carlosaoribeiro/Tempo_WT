package com.carlosribeiro.tempo_wt.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.carlosribeiro.tempo_wt.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HourlyForecastAdapter(private val items: List<HourlyForecast>) :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHour: TextView = view.findViewById(R.id.tvHour)
        val ivIcon: ImageView = view.findViewById(R.id.ivIconHour)
        val tvTemp: TextView = view.findViewById(R.id.tvTempHour)
        val tvRainChance: TextView = view.findViewById(R.id.tvRainChance) // ✅ este é o id do XML
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]

        // Hora (já vem formatada de MainActivity)
        holder.tvHour.text = item.hour

        // Temperatura
        holder.tvTemp.text = "${item.temp.roundToInt()}°C"

        // % de chuva
        holder.tvRainChance.text = "${item.rain ?: 0}%"

        // Ícone
        holder.ivIcon.load(item.iconUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_weather_placeholder)
            error(R.drawable.ic_weather_error)
        }
    }


    override fun getItemCount(): Int = items.size
}
