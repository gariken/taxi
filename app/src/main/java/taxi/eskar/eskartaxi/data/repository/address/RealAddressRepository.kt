package taxi.eskar.eskartaxi.data.repository.address

import io.reactivex.Observable
import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.responses.YandexSuggestionResponse.Response.GeoObjectCollection.FeatureMember
import taxi.eskar.eskartaxi.data.model.results.AddressResult
import taxi.eskar.eskartaxi.data.model.results.AddressesResult
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import taxi.eskar.eskartaxi.data.retrofit.YandexApi
import timber.log.Timber

class RealAddressRepository(
        private val eskarApi: EskarApi,
        private val yandexApi: YandexApi
) : AddressRepository {

    private val recentAddresses = mutableListOf(
            Address("RecentAddress#1"),
            Address("RecentAddress#2"),
            Address("RecentAddress#3"),
            Address("RecentAddress#4")
    )

    override fun getRecents(): Single<AddressesResult> {
        return Single.just(recentAddresses).map { AddressesResult.success(it) }
    }

    override fun getAddressFor(latLon: LatLon): Single<AddressResult> {
        return yandexApi.getSuggestions("${latLon.lon},${latLon.lat}", null, 1)
                .map { it.response.geoObjectCollection.featureMember[0] }
                .map(this::yandexObjectToAddress)
                .map { AddressResult.success(it) }
                .onErrorReturn { AddressResult.fail(it) }
    }

    override fun getSuggestionsFor(query: String, latLon: LatLon?): Single<AddressesResult> {
        return yandexApi.getSuggestions(query, "${latLon?.let { "${it.lon},${it.lat}" }}", 10)
                .flatMapObservable { Observable.fromIterable(it.response.geoObjectCollection.featureMember) }
                .map(this::yandexObjectToAddress).toList()
                .map { AddressesResult.success(it) }
                .onErrorReturn { AddressesResult.fail(it) }
    }

    private fun yandexObjectToAddress(obj: FeatureMember): Address {
        val point = obj.geoObject.point.pos.split(" ")
        return Address(obj.geoObject.metadataProperty.geocoderMetaData.addressDetails.country.addressLine,
                point[1].toDouble(), point[0].toDouble(), obj.geoObject.name)
    }

    override fun getLatLonFor(address: String): Single<LocationResult> {
        return eskarApi.getLatLonFor(address)
                .doOnSuccess { Timber.i(it.toString()) }
                .map { LocationResult.success(LatLon(it.data.lat(), it.data.lon())) }
                .onErrorReturn { LocationResult.fail(it) }
    }
}