package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Order

sealed class OrdersResult {

    companion object {
        fun success(orders: List<Order>): OrdersResult = Success(orders)
        fun fail(throwable: Throwable): OrdersResult = Fail(throwable)
        fun unknownStatusCode(statusCode: Int): OrdersResult = UnknownStatusCode(statusCode)
        fun unconfirmed(): OrdersResult = Unconfirmed()
    }

    data class Success(val orders: List<Order>) : OrdersResult()
    data class Fail(val throwable: Throwable) : OrdersResult()
    data class UnknownStatusCode(val statusCode: Int): OrdersResult()
    class Unconfirmed: OrdersResult()
}
