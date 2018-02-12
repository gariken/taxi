package taxi.eskar.eskartaxi.data.model

import java.io.Serializable
import java.util.*

data class Order(val id: Int, val userId: Int?, val driverId: Int?,
                 var addressFrom: String?, var addressTo: String?,
                 var latFrom: Double?, var lonFrom: Double?,
                 var latTo: Double?, var lonTo: Double?,
                 var distance: Double?, var rating: Double?, var review: String?,
                 var amount: Int?, var tariffId: Int?,
                 var comment: String?, var paymentMethod: String?,
                 val timeOfTaking: Date?, val startWaitingTime: Date?,
                 val timeOfStarting: Date?, val timeOfClosing: Date?,
                 val orderOptions: MutableList<OrderOption>,
                 val createdAt: Date, var cardId: Int?) : Serializable {

    companion object {

        private const val SEPARATOR_OPTIONS = "\n"

        fun empty() = Order(-1, -1, -1, null, null,
                null, null, null, null, .0, null, null,
                null, null, null, null, null,
                null, null, null, mutableListOf(), Date(), null)
    }

    fun canBePriced() = addressFrom != null && addressTo != null
            && lonFrom != null && lonTo != null
            && latFrom != null && latTo != null

    fun hasFromAddress() = addressFrom != null && latFrom != null && latFrom != null

    fun hasToAddress() = addressTo != null && latTo != null && latTo != null

    fun setAddressFrom(address: Address) {
        addressFrom = address.title
        latFrom = address.lat
        lonFrom = address.lon
    }

    fun setAddressTo(address: Address) {
        addressTo = address.title
        latTo = address.lat
        lonTo = address.lon
    }

    fun getCommentsAndOptions(): String {
        var result = comment ?: ""

        if (orderOptions.isNotEmpty()) {
            if (result.isNotBlank()) result = result.plus("\n")
            result = result.plus(orderOptions.joinToString(SEPARATOR_OPTIONS) {
                "- ${it.description}"
            })
        }

        return result
    }

}