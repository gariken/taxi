package taxi.eskar.eskartaxi.presentation.debts

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Card

interface DebtView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class) fun showDebt(sum: Int)
    @StateStrategyType(AddToEndSingleStrategy::class) fun showNoDebt()
    @StateStrategyType(AddToEndSingleStrategy::class) fun showCards(cards: List<Card>)
}