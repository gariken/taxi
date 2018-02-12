package taxi.eskar.eskartaxi.ui.profile.passenger

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.data.store.payment.PaymentStore
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class ProfilePassengerPresenter @Inject constructor(
        private val profileRepository: ProfileRepository,
        private val paymentStore: PaymentStore, router: Router
) : BasePresenter<ProfilePassengerView>(router) {

    private var avatarUploadDisposable: Disposable? = null
    private var avatarDeleteDisposable: Disposable? = null

    private var passenger: Passenger? = null


    init {
        this.fetchProfile()

        router.setResultListener(Results.PROFILE_EDIT_SAVED_PASSENGER, {
            viewState.showProfile(it as Passenger)
        })
    }


    // View

    fun onPhotoClicked() {
        if (avatarUploadDisposable != null || avatarDeleteDisposable != null)
            return

        if (passenger == null)
            return

        if (passenger!!.photo.exists()) {
            viewState.showPhotoDialog()
        } else {
            viewState.showPhotoPicker()
        }
    }

    fun onDeletePhotoOk() {
        if (avatarDeleteDisposable == null) {
            profileRepository.deletePhotoPassenger()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::unsubscribeOnDestroy)
                    .doOnSubscribe {
                        avatarDeleteDisposable = it
                        viewState.showLoading(true)
                    }
                    .doOnEvent { _, _ ->
                        avatarDeleteDisposable = null
                        viewState.showLoading(false)
                    }
                    .subscribe(this::processProfileResult, this::processError)
        }

    }

    fun onPhotoResult(uri: Uri) {
        if (avatarUploadDisposable == null) {
            profileRepository.uploadPhotoPassenger(uri)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::unsubscribeOnDestroy)
                    .doOnSubscribe {
                        avatarUploadDisposable = it
                        viewState.showLoading(true)
                    }
                    .doOnEvent { _, _ ->
                        avatarUploadDisposable = null
                        viewState.showLoading(false)
                    }
                    .subscribe(this::processProfileResult, this::processError)
        }
    }

    fun onEditProfileClicked() {
        router.navigateTo(Screens.PROFILE_PASSENGER_EDIT)
    }

    fun onFavoritedAddressesClicked() {
        router.navigateTo(Screens.FAVORITED_ADDRESSES_PASSENGER, 0)
    }

    fun onCards() {
        router.navigateTo(Screens.CARDS)
    }

    fun onDebtsClicked() {
        router.navigateTo(Screens.DEBTS)
    }

    fun onOrderHistory() {
        router.navigateTo(Screens.ORDER_HISTORY_PASSENGER)
    }

    fun onShareClicked() {
        router.navigateTo(Screens.SHARE)
    }

    fun onSignOutClicked() {
        profileRepository.signOutPassenger()
                .doOnSuccess { paymentStore.clear() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({ router.newRootScreen(Screens.SPLASH) }, this::processError)
    }

    // Private
    private fun fetchProfile() {
        profileRepository.getPassengerMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processProfileResult)
    }

    private fun processProfileResult(result: PassengerResult) {
        when (result) {
            is PassengerResult.Success -> {
                passenger = result.passenger
                viewState.showProfile(result.passenger)
            }
            is PassengerResult.Failure -> {
                this.processError(result.throwable)
                router.showSystemMessage(result.throwable.message)
            }
        }
    }
}