package taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hosopy.actioncable.ActionCable
import com.hosopy.actioncable.Channel
import com.hosopy.actioncable.Consumer
import com.hosopy.actioncable.Subscription
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.functions.Function5
import taxi.eskar.eskartaxi.data.actioncable.Channels
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.CableResult
import timber.log.Timber
import java.net.URI

class RealOrderProgressPassengerCable(
        private val domain: String, private val gson: Gson
) : OrderProgressPassengerCable {

    private val coordinatesDriverRelay = PublishRelay.create<CableResult>()
    private val ordersTakeRelay = PublishRelay.create<CableResult>()
    private val startWaitingRelay = PublishRelay.create<CableResult>()
    private val startDrivingRelay = PublishRelay.create<CableResult>()
    private val ordersCloseRelay = PublishRelay.create<CableResult>()

    private val cableResultToFail = Function5<CableResult.Fail, CableResult.Fail, CableResult.Fail, CableResult.Fail, CableResult.Fail, CableResult> { _, _, _, _, _ ->
        CableResult.fail(RuntimeException())
    }

    private val failObservable = Observable
            .combineLatest(
                    coordinatesDriverRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    ordersTakeRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    startWaitingRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    startDrivingRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    ordersCloseRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    cableResultToFail)

    private lateinit var consumer: Consumer


    override fun connect(order: Order) {
        if (order.userId == null) {
            return
        }

        val uri = URI("ws://$domain/api/v1/cable")
        val options = Consumer.Options()

        consumer = ActionCable.createConsumer(uri, options)

        val userId: Int = order.userId

        this.subscribe(Channels.COORDINATES_DRIVERS, null, userId, coordinatesDriverRelay)
        this.subscribe(Channels.ORDERS_TAKE, null, userId, ordersTakeRelay)
        this.subscribe(Channels.START_WAITING, null, userId, startWaitingRelay)
        this.subscribe(Channels.START_DRIVING, null, userId, startDrivingRelay)
        this.subscribe(Channels.ORDERS_CLOSE, null, userId, ordersCloseRelay)

        consumer.connect()
    }

    override fun disconnect() {
        consumer.disconnect()
    }

    override fun failsObservable(): Observable<CableResult> {
        return failObservable.hide()
    }

    override fun coordinatesObservable(): Observable<CableResult> {
        return coordinatesDriverRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun ordersTakeObservable(): Observable<CableResult> {
        return ordersTakeRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun startWaitingObservable(): Observable<CableResult> {
        return startWaitingRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun startDrivingObservable(): Observable<CableResult> {
        return startDrivingRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun ordersCloseObservable(): Observable<CableResult> {
        return ordersCloseRelay.map(this::cabelResultToCabelResult).hide()
    }


    // =============================================================================================
    //   Private
    // =============================================================================================

    private fun subscribe(channelName: String, driverId: Int?, userId: Int?,
                          relay: Relay<CableResult>): Subscription {
        val channel = Channel(channelName).apply {
            driverId?.let {
                addParam("data", JsonObject().apply { addProperty("driver_id", driverId) })
            }
            userId?.let {
                addParam("data", JsonObject().apply { addProperty("user_id", userId) })
            }
        }

        return consumer.subscriptions.create(channel)
                .onConnected {
                    Timber.v("Connected to $channelName")
                }
                .onRejected {
                    Timber.v("Rejected on $channelName")
                }
                .onReceived {
                    Timber.v("Received on $channelName: ${it.toString()}")
                    relay.accept(CableResult.receivement(it))
                }
                .onDisconnected {
                    Timber.v("Disconnected from $channelName")
                }
                .onFailed { e ->
                    Timber.e("Failed on $channelName: ${e.message}")
                    relay.accept(CableResult.fail(e))
                }
    }

    private fun cabelResultToCabelResult(result: CableResult): CableResult {
        return when (result) {
            is CableResult.Receivement -> {
                Timber.i(result.jsonElement.toString())
                gson.fromJson(result.jsonElement, CableResult.Success::class.java) as CableResult
            }
            else -> result
        }
    }

}