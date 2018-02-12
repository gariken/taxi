package taxi.eskar.eskartaxi.ui.order.setup.passenger

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Tariff

class TariffViewHolder(
        private val callback: (pos: Int) -> Unit, view: View
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    // Current tariff
    private var tariff: Tariff? = null

    // Views
    private val cardView = view as CardView
    private val imageView = view.findViewById<ImageView>(R.id.iv_tariff_icon)
    private val titleView = view.findViewById<TextView>(R.id.tv_tariff_title)
    private val priceView = view.findViewById<TextView>(R.id.tv_tariff_price)

    // Colors
    private val cardColorActive: Int =
            ContextCompat.getColor(itemView.context, R.color.white)
    private val cardColorInactive: Int =
            ContextCompat.getColor(itemView.context, R.color.light_gray)
    private val textColorActive =
            ContextCompat.getColor(itemView.context, R.color.text_normal)
    private val textColorInactive =
            ContextCompat.getColor(itemView.context, R.color.text_normal_disabled)

    init {
        view.setOnClickListener { callback.invoke(this.adapterPosition) }
    }

    fun bind(tariff: Tariff, selected: Boolean) {
        this.tariff = tariff

        cardView.cardElevation = if (selected) 4f else 0f

        val drawable = ContextCompat.getDrawable(
                itemView.context, Tariff.getDrawableResIdForId(tariff.id, selected))
        imageView.setImageDrawable(drawable)

        val textColor = if (selected) textColorActive else textColorInactive
        titleView.apply {
            text = tariff.title
            setTextColor(textColor)
        }
        priceView.apply {
            text = if (tariff.price == null)
                itemView.context.getString(R.string.mask_price_empty, "-")
            else
                itemView.context.getString(R.string.mask_price, tariff.price)
            setTextColor(textColor)
        }
    }

    override fun onClick(view: View) {
        tariff?.let { callback.invoke(this.adapterPosition) }
    }
}