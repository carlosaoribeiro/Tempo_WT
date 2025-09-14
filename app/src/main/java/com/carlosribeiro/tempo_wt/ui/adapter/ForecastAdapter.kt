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

    class ForecastAdapter(
        private val items: List<ForecastUiItem>
    ) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

        class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvDate: TextView = view.findViewById(R.id.tvDate)
            private val tvMinMax: TextView = view.findViewById(R.id.tvMinMax)
            private val tvDesc: TextView = view.findViewById(R.id.tvDesc)
            private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

            fun bind(item: ForecastUiItem) {
                val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                tvDate.text = sdf.format(Date(item.date * 1000))

                tvMinMax.text = "Min: ${item.minTemp}°C / Max: ${item.maxTemp}°C"
                tvDesc.text = item.description

                val url = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
                ivIcon.load(url)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_forecast, parent, false)
            return ForecastViewHolder(view)
        }

        override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size
    }
