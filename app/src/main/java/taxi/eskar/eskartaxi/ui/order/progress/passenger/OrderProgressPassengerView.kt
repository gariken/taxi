package taxi.eskar.eskartaxi.ui.order.progress.passenger

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Driver

interface OrderProgressPassengerView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriverSearch()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriverFound()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriverInfo(driver: Driver)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriverArived()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOrderInProgress(showRating: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOrderEnded(showRating: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCancelOrderAlert()
}