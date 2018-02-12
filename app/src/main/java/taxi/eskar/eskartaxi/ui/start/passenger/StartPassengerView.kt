package taxi.eskar.eskartaxi.ui.start.passenger

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Address

interface StartPassengerView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAddressFrom(address: Address)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOrderDetails()
}