package taxi.eskar.eskartaxi.ui.order.progress.driver

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger

interface OrderProgressDriverView : BaseView {
    fun showOrder(order: Order)
    fun showOrderTaked()
    fun showOrderWaiting()
    fun showOrderStarted()
    fun showOrderClosed()
    fun showPassenger(passenger: Passenger)
    fun showPassengerEmpty()
    fun showPassengerLoading(show: Boolean)
}
