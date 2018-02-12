package taxi.eskar.eskartaxi.data.managers

interface PushManager {
    fun subscribeToNewOrders(tariffId: Int)
    fun unsubscribeFromNewOrders(tariffId: Int)
    fun subscribeToOrderProgress(orderId: Int)
    fun unsubscribeFromOrderProgress(orderId: Int)
}