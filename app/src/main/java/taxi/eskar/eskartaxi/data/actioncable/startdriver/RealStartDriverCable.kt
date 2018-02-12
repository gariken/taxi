package taxi.eskar.eskartaxi.data.actioncable.startdriver

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hosopy.actioncable.ActionCable
import com.hosopy.actioncable.Channel
import com.hosopy.actioncable.Consumer
import com.hosopy.actioncable.Subscription
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.functions.Function3
import taxi.eskar.eskartaxi.data.actioncable.Channels
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.results.CableResult
import timber.log.Timber
import java.net.URI

class RealStartDriverCable(
        private val domain: String, private val gson: Gson
) : StartDriverCable {

    private val ordersRelay = PublishRelay.create<CableResult>()
    private val ordersCancelRelay = PublishRelay.create<CableResult>()
    private val ordersTakeRelay = PublishRelay.create<CableResult>()


    private val cableResultToFail = Function3<CableResult.Fail, CableResult.Fail, CableResult.Fail, CableResult> { _, _, _ ->
        CableResult.fail(RuntimeException())
    }

    private val failObservable = Observable
            .combineLatest(
                    ordersRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    ordersTakeRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    ordersCancelRelay.ofType<CableResult.Fail>(CableResult.Fail::class.java),
                    cableResultToFail)

    private lateinit var consumer: Consumer

    override fun connect(driver: Driver) {

        val uri = URI("ws://$domain/api/v1/cable")
        val options = Consumer.Options()

        consumer = ActionCable.createConsumer(uri, options)

        this.subscribe(Channels.ORDERS, driver.id, null, ordersRelay)
        this.subscribe(Channels.ORDERS_CANCEL_FOR_DRIVERS, driver.id, null, ordersCancelRelay)
        this.subscribe(Channels.ORDERS_TAKE_FOR_DRIVERS, driver.id, null, ordersTakeRelay)

        consumer.connect()
    }

    override fun disconnect() {
        consumer.disconnect()
    }

    override fun failsObservable(): Observable<CableResult> {
        return failObservable
    }

    override fun ordersObservable(): Observable<CableResult> {
        return ordersRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun ordersCancelForDriversObservable(): Observable<CableResult> {
        return ordersCancelRelay.map(this::cabelResultToCabelResult).hide()
    }

    override fun ordersTakeForDriversObservable(): Observable<CableResult> {
        return ordersTakeRelay.map(this::cabelResultToCabelResult).hide()
    }


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
                    Timber.v("Received on $channelName: $it")
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
                gson.fromJson(result.jsonElement, CableResult.Success::class.java) as CableResult
            }
            else -> result
        }
    }
}