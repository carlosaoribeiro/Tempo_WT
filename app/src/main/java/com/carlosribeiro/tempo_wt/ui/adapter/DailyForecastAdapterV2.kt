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
import java.text.SimpleDateFormat
import java.util.*

class DailyForecastAdapterV2(
    private val items: List<ForecastUiItem>
) : RecyclerView.Adapter<DailyForecastAdapterV2.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvRain: TextView = view.findViewById(R.id.tvRain)
        val tvTempMax: TextView = view.findViewById(R.id.tvTempMax)
        val tvTempMin: TextView = view.findViewById(R.id.tvTempMin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast_v2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Dia da semana
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        holder.tvDay.text = sdf.format(Date(item.date * 1000L))

        // Ícone do clima
        if (item.icon.isNotEmpty()) {
            val url = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
            holder.ivIcon.load(url)
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_weather_placeholder)
        }

        // Descrição
        holder.tvDesc.text = item.description

        // 🌧️ Probabilidade de chuva
        holder.tvRain.text = item.rain?.let { "${it.toInt()}%" } ?: "--"

        // 🌡️ Temperaturas
        holder.tvTempMax.text = "${item.maxTemp.toInt()}°"
        holder.tvTempMin.text = "${item.minTemp.toInt()}°"
    }

    override fun getItemCount(): Int = items.size
}
