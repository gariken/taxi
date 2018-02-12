package taxi.eskar.eskartaxi.ui.start.driver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseAdapter
import taxi.eskar.eskartaxi.data.model.Order

class OrderAdapter(
        private val listener: (Int, Order) -> Unit
) : BaseAdapter<Order, OrderAdapter.OrderViewHolder>() {

    // Adapter
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(this.getItemAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)

        return OrderViewHolder(view, listener)
    }

    class OrderViewHolder(
            view: View, private val listener: (Int, Order) -> Unit
    ) : BaseAdapter.BaseViewHolder<Order>(view) {

        private val fromView = view.findViewById<TextView>(R.id.tv_order_from)
        private val toView = view.findViewById<TextView>(R.id.tv_order_to)
        private val priceView = view.findViewById<TextView>(R.id.tv_order_price)
        private val distanceView = view.findViewById<TextView>(R.id.tv_order_distance)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            item?.let {
                listener.invoke(this.adapterPosition, it)
            }
        }

        override fun bind(item: Order) {
            this.item = item
            fromView.text = item.addressFrom ?: "Адрес недоступен"
            toView.text = item.addressTo ?: "Адрес недоступен"
            priceView.text = this.itemView.context.getString(R.string.mask_price, item.amount ?: 0)
            distanceView.text = this.itemView.context.getString(R.string.mask_distance, item.distance)
        }
    }
}