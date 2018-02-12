package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.LatLon

sealed class LocationResult {

    companion object {
        fun success(latLon: LatLon): LocationResult = Success(latLon)
        fun fail(throwable: Throwable): LocationResult = Fail(throwable)
    }

    data class Success(val latLon: LatLon) : LocationResult()
    data class Fail(val throwable: Throwable) : LocationResult()
}