package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.OrderOption

class OrderOptionViewHolder(
        private val onClick: (pos: Int) -> Unit, itemView: View
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val clicksVeiw = itemView.findViewById<View>(R.id.view_clicks)
    private val selectedView = itemView.findViewById<CheckBox>(R.id.cb_option)
    private val titleView = itemView.findViewById<TextView>(R.id.tv_option)
    private val priceView = itemView.findViewById<TextView>(R.id.tv_price)

    init {
        clicksVeiw.setOnClickListener(this)
    }

    fun bind(option: OrderOption, selected: Boolean) {
        selectedView.isChecked = selected
        titleView.text = option.description
        priceView.text = if (option.price == null) "" else
            itemView.context.getString(R.string.mask_price_add, option.price.toInt())
    }

    override fun onClick(view: View) {
        selectedView.isChecked = selectedView.isChecked.not()
        this.onClick.invoke(this.adapterPosition)
    }
}