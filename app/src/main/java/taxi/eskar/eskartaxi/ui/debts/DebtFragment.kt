package taxi.eskar.eskartaxi.ui.debts

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_debt.*
import kotlinx.android.synthetic.main.layout_appbar.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.presentation.debts.DebtPresenter
import taxi.eskar.eskartaxi.presentation.debts.DebtView
import taxi.eskar.eskartaxi.util.AlertDialogHelper
import toothpick.Toothpick

class DebtFragment : BaseFragment(), DebtView {

    companion object {
        fun newInstance() = DebtFragment()
    }


    @InjectPresenter lateinit var presenter: DebtPresenter

    @ProvidePresenter fun providePresenter(): DebtPresenter =
            Toothpick.openScopes(Scopes.APP).getInstance(DebtPresenter::class.java)


    private val adapter = CardsAdapter(object : CardsAdapter.ItemClickListener {
        override fun onClick(card: Card) {
            presenter.onCardSelected(card)
        }
    })


    override val layoutResId: Int = R.layout.fragment_debt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setTitle(R.string.title_debts)
            setNavigationIcon(R.drawable.ic_arrow_back_orange)
            setNavigationOnClickListener { presenter.onBackClicked() }
        }

        rvCards.layoutManager = LinearLayoutManager(context)
        rvCards.adapter = adapter
    }

    override fun showDebt(sum: Int) {
        tv_debt.text = this.getString(R.string.mask_price, sum)
    }

    override fun showNoDebt() {
        tv_debt_empty.visibility = View.VISIBLE
    }

    override fun showCards(cards: List<Card>) {
        adapter.setCards(cards)
        layoutCards.visibility = View.VISIBLE
    }

    override fun showSystemMessage(message: String) {
        AlertDialogHelper.showSystemMessage(context, message)
    }

}