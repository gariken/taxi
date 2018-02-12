package taxi.eskar.eskartaxi.ui.registration.driver

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Driver

interface RegDriverView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriver(driver: Driver)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPhotoPicker()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPhotoUploaded()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLicensePicker()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLicenseUploaded()

}