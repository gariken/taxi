package taxi.eskar.eskartaxi.ui.order.close.driver

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Order

interface OrderCloseDriverView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class) fun showOrder(order: Order)
    @StateStrategyType(AddToEndSingleStrategy::class) fun showPaymentMethod(paymentMethod: String)
    @StateStrategyType(AddToEndSingleStrategy::class) fun showCashButtons()
    @StateStrategyType(AddToEndSingleStrategy::class) fun showCashlessButtons()
}