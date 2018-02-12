package taxi.eskar.eskartaxi.ui.favoritedaddresses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseAdapter
import taxi.eskar.eskartaxi.data.model.Address

class AddressAdapter(
        private val listener: (Int, Address) -> Unit
) : BaseAdapter<Address, AddressAdapter.AddressViewHolder>() {

    // Adapter
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(this.getItemAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fav_address, parent, false)

        return AddressViewHolder(view, listener)
    }

    class AddressViewHolder(
            view: View, private val listener: (Int, Address) -> Unit
    ) : BaseAdapter.BaseViewHolder<Address>(view) {

        private val textView = view as TextView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            item?.let {
                listener.invoke(this.adapterPosition, it)
            }
        }

        override fun bind(item: Address) {
            this.item = item
            textView.text = item.text ?: item.title ?: "Ошибка"
        }
    }
}