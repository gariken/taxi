package taxi.eskar.eskartaxi.ui.address.typing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseAdapter
import taxi.eskar.eskartaxi.data.model.Address

class SuggestionsAdapter(
        private val listener: (Int, Address) -> Unit
) : BaseAdapter<Address, SuggestionsAdapter.SuggestionsViewHolder>() {

    // Adapter
    override fun onBindViewHolder(holder: SuggestionsViewHolder, position: Int) {
        holder.bind(this.getItemAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_suggestion, parent, false)

        return SuggestionsViewHolder(view, listener)
    }

    class SuggestionsViewHolder(
            view: View, private val listener: (Int, Address) -> Unit
    ) : BaseAdapter.BaseViewHolder<Address>(view) {

        private val titleView = view.findViewById<TextView>(R.id.tv_suggestion_title)
        private val subtitleView = view.findViewById<TextView>(R.id.tv_suggestion_subtitle)

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
            titleView.text = item.title
            subtitleView.text = item.subtitle
        }
    }
}