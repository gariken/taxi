package taxi.eskar.eskartaxi.ui.auth.code

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView

interface AuthCodeView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showButtonsEnabled(enabled: Boolean)
}