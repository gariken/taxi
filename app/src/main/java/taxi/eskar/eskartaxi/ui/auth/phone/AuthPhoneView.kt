package taxi.eskar.eskartaxi.ui.auth.phone

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView

interface AuthPhoneView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showButtonsEnabled(maskFilled: Boolean)
}