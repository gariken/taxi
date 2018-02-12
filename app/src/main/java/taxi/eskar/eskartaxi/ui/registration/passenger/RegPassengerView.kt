package taxi.eskar.eskartaxi.ui.registration.passenger

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex

interface RegPassengerView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAllSex(sexList: List<Sex>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPassenger(passenger: Passenger)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setSelectedSex(pos: Int, sex: Sex)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoPicker()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPhotoUploaded()
}