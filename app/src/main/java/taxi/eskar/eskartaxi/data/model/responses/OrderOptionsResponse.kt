package taxi.eskar.eskartaxi.data.model.responses

import java.util.*

data class OrderOptionsResponse(val data: Data) {
    data class Data(val orderOptions: List<OrderOption>) {
        data class OrderOption(val id: Int, val description: String,
                               val createdAt: Date, val updatedAt: Date,
                               val price: Double)
    }
}