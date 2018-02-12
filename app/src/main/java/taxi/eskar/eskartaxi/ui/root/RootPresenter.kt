package taxi.eskar.eskartaxi.ui.root

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.bus.RxBus
import taxi.eskar.eskartaxi.data.bus.events.*
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class RootPresenter @Inject constructor(
        private val rxBus: RxBus, router: Router
) : BasePresenter<RootView>(router) {

    init {
        this.subscribeToShowUserLocation()
        this.subscribeToHideUserLocation()
        this.subscribeToShowDriverLocation()
        this.subscribeToHideDriverLocation()

        router.setResultListener(Results.ORDER_DETAILS_ORDER_PROGRESS) {
            router.newRootScreen(Screens.ORDER_PROGRESS_PASSENGER, it)
        }
    }

    override fun onDestroy() {
        router.removeResultListener(Results.ORDER_DETAILS_ORDER_PROGRESS)
        super.onDestroy()
    }


    // View

    fun onMapReady() {
        rxBus.post(MapReadyEvent())
    }

    fun onMapScrolled(latLon: LatLon) {
        rxBus.post(MapScrolledEvent(latLon))
    }


    // Private
    private fun subscribeToShowUserLocation() {
        rxBus.events().ofType(ShowUserLocationRequest::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::proccessShowUserRequest)
    }

    private fun proccessShowUserRequest(request: ShowUserLocationRequest) {
        viewState.showUserLocation(request.latLon, request.focus)
    }

    private fun subscribeToHideUserLocation() {
        rxBus.events().ofType(HideUserLocationRequest::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({ this.proccessHideUserRequest() }, this::processError)
    }

    private fun proccessHideUserRequest() {
        viewState.showUserLocation(null)
    }

    private fun subscribeToShowDriverLocation() {
        rxBus.events().ofType(ShowDriverLocationRequest::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processShowDriversLocation)
    }

    private fun processShowDriversLocation(request: ShowDriverLocationRequest) {
        viewState.showDriverLocation(request.latLon, request.focus)
    }


    private fun subscribeToHideDriverLocation() {
        rxBus.events().ofType(HideDriverLocationRequest::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({ this.processHideDriversLocation() }, this::processError)
    }

    private fun processHideDriversLocation() {
        viewState.showDriverLocation(null)
    }
}