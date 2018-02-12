package taxi.eskar.eskartaxi.business.startpassenger

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.AddressResult
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.store.order.OrderStore
import javax.inject.Inject

class StartPassengerInteractor @Inject constructor(
        private val addressRepository: AddressRepository,
        private val locationRepository: LocationRepository,
        private val orderStore: OrderStore
) {

    fun latLonToAddress(latLon: LatLon): Single<AddressResult> {
        return addressRepository.getAddressFor(latLon)
                .doOnSuccess(this::updateOrder)
                .subscribeOn(Schedulers.io())
    }

    fun updateSourceAddress(address: Address) {
        orderStore.updateAndGet {
            it.apply { this.setAddressTo(address) }
        }
    }

    private fun updateOrder(result: AddressResult) {
        if (result is AddressResult.Success) {
            orderStore.update {
                it.addressFrom = result.address.title
                it.latFrom = result.address.lat
                it.lonFrom = result.address.lon
                it
            }
        }
    }

}