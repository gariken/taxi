package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Order

sealed class OrderResult {

    companion object {
        fun success(order: Order): OrderResult = Success(order)
        fun hasDebt(): OrderResult = HasDebt
        fun lowBalance(): OrderResult = LowBalance
        fun noMoney(message: String): OrderResult = NoMoney(message)
        fun unknownStatusCode(statusCode: Int): OrderResult = UnknownStatusCode(statusCode)
        fun fail(throwable: Throwable): OrderResult = Fail(throwable)
    }

    data class Success(val order: Order) : OrderResult()
    data class Fail(val throwable: Throwable) : OrderResult()
    data class UnknownStatusCode(val statusCode: Int) : OrderResult()
    data class NoMoney(val message: String): OrderResult()
    object LowBalance: OrderResult()
    object HasDebt: OrderResult()
}