package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger

data class PassengerResponse(val data: Data) {
    data class Data(val user: Passenger, val openOrder: Order?)
}