package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Order

data class OrderResponse(val data: Data, val code: Int) {
    data class Data(val code: Int, val order: Order, val message: String)
}