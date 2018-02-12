package taxi.eskar.eskartaxi.ui.cards.list

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlinx.android.synthetic.main.layout_appbar.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.presentation.cards.CardsPresenter
import taxi.eskar.eskartaxi.presentation.cards.CardsView
import taxi.eskar.eskartaxi.util.show
import toothpick.Toothpick

class CardsFragment : BaseFragment(), CardsView {

    companion object {
        fun newInstance() = CardsFragment()
    }


    // region moxy

    @InjectPresenter lateinit var presenter: CardsPresenter

    @ProvidePresenter fun providePresenter(): CardsPresenter =
            Toothpick.openScopes(Scopes.APP).getInstance(CardsPresenter::class.java)

    // endregion


    private val adapter = CardsAdapter(object : CardsAdapter.ItemClickListener {
        override fun onLongClick(position: Int, card: Card) {
            presenter.onCardLongClicked(position, card)
        }
    })


    // region android

    override val layoutResId: Int = R.layout.fragment_cards

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setTitle(R.string.title_cards)
            setNavigationIcon(R.drawable.ic_arrow_back_orange)
            setNavigationOnClickListener { presenter.onBackClicked() }
        }
        rvCards.layoutManager = LinearLayoutManager(this.context)
        rvCards.adapter = adapter

        btnAddCard.setOnClickListener { presenter.onAddButtonClicked() }
    }

    // endregion


    // region view

    override fun showCards(cards: List<Card>) {
        this.adapter.setCards(cards)
    }

    override fun showNoCards(show: Boolean) {
        tvEmptyCards.show(show)
    }

    override fun showDeleteCardDialog(card: Card) {
        val context = context ?: return
        AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.message_card_unbinding)
                .setPositiveButton(R.string.pos_btn_card_unbinding, { _, _ -> presenter.onUnbindCard(card) })
                .setNegativeButton(R.string.neg_btn_card_unbinding, { _, _ ->  })
                .show()
    }

    // region

}