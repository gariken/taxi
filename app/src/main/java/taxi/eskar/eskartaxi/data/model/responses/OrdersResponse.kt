package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Order

data class OrdersResponse(val data: Data) {
    data class Data(val orders: List<Order>)
}