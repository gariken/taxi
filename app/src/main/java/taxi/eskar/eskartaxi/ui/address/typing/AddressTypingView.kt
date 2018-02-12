package taxi.eskar.eskartaxi.ui.address.typing

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Address

interface AddressTypingView : BaseView {

    fun showRecents(addresses: List<Address>)
    fun showRecentsEmpty()
    fun showSuggestions(addresses: List<Address>)
    fun showSuggestionsEmpty()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNoFavsFromFavsAlert()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showButtonsEnabled(showFavsButton: Boolean)
}