package taxi.eskar.eskartaxi.ui.start.passenger

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.startpassenger.StartPassengerInteractor
import taxi.eskar.eskartaxi.data.bus.RxBus
import taxi.eskar.eskartaxi.data.bus.events.MapScrolledEvent
import taxi.eskar.eskartaxi.data.bus.events.ShowUserLocationRequest
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.AddressResult
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class StartPassengerPresenter @Inject constructor(
        private val interactor: StartPassengerInteractor,
        private val locationRepository: LocationRepository,
        router: Router, private val rxBus: RxBus
) : BasePresenter<StartPassengerView>(router) {

    private val latLonRelay = PublishRelay.create<LatLon>()

    init {
        router.setResultListener(Results.START_PASSENGER_ADDRESS_TYPING) {
            interactor.updateSourceAddress(it as Address)
            viewState.showOrderDetails()
        }

        latLonRelay
                .doOnNext { viewState.showLoading(true) }
                .debounce(150, TimeUnit.MILLISECONDS)
                .switchMapSingle(interactor::latLonToAddress)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processAddressResult, this::processError)

        rxBus.events()
                .ofType(MapScrolledEvent::class.java)
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .map(MapScrolledEvent::latLon)
                .subscribe(this::onMapScrolled, this::processError)

        this.onMapReady()
    }

    fun onProfileClicked() {
        router.navigateTo(Screens.PROFILE_PASSENGER)
    }

    fun onLocationClicked() {
        locationRepository.getUserLatLng()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processLocationResultLatest, this::processError)
    }

    fun onWhereToClicked() {
        router.navigateTo(Screens.ADDRESS_TYPING, Results.START_PASSENGER_ADDRESS_TYPING)
    }


    private fun onMapReady() {
        locationRepository.getUserLatLng()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processLocationResultLatest, this::processError)
    }

    private fun onMapScrolled(latLon: LatLon) {
        latLonRelay.accept(latLon)
    }

    private fun processAddressResult(result: AddressResult) {
        if (result is AddressResult.Success) {
            viewState.showAddressFrom(result.address)
        } else if (result is AddressResult.Fail) {
            this.processError(result.throwable)
        }
    }

    private fun processLocationResultLatest(result: LocationResult) {
        if (result is LocationResult.Success) {
            latLonRelay.accept(result.latLon)
            rxBus.post(ShowUserLocationRequest(result.latLon, true))
        } else if (result is LocationResult.Fail) {
            viewState.showSystemMessage("Местоположение недоступно")
        }
    }
}