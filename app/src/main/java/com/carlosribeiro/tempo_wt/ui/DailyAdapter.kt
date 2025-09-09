package com.carlosribeiro.tempo_wt.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.carlosribeiro.tempo_wt.R

class DailyAdapter(private val items: List<DailyForecast>) :
    RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    inner class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = items[position]
        holder.tvDay.text = item.day
        holder.tvTemp.text = "Min: %.0f° / Max: %.0f°".format(item.min, item.max)
        holder.ivIcon.load("https://openweathermap.org/img/wn/${item.icon}@2x.png") {
            crossfade(true)
            placeholder(R.drawable.ic_weather_placeholder)
            error(R.drawable.ic_weather_error)
        }
    }

    override fun getItemCount(): Int = items.size
}
