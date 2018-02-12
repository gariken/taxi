package taxi.eskar.eskartaxi.ui.cards.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Card

class CardsAdapter(
        private val listener: ItemClickListener
) : RecyclerView.Adapter<CardViewHolder>() {

    private val cards = mutableListOf<Card>()
    private val viewHolderListener = { pos: Int ->
        listener.onLongClick(pos, cards[pos])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view, viewHolderListener)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) =
            holder.bind(cards[position])

    fun setCards(cards: List<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
        this.notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onLongClick(position: Int, card: Card)
    }
}