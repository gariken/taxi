package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order

data class DriverResponse(val data: Data) {
    data class Data(val driver: Driver, val openOrder: Order)
}