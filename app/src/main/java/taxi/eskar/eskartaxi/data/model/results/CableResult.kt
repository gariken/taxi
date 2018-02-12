package taxi.eskar.eskartaxi.data.model.results

import com.google.gson.JsonElement
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order

sealed class CableResult {

    companion object {
        fun connection(): CableResult = Connection()
        fun receivement(jsonElement: JsonElement): CableResult = Receivement(jsonElement)
        fun rejection(): CableResult = Rejection()
        fun disconnection(): CableResult = Disconnection()
        fun fail(throwable: Throwable): CableResult = Fail(throwable)
    }

    class Connection : CableResult()
    class Rejection : CableResult()
    class Disconnection : CableResult()

    data class Receivement(val jsonElement: JsonElement) : CableResult()
    data class Success(val order: Order?, val driver: Driver?, val driversCoordinates: List<Double>?) : CableResult()
    data class Fail(val throwable: Throwable) : CableResult()
}
