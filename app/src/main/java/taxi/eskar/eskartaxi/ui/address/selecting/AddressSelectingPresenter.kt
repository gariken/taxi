package taxi.eskar.eskartaxi.ui.address.selecting

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.AddressResult
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import java.util.concurrent.TimeUnit

@InjectViewState
class AddressSelectingPresenter constructor(
        private val addressRepository: AddressRepository,
        private val locationRepository: LocationRepository,
        private val resultCode: Int, router: Router
) : BasePresenter<AddressSelectingView>(router) {

    private val locationRequests = PublishRelay.create<Unit>()

    private val latLngChanges = PublishRelay.create<LatLon>()
    private val addressChanges = PublishRelay.create<Address>()
    private val selectRequests = PublishRelay.create<Unit>()

    private val unitAddressToAddress = BiFunction<Unit, Address, Address> { _, address -> address }

    init {
        this.subscribeToLocationRequests()
        this.subscribeToLatLngChanges()
        this.subscribeToSelectRequests()
    }

    fun onMapReady() {
        this.fetchLatestUserLocation()
    }

    fun onMapScrolled(latLon: LatLon) {
        latLngChanges.accept(latLon)
    }

    fun onLocationClicked() {
        locationRequests.accept(Unit)
    }

    fun onSelectClicked() {
        selectRequests.accept(Unit)
    }

    private fun fetchLatestUserLocation() {
        locationRepository.getUserLatLngLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processLocationResult, this::processError)
    }

    private fun processLocationResult(result: LocationResult) {
        when (result) {
            is LocationResult.Success -> {
                this.onMapScrolled(result.latLon)
                viewState.showUserLocation(result.latLon)
            }
            is LocationResult.Fail -> {

            }
        }

    }

    private fun subscribeToLocationRequests() {
        locationRequests
                .subscribeOn(Schedulers.io())
                .flatMapSingle { locationRepository.getUserLatLng() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processLocationResult, this::processError)
    }

    private fun subscribeToLatLngChanges() {
        latLngChanges
                .doOnNext { viewState.showLoading(true) }
                .observeOn(Schedulers.io())
                .debounce(150, TimeUnit.MILLISECONDS)
                .switchMapSingle(this::latLngToAddress)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .subscribe(this::processAddressResults, this::processError)
    }

    private fun latLngToAddress(latLon: LatLon): Single<AddressResult> {
        return addressRepository.getAddressFor(latLon)
    }

    private fun subscribeToSelectRequests() {
        selectRequests.withLatestFrom(addressChanges, unitAddressToAddress)
                .subscribe({ router.exitWithResult(resultCode, it) }, this::processError)
    }

    private fun processAddressResults(result: AddressResult) {
        when (result) {
            is AddressResult.Success -> {
                addressChanges.accept(result.address)
                viewState.showAddress(result.address)
            }
            is AddressResult.Fail -> {

            }
        }
    }
}