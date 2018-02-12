package taxi.eskar.eskartaxi.ui.order.close.driver

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.OrderResult
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.data.resources.StringResource
import taxi.eskar.eskartaxi.data.system.Vibrator
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class OrderCloseDriverPresenter(
        private val messageResource: MessageResource,
        private val orderRepository: OrderRepository,
        private val vibrator: Vibrator,
        private val order: Order,
        stringResource: StringResource, router: Router
) : BasePresenter<OrderCloseDriverView>(router) {

    init {
        viewState.showOrder(order)
        viewState.showPaymentMethod(stringResource.paymentMethod(order.paymentMethod.orEmpty()))

        when (order.paymentMethod) {
            "cash" -> viewState.showCashButtons()
            "cashless" -> viewState.showCashlessButtons()
        }
    }

    fun onPaymentPositiveClicked(): Boolean {
        this.close(true)
        return true
    }

    fun onPaymentNegativeClicked(): Boolean {
        this.close(false)
        return true
    }

    fun onContinueClicked(): Boolean {
        this.close(null)
        return true
    }

    private fun close(payed: Boolean?) {
        vibrator.vibrate()
        orderRepository.closeOrder(order, payed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processCloseOrder, this::processError)
    }

    private fun processCloseOrder(orderResult: OrderResult) {
        when (orderResult) {
            is OrderResult.Success -> router.newRootScreen(Screens.START_DRIVER)
            is OrderResult.Fail -> viewState.showSystemMessage(messageResource.orderClosingError())
        }
    }

}