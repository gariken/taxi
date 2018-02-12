package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger

sealed class PassengerResult {

    companion object {
        fun success(passenger: Passenger, order: Order? = null): PassengerResult = Success(passenger, order)
        fun failure(throwable: Throwable): PassengerResult = Failure(throwable)

        fun banned(): PassengerResult = Banned
        fun unauthorized(): PassengerResult = Unauthorized
        fun unknownStatusCode(): PassengerResult = UnknownStatusCode
    }

    data class Success(val passenger: Passenger, val order: Order? = null) : PassengerResult()
    data class Failure(val throwable: Throwable) : PassengerResult()

    object Banned : PassengerResult()
    object Unauthorized : PassengerResult()
    object UnknownStatusCode : PassengerResult()
}