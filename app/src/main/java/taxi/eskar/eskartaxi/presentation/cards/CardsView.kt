package taxi.eskar.eskartaxi.presentation.cards

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Card

interface CardsView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCards(cards: List<Card>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteCardDialog(card: Card)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showNoCards(show: Boolean)
}