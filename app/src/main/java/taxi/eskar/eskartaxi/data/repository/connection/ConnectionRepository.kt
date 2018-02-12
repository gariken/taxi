package taxi.eskar.eskartaxi.data.repository.connection

import io.reactivex.Observable

interface ConnectionRepository {
    fun register()
    fun connectionChanges(): Observable<Boolean>
    fun unregister()
}