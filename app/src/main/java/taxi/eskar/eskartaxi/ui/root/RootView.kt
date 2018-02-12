package taxi.eskar.eskartaxi.ui.root

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.LatLon

interface RootView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showUserLocation(latLon: LatLon?, focus: Boolean = false)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showDriverLocation(latLon: LatLon?, focus: Boolean = false)
}