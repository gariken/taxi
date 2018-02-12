package taxi.eskar.eskartaxi.base

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BaseView : MvpView {
    fun bind() {}

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoading(show: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showSystemMessage(message: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideKeyboard()
}