package taxi.eskar.eskartaxi.ui.start.driver

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.actioncable.startdriver.StartDriverCable
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.CableResult
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.model.results.OrdersResult
import taxi.eskar.eskartaxi.data.repository.connection.ConnectionRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class StartDriverPresenter @Inject constructor(
        private val startDriverCable: StartDriverCable,
        private val connectionRepository: ConnectionRepository,
        private val orderRepository: OrderRepository,
        private var profileRepository: ProfileRepository,
        router: Router
) : BasePresenter<StartDriverView>(router) {

    private val orders = mutableListOf<Order>()

    init {
        this.subscribeToActionCable()
        this.subscribeToConnectionChanges()
    }

    override fun onDestroy() {
        connectionRepository.unregister()
        startDriverCable.disconnect()
        super.onDestroy()
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    fun onOrderClicked(order: Order) {
        router.navigateTo(Screens.ORDER_SETUP_DRIVER, order)
    }

    fun onProfileClicked() {
        router.navigateTo(Screens.PROFILE_DRIVER)
    }

    fun onReloadClicked() {
        this.fetchAllOrders()
    }


    // =============================================================================================
    //   Private
    // =============================================================================================

    private fun subscribeToActionCable() {
        startDriverCable.failsObservable()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processCableFail, this::processError)

        startDriverCable.ordersObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    if (it is CableResult.Success) {
                        it.order?.let { orders.add(0, it) }
                        viewState.showOrders(orders)
                    }
                }, this::processError)

        startDriverCable.ordersTakeForDriversObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processOrderRemove, this::processError)

        startDriverCable.ordersCancelForDriversObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processOrderRemove, this::processError)
    }

    private fun subscribeToConnectionChanges() {
        connectionRepository.connectionChanges()
                .distinctUntilChanged()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::proccessConectionChange, this::processError)

        connectionRepository.register()
    }

    private fun proccessConectionChange(connected: Boolean) {
        if (connected.not()) {
            return
        }

        this.fetchDriverAndSubscribeToNewOrders()
        this.fetchAllOrders()

    }

    private fun fetchAllOrders() {
        orderRepository.getAllNewOrdersDriver()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnSubscribe { viewState.showOrdersEmpty(false) }
                .doOnSubscribe { viewState.showUnconfirmedError(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processAllOrdersResult)
    }

    private fun processAllOrdersResult(result: OrdersResult) {
        when (result) {
            is OrdersResult.Success -> {
                if (result.orders.isNotEmpty()) {
                    orders.clear()
                    orders.addAll(result.orders)
                    viewState.showOrders(orders)
                } else {
                    viewState.showOrdersEmpty(true)
                }
            }
            is OrdersResult.Unconfirmed -> viewState.showUnconfirmedError(true)
            is OrdersResult.Fail -> viewState.showOrdersEmpty(true)
        }
    }

    private fun fetchDriverAndSubscribeToNewOrders() {
        profileRepository.getDriverMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::subscribeToNewOrders, this::processError)
    }

    private fun subscribeToNewOrders(driverResult: DriverResult) {
        when (driverResult) {
            is DriverResult.Success -> startDriverCable.connect(driverResult.driver)
            is DriverResult.Failure -> this.processError(driverResult.throwable)
        }
    }

    private fun processCableFail(result: CableResult) {
        when (result) {
            is CableResult.Fail -> startDriverCable.disconnect()
            else -> {

            }
        }
    }

    private fun processOrderRemove(result: CableResult) {
        if (result is CableResult.Success) {
            orders.removeAll(orders.asSequence().filter { it.id == result.order?.id })
            if (orders.isEmpty()) {
                viewState.showOrdersEmpty(true)
            } else {
                viewState.showOrders(orders)
            }
        }
    }
}