package taxi.eskar.eskartaxi.ui.profile.passenger

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Passenger

interface ProfilePassengerView : BaseView {
    fun showProfile(passenger: Passenger)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoPicker()
}