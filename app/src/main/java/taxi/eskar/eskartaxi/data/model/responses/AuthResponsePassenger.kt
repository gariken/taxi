package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Passenger

data class AuthResponsePassenger(val data: Data) {
    data class Data(val authToken: String, val user: Passenger)
}