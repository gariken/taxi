package taxi.eskar.eskartaxi.ui.order.setup.passenger

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.ordersetup.OrderSetupInteractor
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Tariff
import taxi.eskar.eskartaxi.data.model.results.OrderResult
import taxi.eskar.eskartaxi.data.model.results.PaymentTypesResult
import taxi.eskar.eskartaxi.data.model.results.PreliminaryResult
import taxi.eskar.eskartaxi.data.model.results.TariffsResult
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class OrderSetupPresenter @Inject constructor(
        private val interactor: OrderSetupInteractor,
        private val messageResource: MessageResource, router: Router
) : BasePresenter<OrderSetupView>(router) {

    private val tariffs = mutableListOf<Tariff>()
    private var tariffN = 0

    private var confirmOrderDisposable: Disposable? = null
    private var fetchLocationDisposable: Disposable? = null
    private var orderPriceDisposable: Disposable? = null

    init {
        // Init with data
        this.fetchTariffsAndShow()

        // Init result listeners
        this.onEditResultFrom()
        this.onEditResultTo()
        this.onAddCommentsResult()
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================

    override fun attachView(view: OrderSetupView) {
        super.attachView(view)
        viewState.showOrder(interactor.order())
        viewState.showPaymentType(interactor.paymentType())
        this.updatePrice()
    }

    override fun onDestroy() {
        router.removeResultListener(Results.ORDER_DETAILS_ADDRESS_TYPING_FROM)
        router.removeResultListener(Results.ORDER_DETAILS_ADDRESS_TYPING_TO)
        router.removeResultListener(Results.ORDER_DETAILS_COMMENTS)
        super.onDestroy()
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    fun onFromClicked() {
        if (canSendRequest())
            router.navigateTo(Screens.ADDRESS_TYPING, Results.ORDER_DETAILS_ADDRESS_TYPING_FROM)
    }

    fun onToClicked() {
        if (canSendRequest())
            router.navigateTo(Screens.ADDRESS_TYPING, Results.ORDER_DETAILS_ADDRESS_TYPING_TO)
    }

    fun onPlanSelected(pos: Int) {
        tariffN = pos
    }

    fun onAddCommentsClicked() {
        router.navigateTo(Screens.ORDER_SETUP_PASSENGER_COMMENTS, interactor.order())
    }

    fun onPaymentTypeClicked() {
        interactor.fetchPaymentTypes()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processPaymentTypes, this::processError)
    }

    fun onPaymentTypeSelected(id: Int, title: String) {
        viewState.showPaymentType(interactor.changePaymentType(id, title))
    }

    fun onConfirmClicked() {
        if (!interactor.order().canBePriced()) return

        if (confirmOrderDisposable == null) {
            this.createOrder()
        }
    }


    // =============================================================================================
    //   Private
    // =============================================================================================

    private fun fetchTariffsAndShow() {
        interactor.fetchTariffs()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processTariffs, this::processError)
    }

    private fun processTariffs(result: TariffsResult) {
        when (result) {
            is TariffsResult.Success -> {
                tariffs.clear()
                tariffs.addAll(result.tariffs)
                viewState.showTariffs(result.tariffs, tariffN)
            }
            is TariffsResult.Fail -> {
                this.processError(result.throwable)
                router.showSystemMessage("Произошла ошибка загрузки тарифов. Перезагрузите приложение")
            }
        }
    }

    private fun processPaymentTypes(result: PaymentTypesResult) {
        if (result is PaymentTypesResult.Success) {
            Timber.i(result.types.toString())
            viewState.showPaymentTypes(result.types)
        }
    }

    private fun updatePrice() {
        if (interactor.order().canBePriced()) {
            orderPriceDisposable?.dispose()

            interactor.updatePrice()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::unsubscribeOnDestroy)
                    .doOnSubscribe { orderPriceDisposable = it }
                    .doOnEvent { _, _ -> orderPriceDisposable = null }
                    .subscribe(this::onPreliminaryResult, this::processError)
        }
    }

    private fun onPreliminaryResult(result: PreliminaryResult) {
        when (result) {
            is PreliminaryResult.Success -> {
                tariffs.clear()
                tariffs.addAll(result.tariffs)
                viewState.showTariffs(tariffs, tariffN)
            }
            is PreliminaryResult.Fail -> this.processError(result.throwable)
        }
    }


    private fun onEditResultFrom() {
        router.setResultListener(Results.ORDER_DETAILS_ADDRESS_TYPING_FROM) {
            val address = it as Address
            viewState.showOrder(interactor.updateSourceAddress(address))
            this.updatePrice()
        }
    }


    private fun onEditResultTo() {
        router.setResultListener(Results.ORDER_DETAILS_ADDRESS_TYPING_TO) {
            val address = it as Address
            viewState.showOrder(interactor.updateTargetAddress(address))
            this.updatePrice()
        }
    }


    private fun onAddCommentsResult() {
        router.setResultListener(Results.ORDER_DETAILS_COMMENTS) {
            val order = it as Order
            viewState.showOrder(interactor.updateComments(order))
        }
    }


    private fun createOrder() {
        if (interactor.order().canBePriced())
            interactor.createOrder(tariffs[tariffN])
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::unsubscribeOnDestroy)
                    .doOnSubscribe { confirmOrderDisposable = it }
                    .doOnSubscribe { viewState.showLoading(true) }
                    .doOnEvent { _, _ -> confirmOrderDisposable = null }
                    .doOnEvent { _, _ -> viewState.showLoading(false) }
                    .subscribe(this::processCreateOrder, this::processError)
    }

    private fun processCreateOrder(result: OrderResult) {
        Timber.i(result.toString())
        when (result) {
            is OrderResult.Success ->
                router.newRootScreen(Screens.ORDER_PROGRESS_PASSENGER, result.order)
            is OrderResult.NoMoney -> viewState.showSystemMessage(result.message)
            is OrderResult.Fail -> this.processError(result.throwable)
            is OrderResult.UnknownStatusCode ->
                viewState.showSystemMessage(messageResource.error(result.statusCode))
        }
    }


    private fun canSendRequest() = confirmOrderDisposable == null
            && orderPriceDisposable == null
            && fetchLocationDisposable == null
}