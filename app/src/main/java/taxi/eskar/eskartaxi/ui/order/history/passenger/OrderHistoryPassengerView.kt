package taxi.eskar.eskartaxi.ui.order.history.passenger

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order

interface OrderHistoryPassengerView : BaseView {
    fun showOrders(orders: List<Order>)
    fun showOrdersEmpty()
    fun showOrdersError()
}