package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.OrderOption

interface OrderCommentsView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOrder(order: Order)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOptionsEmpty()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOptions(options: List<OrderOption>, selectedOptions: List<Int>)
}
