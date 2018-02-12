package taxi.eskar.eskartaxi.data.repository.address

import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.AddressResult
import taxi.eskar.eskartaxi.data.model.results.AddressesResult
import taxi.eskar.eskartaxi.data.model.results.LocationResult

interface AddressRepository {

    fun getRecents(): Single<AddressesResult>

    fun getAddressFor(latLon: LatLon): Single<AddressResult>
    fun getSuggestionsFor(query: String, latLon: LatLon?): Single<AddressesResult>
    fun getLatLonFor(address: String): Single<LocationResult>
}