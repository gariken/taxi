package taxi.eskar.eskartaxi.data.bus

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import taxi.eskar.eskartaxi.data.bus.events.Event


class RxBus {

    private val bus = PublishRelay.create<Event>()

    fun post(event: Event) {
        bus.accept(event)
    }

    fun events(): Observable<Event> =
            bus.hide()

}