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
import kotlin.math.roundToInt   // <-- IMPORT AQUI

class DailyForecastAdapter(
    private val items: List<ForecastUiItem>
) : RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDay: TextView = view.findViewById(R.id.tvDay)
        private val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        private val tvRain: TextView = view.findViewById(R.id.tvRain)
        private val tvTempMax: TextView = view.findViewById(R.id.tvTempMax)
        private val tvTempMin: TextView = view.findViewById(R.id.tvTempMin)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

        fun bind(item: ForecastUiItem) {
            // Dia da semana (ex: Mon, Tue...)
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            tvDay.text = sdf.format(Date(item.date * 1000))

            tvDesc.text = item.description

            // % de chuva – se não vier no modelo, você pode deixar "--%" em vez de random
            tvRain.text = "${item.rain ?: 0}%"

            // arredonda valores
            tvTempMax.text = "${item.maxTemp.roundToInt()}°"
            tvTempMin.text = "${item.minTemp.roundToInt()}°"

            val url = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
            ivIcon.load(url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
