package taxi.eskar.eskartaxi.data.managers

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class FirebasePushManager : PushManager {

    companion object {
        private const val TOPIC_NEW_ORDERS = "orders_drivers_%d"
        private const val TOPIC_ORDER_PROGRESS = "orders_users_%d"
    }

    override fun subscribeToNewOrders(tariffId: Int) {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEW_ORDERS.format(tariffId))
    }

    override fun unsubscribeFromNewOrders(tariffId: Int) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_NEW_ORDERS.format(tariffId))
    }

    override fun subscribeToOrderProgress(orderId: Int) {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ORDER_PROGRESS.format(orderId))
    }

    override fun unsubscribeFromOrderProgress(orderId: Int) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ORDER_PROGRESS.format(orderId))
    }
}