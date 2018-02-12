package taxi.eskar.eskartaxi.data.actioncable.base

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.hosopy.actioncable.ActionCable
import com.hosopy.actioncable.Channel
import com.hosopy.actioncable.Consumer
import com.hosopy.actioncable.Subscription
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.ACOrderResult
import timber.log.Timber
import java.net.URI


/**
 * CancelOrdersChannel - передается driver_id, приходят заказы, когда их отменяет пользователь
 * CloseOrdersChannel - передается user_id, приходят заказы, когда их закрывает водитель
 * DriversCoordinatesChannel - передается userd_id или driver_id, водитель отсылает свои координаты в виде [lat, lon] и driver_id, приходят координаты подписанным пользователям
 * DrivingStartChannel - передается user_id, приходят заказы, когда водитель начал движение
 * OrdersChannel - передается driver_id, приходят заказы, когда пользователь создает заказ
 * TakeOrdersChannel -  передается user_id, приходят заказы, когда водитель принял заказ
 * TakeOrdersForDriversChannel - передается driver_id,  приходят заказы, когда другой водитель взять заказ
 * UsersCoordinatesChannel -  передается userd_id или driver_id, пользователь отсылает свои координаты в виде [lat, lon] и userd_id, приходят координаты подписанным водителям
 * WaitingStartChannel - передается user_id, приходят заказы, когда водитель начал ожидание
 */

class RealACRepository(
        domain: String, private val gson: Gson
) : ACRepository {

    companion object {
        const val CHANNEL_COORDINATES_DRIVERS = "DriversCoordinatesChannel"
        const val CHANNEL_COORDINATES_PASSENGER = "UsersCoordinatesChannel"
        const val CHANNEL_ORDERS = "OrdersChannel"
        const val CHANNEL_ORDERS_CANCEL = "CancelOrdersChannel"
        const val CHANNEL_ORDERS_CANCEL_FOR_DRIVERS = "CancelOrdersForDriversChannel"
        const val CHANNEL_ORDERS_CLOSE = "CloseOrdersChannel"
        const val CHANNEL_ORDERS_TAKE = "TakeOrdersChannel"
        const val CHANNEL_ORDERS_TAKE_FOR_DRIVERS = "TakeOrdersForDriversChannel"
        const val CHANNEL_START_DRIVING = "DrivingStartChannel"
        const val CHANNEL_START_WAITING = "WaitingStartChannel"
    }

    private val consumer: Consumer

    private var coordinatesDrivers: Subscription? = null
    private var coordinatesPassenger: Subscription? = null
    private var orders: Subscription? = null
    private var ordersCancel: Subscription? = null
    private var ordersCancelForDrivers: Subscription? = null
    private var ordersClose: Subscription? = null
    private var ordersTake: Subscription? = null
    private var ordersTakeForDrivers: Subscription? = null
    private var startDriving: Subscription? = null
    private var startWaiting: Subscription? = null

    private val coordinatesDriversRelay = PublishRelay.create<JsonElement>()
    private val coordinatesPassengerRelay = PublishRelay.create<JsonElement>()
    private val ordersRelay = PublishRelay.create<JsonElement>()
    private val ordersCancelRelay = PublishRelay.create<JsonElement>()
    private val ordersCancelForDriversRelay = PublishRelay.create<JsonElement>()
    private val ordersCloseRelay = PublishRelay.create<JsonElement>()
    private val ordersTakeRelay = PublishRelay.create<JsonElement>()
    private val ordersTakeForDriverRelay = PublishRelay.create<JsonElement>()
    private val startDrivingRelay = PublishRelay.create<JsonElement>()
    private val startWaitingRelay = PublishRelay.create<JsonElement>()


    init {
        val uri = URI("ws://$domain/api/v1/cable")
        val options = Consumer.Options()

        consumer = ActionCable.createConsumer(uri, options)
    }

    override fun connect() {
        consumer.connect()
    }

    override fun disconnect() {
        consumer.disconnect()
    }


    override fun ordersCancelSubscribe(driverId: Int): Observable<ACOrderResult> {
        ordersCancel = subscribe(CHANNEL_ORDERS_CANCEL, driverId, null, ordersCancelRelay)
        return ordersCancelRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersCancelUnsubscribe() {
        ordersCancel = unsubscribe(ordersCancel)
    }

    override fun ordersCancelForDriversSubscribe(driverId: Int): Observable<ACOrderResult> {
        ordersCancelForDrivers = subscribe(CHANNEL_ORDERS_CANCEL_FOR_DRIVERS, driverId, null, ordersCancelForDriversRelay)
        return ordersCancelForDriversRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersCancelForDriversUnsubscribe() {
        ordersCancelForDrivers = unsubscribe(ordersCancelForDrivers)
    }

    override fun ordersCloseSubscribe(userId: Int): Observable<ACOrderResult> {
        ordersClose = subscribe(CHANNEL_ORDERS_CLOSE, null, userId, ordersCloseRelay)
        return ordersCloseRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersCloseUnsubscribe() {
        ordersClose = unsubscribe(ordersClose)
    }

    override fun startDrivingSubscribe(userId: Int): Observable<ACOrderResult> {
        startDriving = subscribe(CHANNEL_START_DRIVING, null, userId, startDrivingRelay)
        return startDrivingRelay
                .map(this::jsonToACOrderResult)
    }

    override fun startDrivingUnsubscribe() {
        startDriving = unsubscribe(startDriving)
    }

    override fun ordersSubscribe(driverId: Int): Observable<ACOrderResult> {
        orders = subscribe(CHANNEL_ORDERS, driverId, null, ordersRelay)
        return ordersRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersUnsubscribe() {
        orders = unsubscribe(orders)
    }

    override fun ordersTakeSubscribe(userId: Int): Observable<ACOrderResult> {
        ordersTake = subscribe(CHANNEL_ORDERS_TAKE, null, userId, ordersTakeRelay)
        return ordersTakeRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersTakeUnsubscribe() {
        ordersTake = unsubscribe(ordersTake)
    }


    override fun ordersTakeForDriversSubscribe(driverId: Int): Observable<ACOrderResult> {
        ordersTakeForDrivers = subscribe(CHANNEL_ORDERS_TAKE_FOR_DRIVERS, driverId,
                null, ordersTakeForDriverRelay)
        return ordersTakeForDriverRelay
                .map(this::jsonToACOrderResult)
    }

    override fun ordersTakeForDriversUnsubscribe() {
        ordersTakeForDrivers = unsubscribe(ordersTakeForDrivers)
    }

    override fun startWaitingSubscribe(userId: Int): Observable<ACOrderResult> {
        startWaiting = subscribe(CHANNEL_START_WAITING, null, userId, startWaitingRelay)
        return startWaitingRelay
                .map(this::jsonToACOrderResult)
    }

    override fun startWaitingUnsubscribe() {
        startWaiting = unsubscribe(startWaiting)
    }

    override fun coordinatesDriversSubscribeDriver(driverId: Int): Observable<ACOrderResult> {
        coordinatesDrivers = subscribe(CHANNEL_COORDINATES_DRIVERS, driverId, null, coordinatesDriversRelay)
        return coordinatesDriversRelay.map(this::jsonToACOrderResult)
    }

    override fun coordinatesDriversSubscribePassenger(userId: Int): Observable<ACOrderResult> {
        coordinatesDrivers = subscribe(CHANNEL_COORDINATES_DRIVERS, null, userId, coordinatesDriversRelay)
        return coordinatesDriversRelay.map(this::jsonToACOrderResult)
    }

    override fun coordinatesDriversUnsubscribe() {
        coordinatesDrivers = unsubscribe(coordinatesDrivers)
    }

    override fun coordinatesPassengerSubscribe(driverId: Int): Observable<ACOrderResult> {
        coordinatesPassenger = subscribe(CHANNEL_COORDINATES_PASSENGER, driverId, null, coordinatesPassengerRelay)
        return coordinatesPassengerRelay.map(this::jsonToACOrderResult)
    }

    override fun coordinatesPassengerUnsubscribe() {
        coordinatesPassenger = unsubscribe(coordinatesPassenger)
    }

    override fun sendDriverLatLon(driverId: Int, latLon: LatLon) {
        val obj = JsonObject().apply {
            addProperty("driver_id", driverId)
            add("coordinates", JsonArray().apply {
                add(latLon.lat)
                add(latLon.lon)
            })
        }
        coordinatesDrivers?.perform("receive", obj)
    }


    // =============================================================================================
    //   Private
    // =============================================================================================

    private fun subscribe(channelName: String, driverId: Int?, userId: Int?,
                          relay: Relay<JsonElement>): Subscription {
        val channel = Channel(channelName).apply {
            driverId?.let {
                addParam("data", JsonObject().apply { addProperty("driver_id", driverId) })
            }
            userId?.let {
                addParam("data", JsonObject().apply { addProperty("user_id", userId) })
            }
        }

        return consumer.subscriptions.create(channel).apply {
            onConnected {
                Timber.v("Connected to $channelName")
            }
            onRejected {
                Timber.v("Rejected")
            }
            onReceived {
                Timber.v("Received on $channelName ${it.toString()}")
                relay.accept(it)
            }
            onDisconnected {
                Timber.v("Disconnected")
            }
            onFailed { e ->
                Timber.e(e)
            }
        }
    }

    private fun unsubscribe(subscription: Subscription?): Subscription? {
        subscription?.let {
            consumer.subscriptions.remove(it)
        }
        return null
    }

    private fun jsonToACOrderResult(jsonElement: JsonElement): ACOrderResult =
            gson.fromJson(jsonElement, ACOrderResult::class.java)

}