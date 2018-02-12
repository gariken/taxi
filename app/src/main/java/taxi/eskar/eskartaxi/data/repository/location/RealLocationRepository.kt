package taxi.eskar.eskartaxi.data.repository.location

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.Observable
import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.store.location.LocationStore
import java.util.concurrent.TimeUnit


class RealLocationRepository(
        private val locationStore: LocationStore,
        private val rxLocation: RxLocation
) : LocationRepository {

    companion object {
        private val TIMEOUT_UNIT = TimeUnit.MILLISECONDS
        private val TIMEOUT = 5000.toLong()
    }

    private val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(500)


    override fun getUserLatLng(): Single<LocationResult> {
        return try {
            rxLocation.location().updates(locationRequest).firstOrError()
                    .map { LatLon(it.latitude, it.longitude) }
                    .doOnSuccess(locationStore::putLocation)
                    .map { LocationResult.success(it) }
                    .timeout(TIMEOUT, TIMEOUT_UNIT, this.locationProvidersUnavailableSingle())
                    .onErrorReturn { LocationResult.fail(it) }
        } catch (e: SecurityException) {
            Single.just(LocationResult.fail(e))
        }
    }

    override fun getUserLatLngLatest(): Single<LocationResult> {
        return if (locationStore.hasLocation()) {
            Single.just(locationStore.getLocation())
                    .map { LocationResult.success(it) }
                    .timeout(TIMEOUT, TIMEOUT_UNIT, this.locationProvidersUnavailableSingle())
                    .onErrorReturn { LocationResult.fail(it) }
        } else this.getUserLatLng()
    }

    override fun getUserLatLngUpdates(): Observable<LocationResult> {
        return try {
            rxLocation.location().updates(locationRequest)
                    .map { LatLon(it.latitude, it.longitude) }
                    .doOnNext(locationStore::putLocation)
                    .map { LocationResult.success(it) }
                    .timeout(TIMEOUT, TIMEOUT_UNIT, this.locationProvidersUnavailableObservable())
                    .onErrorReturn { LocationResult.fail(it) }
        } catch (e: SecurityException) {
            Observable.just(LocationResult.fail(e))
        }
    }


    private fun locationProvidersUnavailableSingle() =
            Single.just(LocationResult.fail(LocationProvidersUnavailableException()))

    private fun locationProvidersUnavailableObservable() =
            Observable.just(LocationResult.fail(LocationProvidersUnavailableException()))
}