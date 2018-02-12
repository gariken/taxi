package taxi.eskar.eskartaxi.ui.order.setup.passenger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Tariff

class TariffsAdapter(
        private val callback: (pos: Int, tariff: Tariff) -> Unit
) : RecyclerView.Adapter<TariffViewHolder>() {

    private val items = mutableListOf<Tariff>()

    private var selectedOld = 0
    private var selectedNew = 0


    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TariffViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tariff, parent, false)
        return TariffViewHolder(this::onItemClicked, view)
    }

    override fun onBindViewHolder(holder: TariffViewHolder, position: Int) {
        holder.bind(items[position], position == selectedNew)
    }

    fun replace(tariffs: List<Tariff>, tariffN: Int) {
        selectedOld = selectedNew
        selectedNew = tariffN
        items.clear()
        items.addAll(tariffs)
        this.notifyDataSetChanged()
    }

    fun getSelected(): Int = selectedNew

    fun setSelected(pos: Int) {
        this.onItemClicked(pos)
    }

    private fun onItemClicked(pos: Int) {
        selectedOld = selectedNew
        selectedNew = pos
        this.notifyItemChanged(selectedOld)
        this.notifyItemChanged(selectedNew)
        callback.invoke(pos, items[pos])
    }

}