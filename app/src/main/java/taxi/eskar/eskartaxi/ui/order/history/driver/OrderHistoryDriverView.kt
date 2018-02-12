package taxi.eskar.eskartaxi.ui.order.history.driver

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order

interface OrderHistoryDriverView : BaseView {
    fun showOrders(orders: List<Order>)
    fun showOrdersEmpty()
    fun showOrdersError()
}