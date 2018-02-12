package taxi.eskar.eskartaxi.data.bus.events

import taxi.eskar.eskartaxi.data.model.Order

data class RepeatOrderRequest(val order: Order) : Event()