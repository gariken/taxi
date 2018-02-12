package taxi.eskar.eskartaxi.ui.address.typing

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.results.AddressesResult
import taxi.eskar.eskartaxi.data.model.results.LocationResult
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import java.util.concurrent.TimeUnit

@InjectViewState
class AddressTypingPresenter constructor(
        private val addressRepository: AddressRepository,
        private val locationRepository: LocationRepository,
        private val resultCode: Int, router: Router
) : BasePresenter<AddressTypingView>(router) {

    private val queryChanges = PublishRelay.create<String>()

    init {
        this.resolveUiVisibility()
        this.subscribeToQueryChanges()
        router.setResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP, {
            this.onAddressResult(it as Address)
        })
        router.setResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP_FROM, {
            this.onAddressResult(it as Address)
        })
        router.setResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP_TO, {
            this.onAddressResult(it as Address)
        })
        router.setResultListener(Results.ADDRESS_TYPING_FAVORITED, {
            this.onAddressResult(it as Address)
        })
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    override fun onDestroy() {
        router.removeResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP)
        router.removeResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP_FROM)
        router.removeResultListener(Results.ADDRESS_TYPING_SELECT_ON_MAP_TO)
        router.removeResultListener(Results.ADDRESS_TYPING_FAVORITED)
        super.onDestroy()
    }

    // =============================================================================================
    //   View
    // =============================================================================================
    fun onFavoritedAddressesClicked() {
        if (resultCode == Results.FAVORITED_ADDRESS_TYPING_OTHERS
                || resultCode == Results.FAVORITED_ADDRESS_TYPING_WORK
                || resultCode == Results.FAVORITED_ADDRESS_TYPING_HOME) {
            viewState.showNoFavsFromFavsAlert()
        } else {
            router.navigateTo(Screens.FAVORITED_ADDRESSES_PASSENGER, Results.ADDRESS_TYPING_FAVORITED)
        }
    }

    fun onSelectOnMapClicked() {
        val code = when (resultCode) {
            Results.ORDER_DETAILS_ADDRESS_TYPING_FROM -> Results.ADDRESS_TYPING_SELECT_ON_MAP_FROM
            Results.ORDER_DETAILS_ADDRESS_TYPING_TO -> Results.ADDRESS_TYPING_SELECT_ON_MAP_TO
            Results.START_PASSENGER_ADDRESS_TYPING -> Results.ADDRESS_TYPING_SELECT_ON_MAP_TO
            else -> Results.ADDRESS_TYPING_SELECT_ON_MAP
        }
        router.navigateTo(Screens.ADDRESS_SELECTING, code)
    }

    fun onQueryChanged(query: String) {
        queryChanges.accept(query)
    }


    fun onSuggestionClicked(address: Address) {
        this.onAddressResult(address)
    }

    // =============================================================================================
    //   Private
    // =============================================================================================
    private fun resolveUiVisibility() {
        val isFromFavs = resultCode == Results.FAVORITED_ADDRESS_TYPING_HOME
                || resultCode == Results.FAVORITED_ADDRESS_TYPING_WORK
                || resultCode == Results.FAVORITED_ADDRESS_TYPING_OTHERS
        viewState.showButtonsEnabled(!isFromFavs)
    }

    private fun subscribeToQueryChanges() {
        queryChanges
                .doOnNext { viewState.showLoading(true) }
                .debounce(150, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .switchMapSingle(this::queryToSuggestion)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processSuggestions)
    }

    private fun queryToSuggestion(query: String): Single<AddressesResult> {
        return locationRepository.getUserLatLng()
                .flatMap { result ->
                    when (result) {
                        is LocationResult.Success ->
                            addressRepository.getSuggestionsFor(query, result.latLon)
                                    .subscribeOn(Schedulers.io())
                        is LocationResult.Fail ->
                            addressRepository.getSuggestionsFor(query, null)
                                    .subscribeOn(Schedulers.io())
                    }
                }
    }

    private fun processSuggestions(result: AddressesResult) {
        when (result) {
            is AddressesResult.Success -> {
                if (result.addresses.isEmpty()) {
                    viewState.showSuggestionsEmpty()
                } else {
                    viewState.showSuggestions(result.addresses)
                }
            }
            is AddressesResult.Fail -> {
                this.processError(result.throwable)
                router.showSystemMessage(result.throwable.message)
            }
        }
    }

    private fun onAddressResult(address: Address) {
        router.exitWithResult(resultCode, address)
    }
}