package taxi.eskar.eskartaxi.ui.cards.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Card

class CardViewHolder(
        itemView: View, private val listener: (position: Int) -> Unit
) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

    private val text = itemView.findViewById<TextView>(R.id.text)

    init {
        itemView.setOnLongClickListener(this)
    }

    fun bind(card: Card) {
        text.text = itemView.context.getString(R.string.mask_card, card.lastFourNumbers)
    }

    override fun onLongClick(v: View): Boolean {
        listener.invoke(adapterPosition)
        return true
    }

}