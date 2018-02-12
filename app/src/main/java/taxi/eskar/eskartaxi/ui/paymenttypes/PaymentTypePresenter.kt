package taxi.eskar.eskartaxi.ui.paymenttypes

import com.arellomobile.mvp.InjectViewState
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.results.PaymentResult
import taxi.eskar.eskartaxi.data.model.results.PaymentsResult
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class PaymentTypePresenter(
        private val paymentRepository: PaymentRepository,
        router: Router
) : BasePresenter<PaymentTypeView>(router) {

    private var setPaymentTypeDisposable: Disposable? = null

    init {
        this.fetchPaymentTypes()
    }

    // View
    fun onPaymentClick(pos: Int, paymentType: PaymentType) {
        if (setPaymentTypeDisposable != null) {
            viewState.showPaymentTypeChangeInProcess()
            return
        }

        paymentRepository.setPaymentType(paymentType)
                .doOnSubscribe { setPaymentTypeDisposable = it }
                .doOnSuccess { setPaymentTypeDisposable = null }
                .subscribe({ this.processPaymentResult(pos, it) }) { }
    }

    fun onBindCardClicked() {
        router.navigateTo(Screens.CARD_BINDING)
    }


    // Private
    private fun fetchPaymentTypes() {
        paymentRepository.getPaymentTypes()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processPaymentTypes)
    }

    private fun processPaymentTypes(result: PaymentsResult) {
        when (result) {
            is PaymentsResult.Success -> {
                if (result.types.isNotEmpty()) {
                    viewState.showPaymentTypes(result.types)
                } else {

                }
            }
            is PaymentsResult.Fail -> {
                // todo handle fails
            }
        }
    }

    private fun processPaymentResult(pos: Int, result: PaymentResult) {
        when (result) {
            is PaymentResult.Success -> {
                viewState.setSelectedType(pos, result.type)
            }
            is PaymentResult.Fail -> {
                // todo handle fails
            }
        }
    }
}