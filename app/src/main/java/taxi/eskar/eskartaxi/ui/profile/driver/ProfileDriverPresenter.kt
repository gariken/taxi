package taxi.eskar.eskartaxi.ui.profile.driver

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class ProfileDriverPresenter @Inject constructor(
        private val profileRepository: ProfileRepository,
        router: Router
) : BasePresenter<ProfileDriverView>(router) {

    private var avatarUploadDisposable: Disposable? = null
    private var avatarDeleteDisposable: Disposable? = null

    private var driver: Driver? = null


    init {
        this.fetchProfile()

        router.setResultListener(Results.PROFILE_EDIT_SAVED_DRIVER, {
            viewState.showProfile(it as Driver)
        })
    }


    // View
    fun onPhotoClicked() {
        if (avatarUploadDisposable != null || avatarDeleteDisposable != null)
            return

        if (driver == null)
            return

        if (driver!!.photo.exists()) {
            viewState.showPhotoDialog()
        } else viewState.showPhotoPicker()
    }

    fun onPhotoResult(uri: Uri) {
        if (avatarUploadDisposable == null) {
            profileRepository.uploadPhotoDriver(uri)
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

    fun onDeletePhotoClicked() {
        if (avatarDeleteDisposable == null) {
            profileRepository.deletePhotoDriver()
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

    fun onEditProfileClicked() {
        router.navigateTo(Screens.PROFILE_DRIVER_EDIT)
    }

    fun onOrderHistory() {
        router.navigateTo(Screens.ORDER_HISTORY_DRIVER)
    }

    fun onShareClicked() {
        router.navigateTo(Screens.SHARE)
    }

    fun onSignOutClicked() {
        profileRepository.signOutDriver()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({ router.newRootScreen(Screens.SPLASH) }, this::processError)
    }


    // Private
    private fun fetchProfile() {
        profileRepository.getDriverMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processProfileResult)
    }

    private fun processProfileResult(result: DriverResult) {
        when (result) {
            is DriverResult.Success -> {
                driver = result.driver
                viewState.showProfile(result.driver)
            }
            is DriverResult.Failure -> {
                router.showSystemMessage(result.throwable.message)
            }
        }
    }
}