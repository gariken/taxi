package taxi.eskar.eskartaxi.ui.splash

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView

interface SplashView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBannedError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showIOError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLocationError(show: Boolean)
}