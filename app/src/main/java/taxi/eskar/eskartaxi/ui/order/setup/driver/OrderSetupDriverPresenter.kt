package taxi.eskar.eskartaxi.ui.order.setup.driver

import com.arellomobile.mvp.InjectViewState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.actioncable.startdriver.StartDriverCable
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.results.CableResult
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.model.results.OrderResult
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens
import timber.log.Timber

@InjectViewState
class OrderSetupDriverPresenter constructor(
        private val startDriverCable: StartDriverCable,
        private val orderRepository: OrderRepository,
        private val profileRepository: ProfileRepository,
        private val order: Order, router: Router
) : BasePresenter<OrderSetupDriverView>(router) {

    private var passenger: Passenger? = null
    private var takeOderInProgress = false

    init {
        viewState.showOrder(order)
        this.fetchPassengerAndShow()
        this.fetchDriverAndSubscribeToCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        startDriverCable.disconnect()
    }


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

    fun onCommentsClicked() {
        viewState.showComment(order)
    }

    fun onTakeClicked() {
        if (takeOderInProgress) return

        this.fetchDriverAndTakeOrder()
    }


    // =============================================================================================
    // Private
    // =============================================================================================

    private fun fetchPassengerAndShow() {
        profileRepository.getPassenger(order.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showPassengerLoading(true) }
                .doOnEvent { _, _ -> viewState.showPassengerLoading(false) }
                .subscribe(this::processPassengerResult, this::processError)
    }

    private fun processPassengerResult(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                passenger = result.passenger
                viewState.showPassenger(result.passenger)
            }
            is PassengerResult.Failure -> {
                Timber.e(result.throwable)
                viewState.showPassengerEmpty()
            }
        }
    }


    private fun fetchDriverAndTakeOrder() {
        profileRepository.getDriverMe()
                .subscribeOn(Schedulers.io())
                .flatMap(this::driverResulToOrderResult)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { takeOderInProgress = true }
                .doOnEvent { _, _ -> takeOderInProgress = false }
                .subscribe(this::processTakeResult, this::processError)
    }

    private fun driverResulToOrderResult(result: DriverResult): Single<OrderResult> {
        return when (result) {
            is DriverResult.Success -> {
                orderRepository.takeOrder(order, result.driver)
            }
            is DriverResult.Failure -> {
                Single.just(OrderResult.fail(result.throwable))
            }
            else -> Single.error(RuntimeException("Unexpected result is $result"))
        }
    }

    private fun processTakeResult(result: OrderResult) {
        when (result) {
            is OrderResult.Success -> {
                router.newRootScreen(Screens.ORDER_PROGRESS_DRIVER, result.order)
            }
            is OrderResult.LowBalance -> {
                router.exitWithMessage("Ваш баланс ниже нуля. Вы не можете взять этот заказ.")
            }
            is OrderResult.UnknownStatusCode -> {
                router.exitWithMessage("Произошла ошибка. Вы не можете принять этот заказ.")
            }
            is OrderResult.Fail -> {
                router.exitWithMessage("Произошла ошибка. Вы не можете принять этот заказ.")
            }
        }
    }


    private fun fetchDriverAndSubscribeToCancel() {
        profileRepository.getDriverMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::subscribeToCancel, this::processError)
    }

    private fun subscribeToCancel(result: DriverResult) {
        when (result) {
            is DriverResult.Success -> {
                startDriverCable.ordersCancelForDriversObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::unsubscribeOnDestroy)
                        .subscribe({
                            if (it is CableResult.Success) {
                                if (order.id == it.order?.id) {
                                    router.exitWithMessage("Кто-то взял эту заявку или ее отменили")
                                }
                            }
                        }, this::processError)

                startDriverCable.connect(result.driver)
            }
            is DriverResult.Failure -> this.processError(result.throwable)
        }
    }
}