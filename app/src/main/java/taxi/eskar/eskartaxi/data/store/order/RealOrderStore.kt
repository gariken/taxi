package taxi.eskar.eskartaxi.data.store.order

import taxi.eskar.eskartaxi.data.model.Order
import java.util.concurrent.atomic.AtomicReference

class RealOrderStore : OrderStore {

    private val order = AtomicReference<Order>(Order.empty())

    override fun get(): Order = order.get()

    override fun update(updater: (order: Order) -> Order) {
        order.set(updater.invoke(order.get()))
    }

    override fun updateAndGet(updater: (order: Order) -> Order): Order {
        order.set(updater.invoke(order.get()))
        return order.get()
    }
}