package taxi.eskar.eskartaxi.ui.favoritedaddresses

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Address

interface FavoritedAddressesView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeAddress(address: Address)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHomeAddressEmpty()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showWorkAddress(address: Address)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showWorkAddressEmpty()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFvrtAddresses(addresses: List<Address>)

    // =============================================================================================
    //   Dialogs
    // =============================================================================================

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showReadModeOnlyAlert()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteOtherAddressAlert()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteHomeAddressAlert()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteWorkAddressAlert()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showButtonsEnabled(homeEnabled: Boolean, workEnabled: Boolean, othersEnabled: Boolean, addOthersEnabled: Boolean)
}