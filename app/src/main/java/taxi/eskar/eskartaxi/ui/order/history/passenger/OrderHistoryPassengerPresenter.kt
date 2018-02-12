package taxi.eskar.eskartaxi.ui.order.history.passenger

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.OrdersResult
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class OrderHistoryPassengerPresenter @Inject constructor(
        private val orderRepository: OrderRepository, router: Router
) : BasePresenter<OrderHistoryPassengerView>(router) {

    init {
        this.fetchAllOrders()
    }


    fun onOrderClicked(pos: Int, order: Order) {
        router.navigateTo(Screens.ORDER_HISTORY_DETAILS_PASSENGER, order)
    }


    fun onReloadClicked() {
        this.fetchAllOrders()
    }

    // Private
    private fun fetchAllOrders() {
        orderRepository.getOrdersHistoryPassenger()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processAllOrdersResult)
    }

    private fun processAllOrdersResult(result: OrdersResult) {
        when (result) {
            is OrdersResult.Success -> {
                if (result.orders.isNotEmpty()) {
                    viewState.showOrders(result.orders)
                } else {
                    viewState.showOrdersEmpty()
                }
            }
            is OrdersResult.Fail -> {
                viewState.showOrdersError()
            }
        }
    }
}