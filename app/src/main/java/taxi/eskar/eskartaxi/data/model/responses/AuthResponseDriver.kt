package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Driver

data class AuthResponseDriver(val data: Data) {
    data class Data(val authToken: String, val driver: Driver)
}