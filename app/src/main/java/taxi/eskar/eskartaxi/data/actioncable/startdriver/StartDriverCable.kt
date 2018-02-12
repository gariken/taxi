package taxi.eskar.eskartaxi.data.actioncable.startdriver

import io.reactivex.Observable
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.results.CableResult

interface StartDriverCable {
    fun connect(driver: Driver)
    fun disconnect()

    fun failsObservable(): Observable<CableResult>

    fun ordersObservable(): Observable<CableResult>
    fun ordersCancelForDriversObservable(): Observable<CableResult>
    fun ordersTakeForDriversObservable(): Observable<CableResult>
}
