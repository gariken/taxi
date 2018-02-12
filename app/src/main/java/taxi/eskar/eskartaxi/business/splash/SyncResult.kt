package taxi.eskar.eskartaxi.business.splash

import taxi.eskar.eskartaxi.data.model.Order

sealed class SyncResult {
    companion object {
        fun driver(driver: taxi.eskar.eskartaxi.data.model.Driver, order: Order? = null): SyncResult = Driver(driver, order)
        fun passenger(passenger: taxi.eskar.eskartaxi.data.model.Passenger, order: Order? = null): SyncResult = Passenger(passenger, order)
        fun error(throwable: Throwable): SyncResult = Error(throwable)

        fun banned(): SyncResult = Banned
        fun none(): SyncResult = None
        fun unauthorized(): SyncResult = Unauthorized
        fun unknownStatusCode(): SyncResult = UnknownStatusCode
    }

    data class Driver(val driver: taxi.eskar.eskartaxi.data.model.Driver, val order: Order?) : SyncResult()
    data class Passenger(val passenger: taxi.eskar.eskartaxi.data.model.Passenger, val order: Order?) : SyncResult()
    data class Error(val throwable: Throwable) : SyncResult()

    object Banned : SyncResult()
    object None : SyncResult()
    object Unauthorized : SyncResult()
    object UnknownStatusCode : SyncResult()
}