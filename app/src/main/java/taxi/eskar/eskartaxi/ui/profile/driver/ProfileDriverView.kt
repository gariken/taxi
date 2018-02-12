package taxi.eskar.eskartaxi.ui.profile.driver

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Driver

interface ProfileDriverView : BaseView {
    fun showProfile(driver: Driver)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoPicker()
}