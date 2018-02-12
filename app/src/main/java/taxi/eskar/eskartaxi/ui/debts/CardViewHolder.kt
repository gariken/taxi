package taxi.eskar.eskartaxi.ui.debts

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Card

class CardViewHolder(
        private val listener: (pos: Int) -> Unit, itemView: View
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val text = itemView.findViewById<TextView>(R.id.text)

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(card: Card) {
        text.text = itemView.context.getString(R.string.mask_card, card.lastFourNumbers)
    }

    override fun onClick(v: View) {
        listener.invoke(this.adapterPosition)
    }
}