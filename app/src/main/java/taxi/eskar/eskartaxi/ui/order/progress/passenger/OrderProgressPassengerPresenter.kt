package taxi.eskar.eskartaxi.ui.order.progress.passenger

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger.OrderProgressPassengerCable
import taxi.eskar.eskartaxi.data.bus.RxBus
import taxi.eskar.eskartaxi.data.bus.events.HideDriverLocationRequest
import taxi.eskar.eskartaxi.data.bus.events.HideUserLocationRequest
import taxi.eskar.eskartaxi.data.bus.events.ShowDriverLocationRequest
import taxi.eskar.eskartaxi.data.bus.events.ShowUserLocationRequest
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.*
import taxi.eskar.eskartaxi.data.repository.connection.ConnectionRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens
import timber.log.Timber

@InjectViewState
class OrderProgressPassengerPresenter(
        private val connectionRepository: ConnectionRepository,
        private val locationRepository: LocationRepository,
        private val orderProgressPassengerCable: OrderProgressPassengerCable,
        private val orderRepository: OrderRepository,
        private val profileRepository: ProfileRepository,
        private var order: Order, router: Router, private val rxBus: RxBus
) : BasePresenter<OrderProgressPassengerView>(router) {

    private var driver: Driver? = null
    private var rating: Float?  = null
    private var review: String? = null

    private var ratingDisposable: Disposable? = null

    init {
        this.subscribeToActionCable()
        this.resolveScreenState()
        this.fetchDriverAndShow()

        connectionRepository.connectionChanges()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .distinctUntilChanged()
                .subscribe(this::processConnectionChange, this::processError)
        connectionRepository.register()
    }

    private fun processConnectionChange(connected: Boolean) {
        if (connected.not()) {
            return
        }

        orderProgressPassengerCable.connect(order)
        profileRepository.getPassengerMeSplash()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    when (it) {
                        is PassengerResult.Success -> {
                            if (it.order == null) {
                                router.showSystemMessage("Ваша поездка уже закончилась")
                                router.newRootScreen(Screens.START_PASSENGER)
                            } else {
                                order = it.order
                                this.resolveScreenState()
                            }
                        }
                        else -> {
                            router.showSystemMessage("Произошла ошибка.")
                            router.newRootScreen(Screens.START_PASSENGER)
                        }
                    }
                }, this::processError)
    }


    // =============================================================================================
    // Moxy
    // =============================================================================================

    override fun onDestroy() {
        connectionRepository.unregister()
        orderProgressPassengerCable.disconnect()
        super.onDestroy()
    }


    // =============================================================================================
    // View
    // =============================================================================================

    fun onCancelOrderClicked() {
        viewState.showCancelOrderAlert()
    }

    fun onCancelOrderOk() {
        orderRepository.deleteOrder(order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    when (it) {
                        is OrderResult.Success -> {
                            this.hideDriverAndShowPassenger()
                            router.newRootScreen(Screens.START_PASSENGER)
                        }
                        is OrderResult.Fail -> {
                            router.showSystemMessage("Не удалось отменить заказ")
                            this.processError(it.throwable)
                        }
                    }
                }, this::processError)
    }

    fun onRatingChanged(rating: Float) {
        this.rating = rating
    }

    fun onReviewChanged(review: String) {
        this.review = review
    }

    fun onRatingSaveClicked() {
        this.updateRating()
    }

    fun onContinueClicked() {
        router.newRootScreen(Screens.START_PASSENGER)
    }

    fun onEndClicked() {
        router.newRootScreen(Screens.START_PASSENGER)
    }


    // =============================================================================================
    // Private
    // =============================================================================================

    private fun fetchDriverAndShow() {
        if (order.driverId == null)
            return

        profileRepository.getDriver(order.driverId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    when (it) {
                        is DriverResult.Success -> {
                            driver = it.driver
                            viewState.showDriverInfo(it.driver)
                        }
                        is DriverResult.Failure -> this.processError(it.throwable)
                    }
                }, this::processError)
    }

    private fun resolveScreenState() {
        when {
            order.timeOfClosing != null ->
                viewState.showOrderEnded(order.rating == null)
            order.timeOfStarting != null -> {
                viewState.showOrderInProgress(order.rating == null)
                rxBus.post(HideUserLocationRequest())
            }
            order.startWaitingTime != null ->
                viewState.showDriverArived()
            order.timeOfTaking != null ->
                viewState.showDriverFound()
            else -> viewState.showDriverSearch()
        }
    }

    private fun subscribeToActionCable() {

        orderProgressPassengerCable.failsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processCableFail, this::processError)

        orderProgressPassengerCable.coordinatesObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processDriverCoordinates, this::processError)

        orderProgressPassengerCable.ordersTakeObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processOrdersTake, this::processError)

        orderProgressPassengerCable.startWaitingObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processStartWaiting, this::processError)

        orderProgressPassengerCable.startDrivingObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processStartDriving, this::processError)

        orderProgressPassengerCable.ordersCloseObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processOrdersClose, this::processError)
    }


    private fun processCableFail(result: CableResult) {
        when (result) {
            is CableResult.Fail -> orderProgressPassengerCable.disconnect()
            else -> {

            }
        }
    }


    private fun processDriverCoordinates(result: CableResult) {
        Timber.i("On coordinates driver - $result")
        if (result is CableResult.Success) result.driversCoordinates?.let {
            rxBus.post(ShowDriverLocationRequest(LatLon(it[0], it[1]), true))
        }
    }

    private fun processOrdersTake(result: CableResult) {
        Timber.i("On order take - $result")
        this.updateOrderIfCan(result)
        if (result is CableResult.Success) {
            viewState.showDriverFound()
            result.driver?.let {
                viewState.showDriverInfo(it)
            }
        }
    }

    private fun processStartWaiting(result: CableResult) {
        Timber.i("On start waiting - $result")
        this.updateOrderIfCan(result)
        if (result is CableResult.Success) {
            viewState.showDriverArived()
        }
    }

    private fun processStartDriving(result: CableResult) {
        this.updateOrderIfCan(result)
        if (result is CableResult.Success) {
            viewState.showOrderInProgress(order.rating == null)
            rxBus.post(HideUserLocationRequest())
        }
    }

    private fun processOrdersClose(result: CableResult) {
        this.updateOrderIfCan(result)
        this.hideDriverAndShowPassenger()
        if (result is CableResult.Success) viewState.showOrderEnded(order.rating == null)
        orderProgressPassengerCable.disconnect()
    }


    private fun updateRating() {
        if (ratingDisposable != null) return

        orderRepository.rateOrder(order, rating?.toDouble(), review)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { ratingDisposable = it }
                .doOnEvent { _, _ -> ratingDisposable = null }
                .subscribe({
                    when (it) {
                        is OrderResult.Success -> {
                            order = it.order
                            this.resolveScreenState()
                        }
                        is OrderResult.Fail -> {
                            this.processError(it.throwable)
                        }
                    }
                }, this::processError)
    }

    private fun updateOrderIfCan(result: CableResult) {
        if (result is CableResult.Success) {
            this.order = order
        }
    }


    private fun hideDriverAndShowPassenger() {
        locationRepository.getUserLatLngLatest()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processLocationResult, this::processError)
    }

    private fun processLocationResult(result: LocationResult) {
        if (result is LocationResult.Success) {
            rxBus.post(HideDriverLocationRequest())
            rxBus.post(ShowUserLocationRequest(result.latLon, true))
        }
    }

}