package taxi.eskar.eskartaxi.ui.order.progress.driver

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.actioncable.base.ACRepository
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.model.results.OrderResult
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.data.system.Vibrator
import taxi.eskar.eskartaxi.ui.Screens
import timber.log.Timber

@InjectViewState
class OrderProgressDriverPresenter(
        private val acRepository: ACRepository,
        private val locationRepository: LocationRepository,
        private val orderRepository: OrderRepository,
        private var profileRepository: ProfileRepository,
        private val vibrator: Vibrator,
        private var order: Order, router: Router
) : BasePresenter<OrderProgressDriverView>(router) {

    private var waitOrderDisposable: Disposable? = null
    private var driveOrderDisposable: Disposable? = null

    private var passenger: Passenger? = null

    init {
        viewState.showOrder(order)
        this.fetchPassengerAndShow()
        this.resolveState()
        this.sendCoordinates()
        this.subscribeToCable()
    }

    private fun subscribeToCable() {
        order.driverId?.let {
            acRepository.coordinatesDriversSubscribeDriver(it)
                    .subscribe { Timber.i(it.toString()) }
            acRepository.connect()

        }
    }

    // =============================================================================================
    // View
    // =============================================================================================

    fun onAddressFromClicked() {
        router.navigateTo(Screens.MAP_POINT, Address(order.addressFrom,
                order.latFrom ?: .0, order.lonFrom ?: .0))
    }

    fun onAddressToClicked() {
        router.navigateTo(Screens.MAP_POINT, Address(order.addressFrom,
                order.latTo ?: .0, order.lonTo ?: .0))
    }

    fun onBuildRouteClicked() {
        router.navigateTo(Screens.MAP_ROUTE, order)
    }

    fun onCallPassengerClicked() {
        router.navigateTo(Screens.CALL, passenger?.phoneNumber)
    }

    fun onStartWaitingClicked() {
        if (waitOrderDisposable == null || waitOrderDisposable?.isDisposed == true) {
            vibrator.vibrate()
            orderRepository.waitOrder(order)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { waitOrderDisposable = it }
                    .subscribe(this::processWaitOrder, this::processError)
        }
    }

    fun onStartDrivingClicked() {
        if (driveOrderDisposable == null || driveOrderDisposable?.isDisposed == true) {
            vibrator.vibrate()
            orderRepository.startOrder(order)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { driveOrderDisposable = it }
                    .subscribe(this::processStartOrder, this::processError)
        }
    }

    fun onCloseOrderClicked() {
        vibrator.vibrate()
        router.newRootScreen(Screens.ORDER_CLOSE_DRIVER, order)
    }

    fun onRedirectConfirmed() {
        orderRepository.closeOrder(order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is OrderResult.Success -> {
                            router.newRootScreen(Screens.START_DRIVER)
                        }
                        is OrderResult.Fail -> {
                            this.processError(it.throwable)
                            router.showSystemMessage("Произошла ошибка. Повторите.")
                        }
                    }
                }, this::processError)
    }

    fun onShortClick() {
        viewState.showSystemMessage("Для изменения статуса удерживайте кнопку в теченеие некоторого времени")
    }


    // =============================================================================================
    // Private
    // =============================================================================================

    private fun fetchPassengerAndShow() {
        order.userId?.let {
            profileRepository.getPassenger(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::unsubscribeOnDestroy)
                    .doOnSubscribe { viewState.showLoading(true) }
                    .doOnEvent { _, _ -> viewState.showLoading(false) }
                    .subscribe(this::processPassengerAndShow, this::processError)
        }
    }

    private fun processPassengerAndShow(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                passenger = result.passenger
                viewState.showPassenger(result.passenger)
            }
            is PassengerResult.Failure -> {
                this.processError(result.throwable)
                viewState.showPassengerEmpty()
            }
        }
    }

    private fun resolveState() {
        when {
            order.timeOfClosing != null -> viewState.showOrderClosed()
            order.timeOfStarting != null -> viewState.showOrderStarted()
            order.startWaitingTime != null -> viewState.showOrderWaiting()
            order.timeOfTaking != null -> viewState.showOrderTaked()
        }
    }

    private fun processWaitOrder(orderResult: OrderResult) {
        when (orderResult) {
            is OrderResult.Success -> {
                this.order = orderResult.order
                viewState.showOrder(order)
                viewState.showOrderWaiting()
            }
            is OrderResult.Fail -> this.processError(orderResult.throwable)
        }
    }

    private fun processStartOrder(orderResult: OrderResult) {
        when (orderResult) {
            is OrderResult.Success -> {
                this.order = orderResult.order
                viewState.showOrder(order)
                viewState.showOrderStarted()
            }
            is OrderResult.Fail -> this.processError(orderResult.throwable)
        }
    }

    private fun sendCoordinates() {
        locationRepository.getUserLatLngUpdates()
                .doOnNext { Timber.i(it.toString()) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({ result ->
                    when (result) {
                        is LocationResult.Success -> order.driverId?.let {
                            acRepository.sendDriverLatLon(it, result.latLon)
                        }
                        is LocationResult.Fail -> this.processError(result.throwable)
                    }
                }, this::processError)
    }
}
