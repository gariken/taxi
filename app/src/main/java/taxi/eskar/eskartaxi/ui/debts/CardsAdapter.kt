package taxi.eskar.eskartaxi.ui.debts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Card

class CardsAdapter(
        private val listener: ItemClickListener
) : RecyclerView.Adapter<CardViewHolder>() {

    private val cards = mutableListOf<Card>()
    private val lstnr = { pos: Int -> listener.onClick(cards[pos]) }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(lstnr, view)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    fun setCards(cards: List<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
        this.notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(card: Card)
    }
}