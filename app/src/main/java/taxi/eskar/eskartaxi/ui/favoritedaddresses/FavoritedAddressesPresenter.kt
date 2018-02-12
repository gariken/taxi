package taxi.eskar.eskartaxi.ui.favoritedaddresses

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.FavoriteAddresses
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class FavoritedAddressesPresenter constructor(
        private val profileRepository: ProfileRepository,
        private val resultCode: Int, router: Router
) : BasePresenter<FavoritedAddressesView>(router) {

    private var passenger: Passenger? = null
    private var otherAddressToDelete: Address? = null

    init {
        this.fetchAddresses()
        router.setResultListener(Results.FAVORITED_ADDRESS_TYPING_HOME) { address ->
            if (passenger == null) {
                return@setResultListener
            } else this.updateAddresses(passenger!!.apply {
                addresses().home = address as Address
            })
        }
        router.setResultListener(Results.FAVORITED_ADDRESS_TYPING_WORK) { address ->
            if (passenger == null) {
                return@setResultListener
            } else this.updateAddresses(passenger!!.apply {
                addresses().work = address as Address
            })
        }
        router.setResultListener(Results.FAVORITED_ADDRESS_TYPING_OTHERS) { address ->
            if (passenger == null) {
                return@setResultListener
            } else this.updateAddresses(passenger!!.apply {
                addresses().others().add(address as Address)
            })
        }
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    fun onHomeClicked() {
        if (passenger == null)
            return

        val p = passenger!!

        if (resultCode == 0) {
            if (p.addresses().home == null) {
                router.navigateTo(Screens.ADDRESS_TYPING, Results.FAVORITED_ADDRESS_TYPING_HOME)
            } else {
                viewState.showDeleteHomeAddressAlert()
            }
        } else {
            if (p.addresses().home == null) {
                viewState.showReadModeOnlyAlert()
            } else {
                router.exitWithResult(resultCode, p.addresses().home)
            }
        }
    }

    fun onDeleteHomeOk() {
        if (passenger == null) {
            return
        } else {
            this.updateAddresses(passenger!!.apply { addresses().home = null })
        }
    }


    fun onWorkClicked() {
        if (passenger == null)
            return

        val p = passenger!!

        if (resultCode == 0) {
            if (p.addresses().work == null) {
                router.navigateTo(Screens.ADDRESS_TYPING, Results.FAVORITED_ADDRESS_TYPING_WORK)
            } else {
                viewState.showDeleteWorkAddressAlert()
            }
        } else {
            if (p.addresses().work == null) {
                viewState.showReadModeOnlyAlert()
            } else {
                router.exitWithResult(resultCode, p.addresses().work)
            }
        }
    }

    fun onDeleteWorkOk() {
        if (passenger == null) {
            return
        } else {
            this.updateAddresses(passenger!!.apply { addresses().work = null })
        }
    }


    fun onAddressClicked(address: Address) {
        if (resultCode == 0) {
            otherAddressToDelete = address
            viewState.showDeleteOtherAddressAlert()
        } else {
            router.exitWithResult(resultCode, address)
        }
    }

    fun onDeleteOtherOk() {
        if (passenger == null) {
            return
        } else this.updateAddresses(passenger!!.apply {
            otherAddressToDelete?.let { addresses().others().remove(it) }
        })
    }

    fun onAddNewClicked() {
        if (resultCode != 0) {
            viewState.showReadModeOnlyAlert()
        } else {
            router.navigateTo(Screens.ADDRESS_TYPING, Results.FAVORITED_ADDRESS_TYPING_OTHERS)
        }
    }


    // =============================================================================================
    //   Private
    // =============================================================================================
    private fun fetchAddresses() {
        profileRepository.getPassengerMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processAddresses, this::processError)
    }

    private fun updateAddresses(passenger: Passenger) {
        profileRepository
                .updatePassenger(passenger)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processAddresses, this::processError)
    }

    private fun processAddresses(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                passenger = result.passenger
                if (result.passenger.addresses().home == null) {
                    viewState.showHomeAddressEmpty()
                } else {
                    viewState.showHomeAddress(result.passenger.addresses().home!!)
                }

                if (result.passenger.addresses().work == null) {
                    viewState.showWorkAddressEmpty()
                } else {
                    viewState.showWorkAddress(result.passenger.addresses().work!!)
                }

                viewState.showFvrtAddresses(result.passenger.addresses().others())

                this.resolveUiVisibility(result.passenger.addresses())
            }
            is PassengerResult.Failure -> {
                this.processError(result.throwable)
                router.exitWithMessage("Произошла ошибка при загрузке данных.")
            }
        }
    }

    private fun resolveUiVisibility(addresses: FavoriteAddresses) {
        if (resultCode == 0) {
            viewState.showButtonsEnabled(true, true, true, true)
        } else {
            val homeEnabled = addresses.home != null
            val workEnabled = addresses.work != null

            viewState.showButtonsEnabled(homeEnabled, workEnabled, addresses.others().isNotEmpty(), false)
        }
    }
}