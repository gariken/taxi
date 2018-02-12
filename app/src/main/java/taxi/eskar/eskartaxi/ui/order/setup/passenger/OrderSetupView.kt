package taxi.eskar.eskartaxi.ui.order.setup.passenger

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.Tariff

interface OrderSetupView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showOrder(order: Order)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTariffs(tariffs: List<Tariff>, tariffN: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun dismiss()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPaymentTypes(types: List<PaymentType>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPaymentType(type: PaymentType)
}