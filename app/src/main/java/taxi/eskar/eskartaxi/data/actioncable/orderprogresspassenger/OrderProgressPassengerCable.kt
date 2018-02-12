package taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger

import io.reactivex.Observable
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.CableResult

interface OrderProgressPassengerCable {

    fun connect(order: Order)
    fun disconnect()
    fun failsObservable(): Observable<CableResult>
    fun coordinatesObservable(): Observable<CableResult>
    fun ordersTakeObservable(): Observable<CableResult>
    fun startWaitingObservable(): Observable<CableResult>
    fun startDrivingObservable(): Observable<CableResult>
    fun ordersCloseObservable(): Observable<CableResult>
}