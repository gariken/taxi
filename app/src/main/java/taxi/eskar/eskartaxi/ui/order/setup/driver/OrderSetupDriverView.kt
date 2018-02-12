package taxi.eskar.eskartaxi.ui.order.setup.driver

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger

interface OrderSetupDriverView : BaseView {
    fun showOrder(order: Order)

    fun showPassenger(passenger: Passenger)
    fun showPassengerLoading(show: Boolean)
    fun showPassengerEmpty()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showComment(order: Order)
}