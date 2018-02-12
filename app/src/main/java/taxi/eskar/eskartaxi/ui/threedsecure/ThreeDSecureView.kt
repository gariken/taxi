package taxi.eskar.eskartaxi.ui.threedsecure

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView

interface ThreeDSecureView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun loadPage(url: String, postData: String = "")

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showWebView(show: Boolean)
}