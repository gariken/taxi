package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.OrderOption

class OrderOptionsAdapter(
        private val onSelected: (selected: List<Int>) -> Unit
) : RecyclerView.Adapter<OrderOptionViewHolder>() {

    private val items = mutableListOf<OrderOption>()
    private val selected = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderOptionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_option, parent, false)
        return OrderOptionViewHolder({ pos ->
            val option = items[pos]
            if (selected.contains(option.id)) selected.remove(option.id) else selected.add(option.id)
            onSelected.invoke(selected)
        }, view)
    }

    override fun onBindViewHolder(holder: OrderOptionViewHolder, position: Int) {
        val option = items[position]
        holder.bind(option, selected.contains(option.id))
    }

    override fun getItemCount(): Int = items.size


    fun clear() {
        items.clear()
        selected.clear()
        this.notifyDataSetChanged()
    }

    fun replaceAll(newItems: List<OrderOption>, selectedNew: List<Int>) {
        items.clear()
        selected.clear()
        items.addAll(newItems)
        selected.addAll(selectedNew)
        this.notifyDataSetChanged()
    }

}