package taxi.eskar.eskartaxi.data.repository.location

import io.reactivex.Observable
import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.results.LocationResult

interface LocationRepository {

    /**
     * @return Single with newest user's location
     */
    fun getUserLatLng(): Single<LocationResult>

    /**
     * @return Single with latest user's location. Can be retrieved from cache or actual location.
     */
    fun getUserLatLngLatest(): Single<LocationResult>

    /**
     * @return Observable emitting newes user's location.
     */
    fun getUserLatLngUpdates(): Observable<LocationResult>
}