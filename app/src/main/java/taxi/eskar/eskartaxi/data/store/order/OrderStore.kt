package taxi.eskar.eskartaxi.data.store.order

import taxi.eskar.eskartaxi.data.model.Order

interface OrderStore {
    fun get(): Order
    fun update(updater: (order: Order) -> Order)
    fun updateAndGet(updater: (order: Order) -> Order): Order
}