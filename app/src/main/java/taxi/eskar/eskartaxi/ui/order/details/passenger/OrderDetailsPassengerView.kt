package taxi.eskar.eskartaxi.ui.order.details.passenger

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order

interface OrderDetailsPassengerView : BaseView {
    fun showDriver(driver: Driver)
    fun showDriverEmpty()
    fun showOrder(order: Order)
}