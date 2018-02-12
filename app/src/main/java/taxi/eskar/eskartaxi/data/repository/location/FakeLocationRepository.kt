package taxi.eskar.eskartaxi.data.repository.location

import io.reactivex.Observable
import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import javax.inject.Inject

class FakeLocationRepository @Inject constructor(

) : LocationRepository {

    override fun getUserLatLng(): Single<LocationResult> = Single
            .just(LatLon(55.765196, 37.568439))
            .map { LocationResult.success(it) }
            .onErrorReturn { LocationResult.fail(it) }

    override fun getUserLatLngLatest(): Single<LocationResult> = Single
            .just(LatLon(55.765196, 37.568439))
            .map { LocationResult.success(it) }
            .onErrorReturn { LocationResult.fail(it) }

    override fun getUserLatLngUpdates(): Observable<LocationResult> = Observable
            .just(LatLon(55.765196, 37.568439))
            .map { LocationResult.success(it) }
            .onErrorReturn { LocationResult.fail(it) }
}