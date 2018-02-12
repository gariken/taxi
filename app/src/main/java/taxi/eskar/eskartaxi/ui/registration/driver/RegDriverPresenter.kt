package taxi.eskar.eskartaxi.ui.registration.driver

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
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class RegDriverPresenter(
        private val profileRepository: ProfileRepository,
        private val driver: Driver, router: Router
) : BasePresenter<RegDriverView>(router) {

    private var uploadPhotoDisposable: Disposable? = null
    private var uploadLicenseDisposable: Disposable? = null
    private var updateDriverDisposable: Disposable? = null

    init {
        viewState.showDriver(driver)
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    fun onFirstNameChanged(firstName: String) {
        driver.name = firstName
    }

    fun onLastNameChanged(lastName: String) {
        driver.surname = lastName
    }

    fun onUploadPhotoClicked() {
        if (updateDriverDisposable != null || uploadPhotoDisposable != null) {
            return
        }

        viewState.showPhotoPicker()
    }

    fun onCarModelChanged(carModel: String) {
        driver.carModel = carModel
    }

    fun onCarColorChanged(carColor: String) {
        driver.carColor = carColor
    }

    fun onLicensePlateChaged(licensePlate: String) {
        driver.licencePlate = licensePlate
    }

    fun onUploadLicenseClicked() {
        if (updateDriverDisposable != null || uploadPhotoDisposable != null || uploadLicenseDisposable != null) {
            return
        }

        viewState.showLicensePicker()
    }

    fun onPhotoResult(uri: Uri) {
        if (updateDriverDisposable != null || uploadPhotoDisposable != null || uploadLicenseDisposable != null) {
            return
        }

        profileRepository.uploadPhotoDriver(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe {
                    uploadPhotoDisposable = it
                    viewState.showLoading(true)
                }
                .doOnEvent { _, _ ->
                    uploadPhotoDisposable = null
                    viewState.showLoading(false)
                }
                .subscribe({
                    when (it) {
                        is DriverResult.Success -> {
                            this.driver.photo = it.driver.photo
                            viewState.showPhotoUploaded()
                        }
                        is DriverResult.Failure -> {
                            this.processError(it.throwable)
                            router.showSystemMessage("Ошибка загрузки фотографии")
                            this.processError(it.throwable)
                        }
                    }
                }, this::processError)
    }

    fun onLicenseResult(uri: Uri) {
        if (updateDriverDisposable != null || uploadPhotoDisposable != null || uploadLicenseDisposable != null) {
            return
        }

        profileRepository.uploadLicenseDriver(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe {
                    uploadPhotoDisposable = it
                    viewState.showLoading(true)
                }
                .doOnEvent { _, _ ->
                    uploadPhotoDisposable = null
                    viewState.showLoading(false)
                }
                .subscribe({
                    when (it) {
                        is DriverResult.Success -> {
                            this.driver.license = it.driver.license
                            viewState.showLicenseUploaded()
                        }
                        is DriverResult.Failure -> {
                            this.processError(it.throwable)
                            router.showSystemMessage("Ошибка загрузки фотографии")
                            this.processError(it.throwable)
                        }
                    }
                }, this::processError)
    }

    fun onSaveClicked() {
        if (updateDriverDisposable != null || uploadPhotoDisposable != null || uploadLicenseDisposable != null) {
            return
        }

        if (driver.isNotRegistered()) {
            router.showSystemMessage("Необходимо заполнить все поля и загрузить ВУ")
            return
        }

        profileRepository.updateDriver(driver)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { updateDriverDisposable = it }
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> updateDriverDisposable = null }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processUpdateDriverResult, this::processError)
    }

    fun onSignOutClicked() {
        profileRepository.signOutDriver()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe({ router.newRootScreen(Screens.AUTH_PHONE) }, this::processError)
    }


    // =============================================================================================
    //   Private
    // =============================================================================================
    private fun processUpdateDriverResult(result: DriverResult) {
        when (result) {
            is DriverResult.Success -> {
                router.newRootScreen(Screens.SPLASH)
            }
            is DriverResult.Failure -> {
                router.showSystemMessage(result.throwable.message)
                this.processError(result.throwable)
            }
        }
    }
}