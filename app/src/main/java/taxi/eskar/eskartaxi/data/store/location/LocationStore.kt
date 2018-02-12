package taxi.eskar.eskartaxi.data.store.location

import taxi.eskar.eskartaxi.data.model.LatLon

interface LocationStore {
    fun getLocation(): LatLon
    fun hasLocation(): Boolean
    fun putLocation(latLon: LatLon)
    fun removeLocation()
}