package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.OrderOption

sealed class OrderOptionsResult {
    companion object {
        fun success(options: List<OrderOption>): OrderOptionsResult = Success(options)
        fun fail(throwable: Throwable): OrderOptionsResult = Fail(throwable)
    }

    data class Success(val options: List<OrderOption>) : OrderOptionsResult()
    data class Fail(val throwable: Throwable) : OrderOptionsResult()
}