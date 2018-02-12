package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order

sealed class DriverResult {

    companion object {
        fun success(driver: Driver, order: Order? = null): DriverResult = Success(driver, order)
        fun failure(throwable: Throwable): DriverResult = Failure(throwable)

        fun banned(): DriverResult = Banned
        fun unauthorized(): DriverResult = Unauthorized
        fun unknownStatusCode(): DriverResult = UnknownStatusCode
    }

    data class Success(val driver: Driver, val order: Order?) : DriverResult()
    data class Failure(val throwable: Throwable) : DriverResult()

    object Banned: DriverResult()
    object Unauthorized: DriverResult()
    object UnknownStatusCode: DriverResult()
}