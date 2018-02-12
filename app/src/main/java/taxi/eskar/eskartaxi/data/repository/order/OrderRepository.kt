package taxi.eskar.eskartaxi.data.repository.order

import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.*

interface OrderRepository {

    fun getOrderOptions(): Single<OrderOptionsResult>

    fun getTariffs(): Single<TariffsResult>

    fun createOrder(order: Order): Single<OrderResult>

    fun getOrdersHistoryPassenger(): Single<OrdersResult>
    fun getOrdersHistoryDriver(): Single<OrdersResult>

    fun getAllNewOrdersDriver(): Single<OrdersResult>

    fun getPreliminaryOrder(order: Order): Single<PreliminaryResult>

    fun deleteOrder(order: Order): Single<OrderResult>

    fun takeOrder(order: Order, driver: Driver): Single<OrderResult>
    fun waitOrder(order: Order): Single<OrderResult>
    fun startOrder(order: Order): Single<OrderResult>
    fun closeOrder(order: Order, payed: Boolean? = null): Single<OrderResult>

    fun rateOrder(order: Order, rating: Double?, review: String?): Single<OrderResult>
}