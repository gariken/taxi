package taxi.eskar.eskartaxi.data.actioncable.base

import io.reactivex.Observable
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.ACOrderResult

interface ACRepository {
    fun connect()
    fun disconnect()

    fun ordersSubscribe(driverId: Int): Observable<ACOrderResult>
    fun ordersUnsubscribe()

    fun ordersCancelSubscribe(driverId: Int): Observable<ACOrderResult>
    fun ordersCancelUnsubscribe()

    fun ordersCancelForDriversSubscribe(driverId: Int): Observable<ACOrderResult>
    fun ordersCancelForDriversUnsubscribe()

    fun ordersCloseSubscribe(userId: Int): Observable<ACOrderResult>
    fun ordersCloseUnsubscribe()

    fun ordersTakeSubscribe(userId: Int): Observable<ACOrderResult>
    fun ordersTakeUnsubscribe()

    fun ordersTakeForDriversSubscribe(driverId: Int): Observable<ACOrderResult>
    fun ordersTakeForDriversUnsubscribe()

    fun startDrivingSubscribe(userId: Int): Observable<ACOrderResult>
    fun startDrivingUnsubscribe()

    fun startWaitingSubscribe(userId: Int): Observable<ACOrderResult>
    fun startWaitingUnsubscribe()

    fun coordinatesDriversSubscribeDriver(driverId: Int): Observable<ACOrderResult>
    fun coordinatesDriversSubscribePassenger(userId: Int): Observable<ACOrderResult>
    fun coordinatesDriversUnsubscribe()

    fun coordinatesPassengerSubscribe(driverId: Int): Observable<ACOrderResult>
    fun coordinatesPassengerUnsubscribe()

    fun sendDriverLatLon(driverId: Int, latLon: LatLon)

}