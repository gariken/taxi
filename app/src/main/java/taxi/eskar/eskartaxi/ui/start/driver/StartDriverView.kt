package taxi.eskar.eskartaxi.ui.start.driver

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order

interface StartDriverView : BaseView {

    @StateStrategyType(SingleStateStrategy::class)
    fun showOrders(orders: List<Order>)

    @StateStrategyType(SingleStateStrategy::class)
    fun showOrdersEmpty(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showUnconfirmedError(show: Boolean)
}