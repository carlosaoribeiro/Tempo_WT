package com.carlosribeiro.tempo_wt.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.carlosribeiro.tempo_wt.R
import com.carlosribeiro.tempo_wt.data.model.ForecastUiItem
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastAdapter(
    private val items: List<ForecastUiItem>
) : RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvHour: TextView = view.findViewById(R.id.tvHour)
        private val tvTemp: TextView = view.findViewById(R.id.tvTempHour)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIconHour)

        fun bind(item: ForecastUiItem) {
            // Mostrar apenas a hora (ex: 14h)
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvHour.text = sdf.format(Date(item.date * 1000))

            tvTemp.text = "${item.maxTemp}°C"  // usamos maxTemp para simplificar
            val url = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
            ivIcon.load(url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
